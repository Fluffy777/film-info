package com.fluffy.services;

import com.fluffy.dtos.FilmDTO;
import com.fluffy.dtos.GetFilmDataRequest;
import com.fluffy.dtos.ImageDTO;
import com.fluffy.exceptions.FilmServiceException;
import com.fluffy.exceptions.NoDataFoundException;
import com.fluffy.exceptions.RedundantRequestParamException;
import com.fluffy.exceptions.RequestParamInvalidValueException;
import com.fluffy.exceptions.RequestParamOmitedException;
import com.fluffy.exceptions.RequestParamsConflictException;
import com.fluffy.util.RequestParamMapper;
import com.fluffy.util.URLBuilder;
import com.fluffy.util.XWPFDocumentManipulator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Клас сервісу. Інкапсулює в собі бізнес-логіку, необхідну до виконання над
 * вхідними даними.
 * @author Сивоконь Вадим
 */
@Service
public class FilmService {
    /**
     * Для забезпечення логування.
     */
    private static final Logger logger = Logger.getLogger(FilmService.class);

    /**
     * Протокол для отримання даних від джерела.
     */
    private final String dataSourceProtocol;

    /**
     * Доменне ім'я джерела та порт (опціонально).
     */
    private final String dataSourceHost;

    /**
     * Ключ до API джерела даних.
     */
    private final String dataSourceApiKey;

    /**
     * Ключ для вказання назви фільму (для джерела даних).
     */
    private final String dataSourceQueryKeyTitle;

    /**
     * Ключ для вказання року випуску (для джерела даних).
     */
    private final String dataSourceQueryKeyYear;

    /**
     * Ключ для вказання режиму відображення сюжету (для джерела даних).
     */
    private final String dataSourceQueryKeyPlot;

    /**
     * Ключ для вказання формату відповіді (для джерела даних).
     */
    private final String dataSourceQueryKeyFormat;

    /**
     * Ключ для вказання IMDb ID (для джерела даних).
     */
    private final String dataSourceQueryKeyId;

    /**
     * Ключ для вказання ключа до API (для джерела даних).
     */
    private final String dataSourceQueryKeyApiKey;

    /**
     * Протокол для отримання даних від додатка.
     */
    private final String applicationProtocol;

    /**
     * Доменне ім'я сервера та порт (опціонально).
     */
    private final String applicationHost;

    /**
     * Ключ для вказання назви фільму (для додатку).
     */
    private final String applicationQueryKeyTitle;

    /**
     * Ключ для вказання року випуску (для додатку).
     */
    private final String applicationQueryKeyYear;

    /**
     * Ключ для вказання режиму відображення сюжету (для додатку).
     */
    private final String applicationQueryKeyPlot;

    /**
     * Ключ для вказання формату відповіді (для додатку).
     */
    private final String applicationQueryKeyFormat;

    /**
     * Ключ для вказання IMDb ID (для додатку).
     */
    private final String applicationQueryKeyId;

    /**
     * Назва поля JSON-об'єкта, що зберігає результат опрацювання запиту.
     */
    private final String jsonKeyResponse;

    /**
     * Назва поля JSON-об'єкта, що зберігає тип об'єкта.
     */
    private final String jsonKeyType;

    /**
     * Назва поля JSON-об'єкта, що зберігає назву фільму.
     */
    private final String jsonKeyTitle;

    /**
     * Назва поля JSON-об'єкта, що зберігає рік випуску фільму.
     */
    private final String jsonKeyYear;

    /**
     * Назва поля JSON-об'єкта, що зберігає IMDb ID.
     */
    private final String jsonKeyImdbId;

    /**
     * Назва поля JSON-об'єкта, що зберігає рейтинг вмісту.
     */
    private final String jsonKeyRated;

    /**
     * Назва поля JSON-об'єкта, що зберігає тривалість фільму.
     */
    private final String jsonKeyRuntime;

    /**
     * Назва поля JSON-об'єкта, що зберігає жанр фільму.
     */
    private final String jsonKeyGenre;

    /**
     * Назва поля JSON-об'єкта, що зберігає дату випуску.
     */
    private final String jsonKeyReleased;

    /**
     * Назва поля JSON-об'єкта, що зберігає сюжет.
     */
    private final String jsonKeyPlot;

    /**
     * Назва поля JSON-об'єкта, що зберігає URL-посилання на постер.
     */
    private final String jsonKeyPoster;

    /**
     * Назва поля JSON-об'єкта, що зберігає директора.
     */
    private final String jsonKeyDirector;

    /**
     * Назва поля JSON-об'єкта, що зберігає сценариста.
     */
    private final String jsonKeyWriter;

    /**
     * Назва поля JSON-об'єкта, що зберігає акторів.
     */
    private final String jsonKeyActors;

