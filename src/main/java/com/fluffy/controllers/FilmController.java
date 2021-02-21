package com.fluffy.controllers;

import com.fluffy.dtos.GetFilmDataRequest;
import com.fluffy.exceptions.FilmServiceException;
import com.fluffy.services.FilmService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlException;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Клас контролера, який надає API для взаємодії між користувачем та веб-
 * сервером. Призначений для опрацювання запитів на отримання даних про фільм
 * у форматі JSON або XML.
 * @author Сивоконь Вадим
 */
@RestController
public class FilmController {
    /**
     * Відповідний сервіс, що займається виконанням бізнес-логіки.
     */
    private final FilmService service;

    private final int CACHE_RESPONSE_BODIES_MAX_SIZE;
    private final int CACHE_DOCUMENTS_MAX_SIZE;

    private final Environment env;

    private int cacheResponseBodiesCounter = 0;
    private int cacheDocumentsCounter = 0;

    @CacheEvict(value = "responseBodies", allEntries = true)
    public void evictCachedResponseBodies() {
        cacheResponseBodiesCounter = 0;
    }

    @CacheEvict(value = "documents", allEntries = true)
    public void evictCachedDocuments() {
        cacheDocumentsCounter = 0;
    }

    /**
     * Створює об'єкт (бін) контролера.
     * @param service відповідний сервіс
     */
    public FilmController(FilmService service, Environment env) {
        this.service = service;
        this.env = env;

        CACHE_RESPONSE_BODIES_MAX_SIZE = env.getProperty("application.cache.cache-response-bodies-max-size", int.class);
        CACHE_DOCUMENTS_MAX_SIZE = env.getProperty("application.cache.cache-documents-max-size", int.class);
    }

    /**
     * Повертає JSON - результат опрацювання вхідних даних користувача
     * відповідним сервісом, що інкапсулює в собі бізнес-логіку.
     *
     * Параметри запиту вказані як необов'язкові для забезпечення можливості
     * подальшої ручної перевірки вхідних значень. Наприклад, значення
     * параметрів title та id повинні бути вказані в запиті, але не одночасно.
     * @param title назва фільму
     * @param year рік випуску
     * @param plot тип співпадіння
     * @param id IMDb ID
     * @param format формат відповіді
     * @return відповідь у необхідному форматі (JSON)
     */
    @GetMapping(value = "/film")
    @Cacheable("responseBodies")
    public ResponseEntity<?> filmData(@RequestParam(name = "${application.api.param.title}", required = false) String title,
                                  @RequestParam(name = "${application.api.param.year}", required = false) String year,
                                  @RequestParam(name = "${application.api.param.plot}", required = false) String plot,
                                  @RequestParam(name = "${application.api.param.id}", required = false) String id,
                                  @RequestParam(name = "${application.api.param.format}", required = false) String format) throws FilmServiceException, ExecutionException, InterruptedException {
        if (cacheResponseBodiesCounter >= CACHE_RESPONSE_BODIES_MAX_SIZE) {
            evictCachedResponseBodies();
        }

        CompletableFuture<String> futureBody = service.getRequestBody(new GetFilmDataRequest(title, year, plot, id, format));
        CompletableFuture.allOf(futureBody).join();

        ++cacheResponseBodiesCounter;
        if (format.equals("json")) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(futureBody.get());
        } else {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(futureBody.get());
        }
    }

    /**
     * Повертає файл - результат опрацювання вхідних даних користувача
     * відповідним сервісом, що інкапсулює в собі бізнес-логіку.
     *
     * Параметри запиту вказані як необов'язкові для забезпечення можливості
     * подальшої ручної перевірки вхідних значень. Наприклад, значення
     * параметрів title та id повинні бути вказані в запиті, але не одночасно.
     * @param title назва фільму
     * @param year рік випуску
     * @param plot тип співпадіння
     * @param id IMDb ID
     * @param format формат відповіді
     * @return відповідь у необхідному форматі (docx)
     */
    @GetMapping(value = "/film", params = "format=docx")
    @Cacheable("documents")
    public ResponseEntity<StreamingResponseBody> filmDocument(@RequestParam(name = "${application.api.param.title}", required = false) String title,
                                                        @RequestParam(name = "${application.api.param.year}", required = false) String year,
                                                        @RequestParam(name = "${application.api.param.plot}", required = false) String plot,
                                                        @RequestParam(name = "${application.api.param.id}", required = false) String id,
                                                        @RequestParam(name = "${application.api.param.format}", required = false) String format, HttpServletResponse response) throws FilmServiceException, FileNotFoundException, URISyntaxException, InterruptedException {
        if (cacheDocumentsCounter >= CACHE_DOCUMENTS_MAX_SIZE) {
            evictCachedDocuments();
        }

        StreamingResponseBody stream = out -> {
            try (OutputStream writer = new DataOutputStream(response.getOutputStream())) {
                CompletableFuture<XWPFDocument> futureDoc = service.getDocument(new GetFilmDataRequest(title, year, plot, id, "json"));

                // для очікування завершення виконання декількох потоків (у цьому випадку - одного)
                CompletableFuture.allOf(futureDoc).join();

                // документ згенерований і може бути отриманий
                XWPFDocument doc = futureDoc.get();

                doc.write(writer);
            } catch (IOException | URISyntaxException | FilmServiceException | XmlException e) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        };

        ++cacheDocumentsCounter;
        return ResponseEntity
                .ok()
                .contentType(new MediaType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=sample.docx")
                .body(stream);
    }
}
