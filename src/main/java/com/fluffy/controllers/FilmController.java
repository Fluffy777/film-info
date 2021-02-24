package com.fluffy.controllers;

import com.fluffy.dtos.GetFilmDataRequest;
import com.fluffy.exceptions.FilmServiceException;
import com.fluffy.services.FilmService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Клас контролера, який надає API для взаємодії між користувачем та веб-
 * сервером. Призначений для опрацювання запитів на отримання даних про фільм
 * у форматах JSON, XML та docx.
 * @author Сивоконь Вадим
 */
@RestController
public class FilmController {
    /**
     * Для забезпечення логування.
     */
    private static final Logger logger = Logger.getLogger(FilmController.class);

    /**
     * Бін відповідного сервіса, який займається виконанням бізнес-логіки.
     */
    private final FilmService service;

    /**
     * Бін, необхідний для отримання налаштувань із application.yml.
     */
    private final Environment env;

    /**
     * Максимальна кількість збережених результатів запитів на отримання даних
     * про фільмиу форматах JSON або XML.
     */
    private final int cacheResponseBodiesMaxSize;

    /**
     * Максимальна кількість збережених результатів запитів на отримання
     * документа про фільм у форматі docx.
     */
    private final int cacheDocumentsMaxSize;

    /**
     * Основний тип документа-результата, який повертається на відповідний
     * запит.
     */
    private final String resultDocumentMimeType;

    /**
     * Підтип докумета-результата, який повертається на відповідний запит.
     */
    private final String resultDocumentMimeSubtype;

    /**
     * Назва документа-результата, який повертається на відповідний запит.
     */
    private final String resultDocumentFilename;

    /**
     * Поточний об'єм кешу responseBodies.
     */
    private int cacheResponseBodiesCounter = 0;

    /**
     * Поточний об'єм кешу documents.
     */
    private int cacheDocumentsCounter = 0;

    /**
     * Метод, що викликається для очищення вмісту кешу responseBodies.
     */
    @CacheEvict(value = "responseBodies", allEntries = true)
    public void evictCachedResponseBodies() {
        cacheResponseBodiesCounter = 0;
    }

    /**
     * Метод, що викликається для очищення вмісту кешу documents.
     */
    @CacheEvict(value = "documents", allEntries = true)
    public void evictCachedDocuments() {
        cacheDocumentsCounter = 0;
    }

    /**
     * Створює бін контролера.
     * @param service бін відповідного сервіса
     * @param env бін для отримання значень налаштувань
     */
    public FilmController(final FilmService service, final Environment env) {
        this.service = service;
        this.env = env;
        cacheResponseBodiesMaxSize = env.getProperty("application.cache.cache-response-bodies-max-size", int.class);
        cacheDocumentsMaxSize = env.getProperty("application.cache.cache-documents-max-size", int.class);
        resultDocumentMimeType = env.getProperty("application.result-document.mime-type");
        resultDocumentMimeSubtype = env.getProperty("application.result-document.mime-subtype");
        resultDocumentFilename = env.getProperty("application.result-document.filename");
        logger.debug("Бін REST-контролера " + getClass().getSimpleName() + " створений");
    }