    /**
     * Назва поля JSON-об'єкта, що зберігає мову фільму.
     */
    private final String jsonKeyLanguage;

    /**
     * Назва поля JSON-об'єкта, що зберігає країну-виробника фільму.
     */
    private final String jsonKeyCountry;

    /**
     * Назва поля JSON-об'єкта, що зберігає отримані нагороди.
     */
    private final String jsonKeyAwards;

    /**
     * Назва поля JSON-об'єкта, що зберігає студію - виробника фільму.
     */
    private final String jsonKeyProduction;

    /**
     * Назва поля JSON-об'єкта, що зберігає касові збори.
     */
    private final String jsonKeyBoxOffice;

    /**
     * Назва поля JSON-об'єкта, що зберігає рейтинг від www.metacritic.com.
     */
    private final String jsonKeyMetascore;

    /**
     * Назва поля JSON-об'єкта, що зберігає рейтинг від www.imdb.com.
     */
    private final String jsonKeyImdbRating;

    /**
     * Назва поля JSON-об'єкта, що зберігає рейтинг від www.imdb.com.
     */
    private final String jsonKeyImdbVotes;

    /**
     * Назва поля JSON-об'єкта, що зберігає рейтинги на різних ресурсах.
     */
    private final String jsonKeyRatings;

    /**
     * Назва поля JSON-об'єкта (описує елемент із рейтингом), що зберігає назву
     * джерела.
     */
    private final String jsonRatingSourceKeySource;

    /**
     * Назва поля JSON-об'єкта (описує елемент із рейтингом), що зберігає
     * значення оцінки на відповідному джерелі.
     */
    private final String jsonRatingSourceKeyValue;

    /**
     * Значення поля JSON-об'єкта за замовченням.
     */
    private final String jsonValueDefaultStringField;

    /**
     * Значення поля Response на випадок невдачі під час отримання даних.
     */
    private final String jsonValueBadResponse;

    /**
     * Назва поля в шаблоні для збереження типу контенту.
     */
    private final String docxFieldType;

    /**
     * Назва поля в шаблоні для збереження назви фільму.
     */
    private final String docxFieldTitle;

    /**
     * Назва поля в шаблоні для збереження року випуску фільму.
     */
    private final String docxFieldYear;

    /**
     * Назва поля в шаблоні для збереження IMDb ID.
     */
    private final String docxFieldImdbID;

    /**
     * Назва поля в шаблоні для збереження сюжету.
     */
    private final String docxFieldPlot;

    /**
     * Назва поля в шаблоні для збереження URL-посилання на постер.
     */
    private final String docxFieldPoster;

    /**
     * Назва поля в шаблоні для збереження рейтингу вмісту.
     */
    private final String docxFieldRated;

    /**
     * Назва поля в шаблоні для збереження тривалості фільму.
     */
    private final String docxFieldRuntime;

    /**
     * Назва поля в шаблоні для збереження жанру фільму.
     */
    private final String docxFieldGenre;

    /**
     * Назва поля в шаблоні для збереження дати випуску.
     */
    private final String docxFieldReleased;

    /**
     * Назва поля в шаблоні для збереження директора.
     */
    private final String docxFieldDirector;

    /**
     * Назва поля в шаблоні для збереження сценариста.
     */
    private final String docxFieldWriter;

    /**
     * Назва поля в шаблоні для збереження акторів.
     */
    private final String docxFieldActors;

    /**
     * Назва поля в шаблоні для збереження мови фільму.
     */
    private final String docxFieldLanguage;

    /**
     * Назва поля в шаблоні для збереження країни-виробника.
     */
    private final String docxFieldCountry;

    /**
     * Назва поля в шаблоні для збереження нагород.
     */
    private final String docxFieldAwards;

    /**
     * Назва поля в шаблоні для збереження студії - виробника фільму.
     */
    private final String docxFieldProduction;

    /**
     * Назва поля в шаблоні для збереження касових зборів.
     */
    private final String docxFieldBoxOffice;

    /**
     * Назва поля в шаблоні для збереження рейтингу на www.metacritic.com.
     */
    private final String docxFieldMetascore;

    /**
     * Назва поля в шаблоні для збереження рейтингу на www.imdb.com.
     */
    private final String docxFieldImdbRating;

    /**
     * Назва поля в шаблоні для збереження кількості голосів на www.imdb.com.
     */
    private final String docxFieldImdbVotes;

    /**
     * Назва поля в шаблоні для збереження автора файлу.
     */
    private final String docxFieldAuthor;

    /**
     * Назва поля в шаблоні для збереження поточного року.
     */
    private final String docxFieldCurrentYear;

    /**
     * Назва додатка.
     */
    private final String applicationName;

    /**
     * Назва файлу-шаблона для генерації документів.
     */
    private final String templateFilename;

    /**
     * Текст-зачіпка для можливості отримати посилання на рейтингову таблицю.
     */
    private final String ratingsTableAnchor;

    /**
     * Назва постера в межах документа.
     */
    private final String posterName;

    /**
     * Назва файлу, що замінює постер на випадок його відсутності.
     */
    private final String noPosterImage;

    /**
     * Словник для встановлення відповідності між розширеннями файлів зображень
     * та їх представленням у вигляді цілого числа.
     */
    private final Map<String, Integer> imageFormatMapper;

    /**
     * Загальний опис проблеми, пов'язаної із некоректними вхідними даними.
     */
    private final String exceptionMessageBadInput;

    /**
     * Опис проблеми, пов'язаною із невдачою під час отримки даних.
     */
    private final String exceptionMessageDataGatheringFailed;

    /**
     * Опис проблеми, пов'язаної із відсутністю даних за значеннями параметрів
     * запиту.
     */
    private final String exceptionMessageNoDataFound;

    /**
     * Опис проблеми, пов'язаної із внутрішньою проблемою, що виникла в додатку
     * (помилка парсингу XML, не знайдений файл та ін.).
     */
    private final String exceptionMessageApplicationInnerException;

    /**
     * Бін, що використовується для отримання змінних оточення, визначених у
     * application.properties.
     */
    private final Environment env;

    /**
     * Бін, що займається співставленням звернень до API, щоб мати можливість
     * зручного налаштування відповідності виду: API додатку -> API джерела
     * даних.
     */
    private final RequestParamMapper requestParamMapper;

    /**
     * Бін, що необхідний для можливості отримання даних за допомогою URL-
     * адреси.
     */
    private final RestTemplate restTemplate;