    /**
     * Повертає результат опрацювання вхідних даних користувача відповідним
     * сервісом, що інкапсулює в собі бізнес-логіку. Підтримує кешування
     * запитів.
     *
     * Параметри запиту вказані як необов'язкові для забезпечення можливості
     * подальшої ручної перевірки вхідних значень. Наприклад, значення
     * параметрів title та id повинні бути вказані в запиті, але не одночасно.
     * @param title назва фільму
     * @param year рік випуску
     * @param plot тип співпадіння
     * @param id IMDb ID
     * @param format формат відповіді
     * @return відповідь у необхідному форматі
     */
    @GetMapping(value = "${application.endpoint.film-data}")
    @Cacheable("responseBodies")
    public ResponseEntity<?> filmData(@RequestParam(name = "${application.api.param.title}", required = false) final String title,
                                      @RequestParam(name = "${application.api.param.year}", required = false) final String year,
                                      @RequestParam(name = "${application.api.param.plot}", required = false) final String plot,
                                      @RequestParam(name = "${application.api.param.id}", required = false) final String id,
                                      @RequestParam(name = "${application.api.param.format}", required = false) final String format) throws FilmServiceException, ExecutionException, InterruptedException {
        logger.debug(String.format("Параметри запиту: title = %s, year = %s, plot = %s, id = %s, format = %s", title, year, plot, id, format));
        if (cacheResponseBodiesCounter >= cacheResponseBodiesMaxSize) {
            evictCachedResponseBodies();
            logger.debug("Кеш запитів на отримання даних про фільми очищений");
        }
        CompletableFuture<String> futureBody = service.getRequestBody(
                new GetFilmDataRequest(title, year, plot, id, format));
        CompletableFuture.allOf(futureBody).join();
        logger.info(String.format("Запит на отримання інформації про фільм у форматі %s опрацьований", (format != null) ? format : env.getProperty("application.response-data-format.json")));

        ++cacheResponseBodiesCounter;
        return ResponseEntity
                .ok()
                .contentType(format == null || format.isEmpty() || format.equals(env.getProperty("application.response-data-format.json")) ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_XML)
                .body(futureBody.get());
    }

    /**
     * Повертає результат опрацювання вхідних даних користувача відповідним
     * сервісом, що інкапсулює в собі бізнес-логіку. Підтримує кешування
     * запитів.
     *
     * Параметри запиту вказані як необов'язкові для забезпечення можливості
     * подальшої ручної перевірки вхідних значень. Наприклад, значення
     * параметрів title та id повинні бути вказані в запиті, але не одночасно.
     * @param title назва фільму
     * @param year рік випуску
     * @param plot тип співпадіння
     * @param id IMDb ID
     * @param format формат відповіді
     * @param response відповідь
     * @return відповідь у необхідному форматі (docx)
     */
    @GetMapping(value = "${application.endpoint.film-document}", params = "format=docx")
    @Cacheable("documents")
    public ResponseEntity<StreamingResponseBody> filmDocument(@RequestParam(name = "${application.api.param.title}", required = false) final String title,
                                                              @RequestParam(name = "${application.api.param.year}", required = false) final String year,
                                                              @RequestParam(name = "${application.api.param.plot}", required = false) final String plot,
                                                              @RequestParam(name = "${application.api.param.id}", required = false) final String id,
                                                              @RequestParam(name = "${application.api.param.format}", required = false) final String format, final HttpServletResponse response) {
        logger.debug(String.format("Параметри запиту: title = %s, year = %s, plot = %s, id = %s, format = %s", title, year, plot, id, format));
        if (cacheDocumentsCounter >= cacheDocumentsMaxSize) {
            evictCachedDocuments();
            logger.debug("Кеш запитів на отримання документів про фільми очищений");
        }

        StreamingResponseBody stream = out -> {
            try (OutputStream writer = new DataOutputStream(response.getOutputStream())) {
                CompletableFuture<XWPFDocument> futureDoc = service.getDocument(new GetFilmDataRequest(title, year, plot, id, env.getProperty("application.response-data-format.json")));
                CompletableFuture.allOf(futureDoc).join();
                futureDoc.get().write(writer);
            } catch (InterruptedException | ExecutionException | FilmServiceException e) {
                logger.log(Level.ERROR, "Не вдалося здійснити потокове надсилання документа клієнту", e);
            }
        };
        logger.info("Запит на отримання документа про фільм опрацьований");

        ++cacheDocumentsCounter;
        return ResponseEntity
                .ok()
                .contentType(new MediaType(resultDocumentMimeType, resultDocumentMimeSubtype))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + resultDocumentFilename)
                .body(stream);
    }
}