    /**
     * Створює бін сервіса.
     * @param env бін для отримання змінних із application.yml
     * @param requestParamMapper бін для співставлення звернень до API
     * @param restTemplate бін для отримання даних за допомогою URL-адреси
     */
    public FilmService(final Environment env, final RequestParamMapper requestParamMapper, final RestTemplate restTemplate) {
        this.env = env;
        this.requestParamMapper = requestParamMapper;
        this.restTemplate = restTemplate;

        // константи для використання API додатку
        applicationQueryKeyTitle = env.getProperty("application.api.param.title");
        applicationQueryKeyYear = env.getProperty("application.api.param.year");
        applicationQueryKeyPlot = env.getProperty("application.api.param.plot");
        applicationQueryKeyFormat = env.getProperty("application.api.param.format");
        applicationQueryKeyId = env.getProperty("application.api.param.id");
        applicationProtocol = env.getProperty("application.protocol");
        applicationHost = env.getProperty("application.host");

        // константи для використання API джерела даних
        dataSourceQueryKeyTitle = requestParamMapper.mapParam(applicationQueryKeyTitle);
        dataSourceQueryKeyYear = requestParamMapper.mapParam(applicationQueryKeyYear);
        dataSourceQueryKeyPlot = requestParamMapper.mapParam(applicationQueryKeyPlot);
        dataSourceQueryKeyFormat = requestParamMapper.mapParam(applicationQueryKeyFormat);
        dataSourceQueryKeyId = requestParamMapper.mapParam(applicationQueryKeyId);
        dataSourceQueryKeyApiKey = env.getProperty("application.data-source.api.param.api-key");
        dataSourceProtocol = env.getProperty("application.data-source.protocol");
        dataSourceHost = env.getProperty("application.data-source.host");
        dataSourceApiKey = env.getProperty("application.data-source.api-key");

        // можливі оброблювані ключі отримуваних JSON-об'єктів
        jsonKeyResponse = env.getProperty("application.data-source.json.key.response");
        jsonKeyType = env.getProperty("application.data-source.json.key.type");
        jsonKeyTitle = env.getProperty("application.data-source.json.key.title");
        jsonKeyYear = env.getProperty("application.data-source.json.key.year");
        jsonKeyImdbId = env.getProperty("application.data-source.json.key.imdb-id");
        jsonKeyRated = env.getProperty("application.data-source.json.key.rated");
        jsonKeyRuntime = env.getProperty("application.data-source.json.key.runtime");
        jsonKeyGenre = env.getProperty("application.data-source.json.key.genre");
        jsonKeyReleased = env.getProperty("application.data-source.json.key.released");
        jsonKeyPlot = env.getProperty("application.data-source.json.key.plot");
        jsonKeyPoster = env.getProperty("application.data-source.json.key.poster");
        jsonKeyDirector = env.getProperty("application.data-source.json.key.director");
        jsonKeyWriter = env.getProperty("application.data-source.json.key.writer");
        jsonKeyActors = env.getProperty("application.data-source.json.key.actors");
        jsonKeyLanguage = env.getProperty("application.data-source.json.key.language");
        jsonKeyCountry = env.getProperty("application.data-source.json.key.country");
        jsonKeyAwards = env.getProperty("application.data-source.json.key.awards");
        jsonKeyProduction = env.getProperty("application.data-source.json.key.production");
        jsonKeyBoxOffice = env.getProperty("application.data-source.json.key.box-office");
        jsonKeyMetascore = env.getProperty("application.data-source.json.key.metascore");
        jsonKeyImdbRating = env.getProperty("application.data-source.json.key.imdb-rating");
        jsonKeyImdbVotes = env.getProperty("application.data-source.json.key.imdb-votes");
        jsonKeyRatings = env.getProperty("application.data-source.json.key.ratings");
        jsonRatingSourceKeySource = env.getProperty("application.data-source.json.key.ratings.source.source");
        jsonRatingSourceKeyValue = env.getProperty("application.data-source.json.key.ratings.source.value");

        // деякі особливі значення для JSON-об'єктів
        jsonValueDefaultStringField = env.getProperty("application.data-source.json.value.default-string-field");
        jsonValueBadResponse = env.getProperty("application.data-source.json.value.bad-response");

        // назви полів, що зберігає документ-шаблон
        docxFieldType = env.getProperty("application.document-template.field.type");
        docxFieldTitle = env.getProperty("application.document-template.field.title");
        docxFieldYear = env.getProperty("application.document-template.field.year");
        docxFieldImdbID = env.getProperty("application.document-template.field.imdbID");
        docxFieldPlot = env.getProperty("application.document-template.field.plot");
        docxFieldPoster = env.getProperty("application.document-template.field.poster");
        docxFieldRated = env.getProperty("application.document-template.field.rated");
        docxFieldRuntime = env.getProperty("application.document-template.field.runtime");
        docxFieldGenre = env.getProperty("application.document-template.field.genre");
        docxFieldReleased = env.getProperty("application.document-template.field.released");
        docxFieldDirector = env.getProperty("application.document-template.field.director");
        docxFieldWriter = env.getProperty("application.document-template.field.writer");
        docxFieldActors = env.getProperty("application.document-template.field.actors");
        docxFieldLanguage = env.getProperty("application.document-template.field.language");
        docxFieldCountry = env.getProperty("application.document-template.field.country");
        docxFieldAwards = env.getProperty("application.document-template.field.awards");
        docxFieldProduction = env.getProperty("application.document-template.field.production");
        docxFieldBoxOffice = env.getProperty("application.document-template.field.boxOffice");
        docxFieldMetascore = env.getProperty("application.document-template.field.metascore");
        docxFieldImdbRating = env.getProperty("application.document-template.field.imdbRating");
        docxFieldImdbVotes = env.getProperty("application.document-template.field.imdbVotes");
        docxFieldAuthor = env.getProperty("application.document-template.field.author");
        docxFieldCurrentYear = env.getProperty("application.document-template.field.currentYear");

        // встановлення відповідності між назвами форматів та цілочисельними
        // константами із Apache POI
        imageFormatMapper = new HashMap<>();
        imageFormatMapper.put("emf", XWPFDocument.PICTURE_TYPE_EMF);
        imageFormatMapper.put("wmf", XWPFDocument.PICTURE_TYPE_WMF);
        imageFormatMapper.put("pict", XWPFDocument.PICTURE_TYPE_PICT);
        imageFormatMapper.put("jpeg", XWPFDocument.PICTURE_TYPE_JPEG);
        imageFormatMapper.put("jpg", XWPFDocument.PICTURE_TYPE_JPEG);
        imageFormatMapper.put("png", XWPFDocument.PICTURE_TYPE_PNG);
        imageFormatMapper.put("dib", XWPFDocument.PICTURE_TYPE_DIB);
        imageFormatMapper.put("gif", XWPFDocument.PICTURE_TYPE_GIF);
        imageFormatMapper.put("tiff", XWPFDocument.PICTURE_TYPE_TIFF);
        imageFormatMapper.put("eps", XWPFDocument.PICTURE_TYPE_EPS);
        imageFormatMapper.put("bmp", XWPFDocument.PICTURE_TYPE_BMP);
        imageFormatMapper.put("wpg", XWPFDocument.PICTURE_TYPE_WPG);

        // інші налаштування
        applicationName = env.getProperty("application.name");
        templateFilename = env.getProperty("application.document-template.filename");
        ratingsTableAnchor = env.getProperty("application.document-template.ratings-table-anchor");
        noPosterImage = env.getProperty("application.no-poster-image-filename");
        posterName = env.getProperty("application.document-template.poster-name");
        exceptionMessageBadInput = env.getProperty("application.exception-message.bad-input");
        exceptionMessageDataGatheringFailed = env.getProperty("application.exception-message.data-gathering-failed");
        exceptionMessageNoDataFound = env.getProperty("application.exception-message.no-data-found");
        exceptionMessageApplicationInnerException = env.getProperty("application.exception-message.application-inner-exception");

        logger.debug("Бін сервісу " + getClass().getSimpleName() + " створений");
    }

    private String getString(final JSONObject obj, final String key, final String defaultValue) {
        if (!obj.has(key)) {
            return defaultValue;
        }
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    private String getString(final JSONObject obj, final String key) {
        return getString(obj, key, jsonValueDefaultStringField);
    }

    private FilmDTO parse(final String json) {
        JSONObject obj = null;
        String response;

        // намагаємося створити JSON-об'єкт із вказаноо рядка; на випадок, коли
        // це зробити не вдасться - відповідний DTO повинен мати інформацію, що
        // отримана була погана відповідь (bad response)
        try {
            obj = new JSONObject(json);
            response = getString(obj, jsonKeyResponse, jsonValueBadResponse);
        } catch (JSONException e) {
            response = jsonValueBadResponse;
            logger.log(Level.WARN, "Не вдалося здійснити парсинг отриманої JSON-відповіді", e);
        }
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setResponse(response);

        // якщо JSON-об'єкт був успішно прочитаний та не містив даних про те,
        // що отримана відповідь - погана, можемо зчитувати з нього дані;
        // на випадок, якщо деякі поля не містяться в ньому - передбачається
        // значення за замовчуванням
        if (!response.equals(jsonValueBadResponse)) {
            filmDTO.setType(getString(obj, jsonKeyType));
            filmDTO.setTitle(getString(obj, jsonKeyTitle));
            filmDTO.setYear(getString(obj, jsonKeyYear));
            filmDTO.setImdbID(getString(obj, jsonKeyImdbId));
            filmDTO.setPlot(getString(obj, jsonKeyPlot));
            filmDTO.setPoster(getString(obj, jsonKeyPoster, null));
            filmDTO.setRated(getString(obj, jsonKeyRated));
            filmDTO.setRuntime(getString(obj, jsonKeyRuntime));
            filmDTO.setGenre(getString(obj, jsonKeyGenre));
            filmDTO.setReleased(getString(obj, jsonKeyReleased));
            filmDTO.setDirector(getString(obj, jsonKeyDirector));
            filmDTO.setWriter(getString(obj, jsonKeyWriter));
            filmDTO.setActors(getString(obj, jsonKeyActors));
            filmDTO.setLanguage(getString(obj, jsonKeyLanguage));
            filmDTO.setCountry(getString(obj, jsonKeyCountry));
            filmDTO.setAwards(getString(obj, jsonKeyAwards));
            filmDTO.setProduction(getString(obj, jsonKeyProduction));
            filmDTO.setBoxOffice(getString(obj, jsonKeyBoxOffice));
            filmDTO.setMetascore(getString(obj, jsonKeyMetascore));
            filmDTO.setImdbRating(getString(obj, jsonKeyImdbRating));
            filmDTO.setImdbVotes(getString(obj, jsonKeyImdbVotes));
            logger.debug("Отримання основних даних із JSON-відповіді завершено");

            // перевіряється, чи містить JSON-об'єкт дані про рейтинги на
            // різноманітних ресурсах
            if (obj.has(jsonKeyRatings)) {
                Map<String, Map<String, String>> ratings = new HashMap<>();
                JSONArray jsonArray = obj.getJSONArray(jsonKeyRatings);

                int size = jsonArray.length();
                JSONObject temp;
                HashMap<String, String> currentMap;
                for (int index = 0; index < size; ++index) {
                    temp = jsonArray.getJSONObject(index);
                    currentMap = new HashMap<>();
                    currentMap.put(jsonRatingSourceKeySource, getString(temp, jsonRatingSourceKeySource));
                    currentMap.put(jsonRatingSourceKeyValue, getString(temp, jsonRatingSourceKeyValue));
                    ratings.put(String.valueOf(index), currentMap);
                }
                filmDTO.setRatings(ratings);
                logger.debug("JSON-відповідь мала в наявності дані про рейтинги - вони були опрацьовані");
            }
        }

        return filmDTO;
    }

    /**
     * Повертає тіло відповіді відповідно до параметрів, що містить у собі DTO.
     * Параметри, такі як назва фільму (title), IMDb ID (id) будуть
     * обов'язковими (один із них).
     *
     * Рік випуску фільму є опціональним. Його можна вказувати лише в запитах, що
     * спираються на назву. Метод згенерує виключення на випадок, якщо цей
     * параметр буде визначений для запиту, що сприається на IMDb ID. Може бути
     * порожнім рядком.
     *
     * Режим відображення сюжету - опціональний параметр. За замовчуванням -
     * short. Може бути порожнім рядком.
     *
     * Формат відповіді - опціональний параметр. За замовчуванням - JSON. Може
     * бути порожнім рядком.
     * @param dto об'єкт для передачі даних про фільм
     * @return тіло відповіді
     */
    private String getURL(final GetFilmDataRequest dto) throws FilmServiceException {
        URLBuilder urlb = new URLBuilder(dataSourceProtocol, dataSourceHost);
        String title = dto.getTitle();
        String id = dto.getId();

        boolean titlePresented = title != null;
        boolean idPresented = id != null;

        boolean titleIsEmpty = titlePresented && (title.isEmpty());
        boolean idIsEmpty = idPresented && (id.isEmpty());
        if (!titlePresented && !idPresented) {
            logger.error("Назва фільму або його IMDb ID повинен бути вказаний");
            throw new FilmServiceException(exceptionMessageBadInput, new RequestParamOmitedException("Назва фільму або його IMDb ID повинен бути вказаний"));
        } else if (titlePresented && idPresented) {
            logger.error("Конфлікт параметрів: треба вказувати або назву фільму, або його IMDb ID");
            throw new FilmServiceException(exceptionMessageBadInput, new RequestParamsConflictException("Конфлікт параметрів: треба вказувати або назву фільму, або його IMDb ID"));
        } else {
            // якщо назва фільму представлена - вона повинна бути непорожньою;
            // або - заданий IMDb ID, що на цей випадок не може бути порожнім
            if (titlePresented && titleIsEmpty) {
                logger.error("Значення основного параметру (назва фільму) не може бути порожньою");
                throw new FilmServiceException(exceptionMessageBadInput, new RequestParamInvalidValueException("Значення основного параметру (назва фільму) не може бути порожньою"));
            } else if (idPresented && idIsEmpty) {
                logger.error("Значення основного параметру (IMDb ID) не може бути порожнім");
                throw new FilmServiceException(exceptionMessageBadInput, new RequestParamsConflictException("Значення основного параметру (IMDb ID) не може бути порожнім"));
            }

            // непорожність назви фільму говорить про його існування (not null)
            // та відсутність IMDb ID (null) і навпаки
            if (titlePresented) {
                urlb.addParameter(dataSourceQueryKeyTitle, title);
            } else {
                urlb.addParameter(dataSourceQueryKeyId, id);
            }

            // рік - опціональний параметр, що може бути вказаний для фільму,
            // що шукаємо за назвою, може бути порожнім рядком - для зручності
            // передачі даних із форми
            String year = dto.getYear();
            if (year != null && !year.isEmpty()) {
                // дійшовши до цього фрагмента - рядок із роком непорожінй,
                // тому знаходимо, за яким саме параметром відбувався пошук
                // (за назвою фільму або його IMDb ID)
                if (!titleIsEmpty) {
                    // назва фільму непорожня, отже - існує, а значить - не
                    // існує IMDb ID, тому намагаємося інтерпретувати значення
                    try {
                        urlb.addParameter(dataSourceQueryKeyYear, Integer.parseInt(year));
                    } catch (NumberFormatException e) {
                        logger.error("Некоректне значення параметра року випуску фільму");
                        throw new FilmServiceException(exceptionMessageBadInput, new RequestParamInvalidValueException("Некоректне значення параметра року випуску фільму"));
                    }
                } else {
                    // назва фільму виявилася порожньою, отже - її і не
                    // існувало - отже, здійснюється запит за IMDb, для якого
                    // вказання року є непотрібним
                    logger.error("Вказаний рік випуску фільму є надлишковим параметром для запиту за IMDb ID");
                    throw new FilmServiceException(exceptionMessageBadInput, new RedundantRequestParamException("Вказаний рік випуску фільму є надлишковим параметром для запиту за IMDb ID"));
                }
            }

            // режим відображення сюжету - опціональний параметр, рівний short
            // за замовченням, рядок може бути порожнім - для зручності
            // передачі даних із форми
            String plot = dto.getPlot();
            if (plot != null && !plot.isEmpty()) {
                // режим відображення сюжету виявився не порожнім - тепер треба
                // проаналізувати валідність його значення
                if (requestParamMapper.isParameterValueValid(applicationQueryKeyPlot, plot)) {
                    urlb.addParameter(dataSourceQueryKeyPlot, plot);
                } else {
                    logger.error("Некоректне значення параметра, що відповідає за режим відображення сюжету");
                    throw new FilmServiceException(exceptionMessageBadInput, new RequestParamInvalidValueException("Некоректне значення параметра, що відповідає за режим відображення сюжету"));
                }
            } else {
                // встановлення значення за замовчуванням
                urlb.addParameter(dataSourceQueryKeyPlot, env.getProperty("application.plot-mode.default"));
            }

            // формат відповіді - опціональний параметр, рівний json за
            // замовчуванням, може бути рівний порожньому рядку - для зручності
            // передачі даних із форми
            String format = dto.getFormat();
            if (format != null && !format.isEmpty()) {
                // формат відповіді виявився непорожнім - треба проаналізувати
                // валідність його значення
                if (requestParamMapper.isParameterValueValid(applicationQueryKeyFormat, format)) {
                    urlb.addParameter(dataSourceQueryKeyFormat, format);
                } else {
                    logger.error("Некоректне значення параметра, що відповідає за формат відповіді");
                    throw new FilmServiceException(exceptionMessageBadInput, new RequestParamInvalidValueException("Некоректне значення параметра, що відповідає за формат відповіді"));
                }
            } else {
                // встановлення значення за замовчуванням
                urlb.addParameter(dataSourceQueryKeyPlot, env.getProperty("application.response-data-format.default"));
            }
        }

        // встановлення використовуванного ключу до API для можливості
        // використання джерела даних
        urlb.addParameter(dataSourceQueryKeyApiKey, dataSourceApiKey);

        String result = urlb.toString();
        logger.debug("Побудований URL: " + result);
        return result;
    }

    /**
     * Повертає тіло відповіді відповідно до параметрів, що містить у собі DTO
     * запиту. Працює в асинхронному режимі.
     * @param dto об'єкт для передачі даних про запит
     * @return тіло відповіді
     */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> getRequestBody(final GetFilmDataRequest dto) throws FilmServiceException {
        try {
            return CompletableFuture.completedFuture(restTemplate.getForObject(getURL(dto), String.class));
        } catch (RestClientException e) {
            logger.log(Level.ERROR, "Не вдалося отримати дані від джерела", e);
            throw new FilmServiceException(exceptionMessageDataGatheringFailed, e);
        }
    }

    /**
     * Повертає документ, наповнення якого залежить від параметрів, що містить
     * у собі DTO запиту. Працює в асинхронному режимі.
     * @param dto об'єкт для передачі даних про запит
     * @return документ
     */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<XWPFDocument> getDocument(final GetFilmDataRequest dto) {
        try {
            return getDocument0(dto);
        } catch (FilmServiceException e) {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(e.getMessage());
            return CompletableFuture.completedFuture(document);
        }
    }

    // використовується для спроби отримати документ, яка може бути невдалою
    // через виняткові ситуації
    private CompletableFuture<XWPFDocument> getDocument0(final GetFilmDataRequest dto) throws FilmServiceException {
        String json;
        try {
            json = restTemplate.getForObject(getURL(dto), String.class);
        } catch (RestClientException e) {
            logger.log(Level.ERROR, "Не вдалося отримати дані від джерела для заповнення документа", e);
            throw new FilmServiceException(exceptionMessageDataGatheringFailed, e);
        }

        // якщо спроба отримати дані була успішною
        FilmDTO filmDTO = parse(json);

        if (!filmDTO.getResponse().equals(jsonValueBadResponse)) {
            File file;
            try {
                file = new File(getClass().getClassLoader().getResource(templateFilename).toURI());
            } catch (NullPointerException | URISyntaxException e) {
                logger.log(Level.ERROR, "Не вдалося знайти файл шаблону документа", e);
                throw new FilmServiceException(exceptionMessageApplicationInnerException, e);
            }
            XWPFDocument document;
            try {
                document = new XWPFDocument(new FileInputStream(file));
            } catch (IOException e) {
                logger.log(Level.ERROR, "Не вдалося відкрити для читання файл шаблону документа", e);
                throw new FilmServiceException(exceptionMessageApplicationInnerException, e);
            }

            // якщо внутрішніх помилок не відбулося - можна переносити дані та
            // формувати документ-результат
            Map<String, String> map = new HashMap<>();
            map.put(docxFieldType, filmDTO.getType());
            map.put(docxFieldTitle, filmDTO.getTitle());
            map.put(docxFieldYear, filmDTO.getYear());
            map.put(docxFieldImdbID, filmDTO.getImdbID());
            map.put(docxFieldPlot, filmDTO.getPlot());
            map.put(docxFieldRated, filmDTO.getRated());
            map.put(docxFieldRuntime, filmDTO.getRuntime());
            map.put(docxFieldGenre, filmDTO.getGenre());
            map.put(docxFieldReleased, filmDTO.getReleased());
            map.put(docxFieldDirector, filmDTO.getDirector());
            map.put(docxFieldWriter, filmDTO.getWriter());
            map.put(docxFieldActors, filmDTO.getActors());
            map.put(docxFieldLanguage, filmDTO.getLanguage());
            map.put(docxFieldCountry, filmDTO.getCountry());
            map.put(docxFieldAwards, filmDTO.getAwards());
            map.put(docxFieldProduction, filmDTO.getProduction());
            map.put(docxFieldBoxOffice, filmDTO.getBoxOffice());
            map.put(docxFieldMetascore, filmDTO.getMetascore());
            map.put(docxFieldImdbRating, filmDTO.getImdbRating());
            map.put(docxFieldImdbVotes, filmDTO.getImdbVotes());
            map.put(docxFieldAuthor, applicationName);
            map.put(docxFieldCurrentYear, String.valueOf((Calendar.getInstance().get(Calendar.YEAR))));
            XWPFDocumentManipulator.bindFields(document, map);
            logger.info("Документ заповнений отриманими із JSON-об'єкта значеннями");

            // заповнення даних про рейтинги, якщо вони наявні
            Map<String, Map<String, String>> ratings = filmDTO.getRatings();
            if (ratings != null && ratings.size() > 0) {
                logger.debug("Дані про фільм містять рейтинги, вони будуть використані для заповнення шаблона");
                XWPFTable tableWithRatings = XWPFDocumentManipulator.getTableWithContent(document, ratingsTableAnchor);
                int size = tableWithRatings.getRows().size();
                Set<String> keys = ratings.keySet();
                for (String key : keys) {
                    Map<String, String> sourceEntry = ratings.get(key);
                    XWPFTableRow lastRow = tableWithRatings.getRow(size - 1);
                    CTRow ctrow;
                    try {
                        ctrow = CTRow.Factory.parse(lastRow.getCtRow().newInputStream());
                    } catch (XmlException | IOException e) {
                        logger.log(Level.ERROR, "Не вдалося здійснити XML-парсинг рядка таблиці із рейтингами");
                        throw new FilmServiceException(exceptionMessageApplicationInnerException, e);
                    }
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, tableWithRatings);

                    // видалення тексту, що був отриманий із попереднього рядка
                    // шляхом використання його xml-розмітки
                    for (XWPFTableCell cell : newRow.getTableCells()) {
                        XWPFDocumentManipulator.removeAllParagraphs(cell);
                    }

                    // заповнення даними таблиці у вигляді: джерело рейтингу, оцінка
                    newRow.getCell(0).setText(sourceEntry.get(jsonRatingSourceKeySource));
                    newRow.getCell(1).setText(sourceEntry.get(jsonRatingSourceKeyValue));
                    tableWithRatings.addRow(newRow, size++);
                }
                logger.debug("Шаблон документа заповнений даними про рейтинги");
            }

            String poster = filmDTO.getPoster();
            byte[] data = null;
            String formatName;

            // отримання даних про зображення: якщо не буде наявний постер, тоді
            // буде використано зображення за замовчуванням замість нього
            if (poster != null && imageFormatMapper.containsKey(formatName = poster.substring(poster.lastIndexOf('.') + 1))) {
                try {
                    data = restTemplate.getForObject(poster, byte[].class);
                } catch (RestClientException e) {
                    logger.log(Level.WARN, "Не вдалося отримати файл зображення постера під час завантаження", e);
                    throw new FilmServiceException(exceptionMessageApplicationInnerException, e);
                }
            } else if (imageFormatMapper.containsKey(formatName = noPosterImage.substring(noPosterImage.lastIndexOf('.') + 1))) {
                try (FileInputStream fis = new FileInputStream(new File(getClass().getClassLoader().getResource(noPosterImage).toURI()))) {
                    data = IOUtils.toByteArray(fis);
                } catch (NullPointerException | URISyntaxException | IOException e) {
                    logger.log(Level.WARN, "Не вдалося отримати файл зображення постера під час використання локального файлу", e);
                    throw new FilmServiceException(exceptionMessageApplicationInnerException, e);
                }
            }

            if (data == null) {
                logger.warn("Вказаний формат постера не підтримується. Повернення документа відбудеться");
            } else {
                try (InputStream io = new ByteArrayInputStream(data)) {
                    BufferedImage bufferedImage = ImageIO.read(io);
                    ImageDTO imageDTO = new ImageDTO(data, bufferedImage.getWidth(), bufferedImage.getHeight(), imageFormatMapper.get(formatName), posterName);
                    XWPFDocumentManipulator.bindImageToField(document, docxFieldPoster, imageDTO);
                    logger.info("У документ було додано зображення-постер");
                } catch (IOException e) {
                    logger.log(Level.ERROR, "Не вдалося додати зображення-постер до документа", e);
                    throw new FilmServiceException(exceptionMessageApplicationInnerException, e);
                }
            }

            return CompletableFuture.completedFuture(document);
        } else {
            logger.error("Не вдалося отримати дані для відображення");
            throw new FilmServiceException(exceptionMessageDataGatheringFailed, new NoDataFoundException(exceptionMessageNoDataFound));
        }
    }
}
