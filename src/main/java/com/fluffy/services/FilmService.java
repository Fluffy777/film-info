package com.fluffy.services;

import com.fluffy.dtos.FilmDTO;
import com.fluffy.dtos.GetFilmDataRequest;
import com.fluffy.dtos.ImageDTO;
import com.fluffy.exceptions.*;
import com.fluffy.util.RequestParamMapper;
import com.fluffy.util.URLBuilder;
import com.fluffy.util.XWPFDocumentManipulator;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import  org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Клас сервісу. Інкапсулює в собі бізнес-логіку, необхідну до виконання над
 * вхідними даними.
 * @author Сивоконь Вадим
 */
@Service
public class FilmService {
    private final String DATA_SOURCE_PROTOCOL;
    private final String DATA_SOURCE_HOST;
    private final String DATA_SOURCE_API_KEY;
    private final String DATA_SOURCE_QUERY_KEY_TITLE;
    private final String DATA_SOURCE_QUERY_KEY_YEAR;
    private final String DATA_SOURCE_QUERY_KEY_PLOT;
    private final String DATA_SOURCE_QUERY_KEY_FORMAT;
    private final String DATA_SOURCE_QUERY_KEY_ID;
    private final String DATA_SOURCE_QUERY_KEY_API_KEY;

    private final String APPLICATION_PROTOCOL;
    private final String APPLICATION_HOST;
    private final String APPLICATION_QUERY_KEY_TITLE;
    private final String APPLICATION_QUERY_KEY_YEAR;
    private final String APPLICATION_QUERY_KEY_PLOT;
    private final String APPLICATION_QUERY_KEY_FORMAT;
    private final String APPLICATION_QUERY_KEY_ID;

    private final String TEMPLATE_FILENAME = "Template.docx";

    /**
     * Загальний опис проблеми, пов'язаної із некоректними вхідними даними.
     */
    private final String EXCEPTION_MESSAGE_BAD_INPUT = "Некоректні вхідні дані";

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

    private final RestTemplate restTemplate;

    /**
     * Створює об'єкт (бін) сервіса.
     * @param env бін для отримання змінних із application.properties
     * @param requestParamMapper бін для співставлення звернень до API
     */
    public FilmService(Environment env, RequestParamMapper requestParamMapper, RestTemplate restTemplate) {
        this.env = env;
        this.requestParamMapper = requestParamMapper;
        this.restTemplate = restTemplate;
        APPLICATION_QUERY_KEY_TITLE = env.getProperty("application.api.param.title");
        APPLICATION_QUERY_KEY_YEAR = env.getProperty("application.api.param.year");
        APPLICATION_QUERY_KEY_PLOT = env.getProperty("application.api.param.plot");
        APPLICATION_QUERY_KEY_FORMAT = env.getProperty("application.api.param.format");
        APPLICATION_QUERY_KEY_ID = env.getProperty("application.api.param.id");
        APPLICATION_PROTOCOL = env.getProperty("application.protocol");
        APPLICATION_HOST = env.getProperty("application.host");


        // даний Bean є singleton, оскільки таке його визначення за
        // замовчуванням, тому немає сенсу зберігати константи у
        // вигляді статичних фіналізованих змінних
        DATA_SOURCE_QUERY_KEY_TITLE = requestParamMapper.mapParam(APPLICATION_QUERY_KEY_TITLE);
        DATA_SOURCE_QUERY_KEY_YEAR = requestParamMapper.mapParam(APPLICATION_QUERY_KEY_YEAR);
        DATA_SOURCE_QUERY_KEY_PLOT = requestParamMapper.mapParam(APPLICATION_QUERY_KEY_PLOT);
        DATA_SOURCE_QUERY_KEY_FORMAT = requestParamMapper.mapParam(APPLICATION_QUERY_KEY_FORMAT);
        DATA_SOURCE_QUERY_KEY_ID = requestParamMapper.mapParam(APPLICATION_QUERY_KEY_ID);

        // додаток не вимагає використання api-key, тому співставлення не є необхідністю
        DATA_SOURCE_QUERY_KEY_API_KEY = env.getProperty("application.data-source.api.param.api-key");

        DATA_SOURCE_PROTOCOL = env.getProperty("application.data-source.protocol");
        DATA_SOURCE_HOST = env.getProperty("application.data-source.host");
        DATA_SOURCE_API_KEY = env.getProperty("application.data-source.api-key");

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
    }

    private static final String JSON_KEY_TYPE = "Type";
    private static final String JSON_KEY_TITLE = "Title";
    private static final String JSON_KEY_YEAR = "Year";
    private static final String JSON_KEY_IMDB_ID = "imdbID";
    private static final String JSON_KEY_PLOT = "Plot";

    private static final String JSON_KEY_RATED = "Rated";
    private static final String JSON_KEY_RUNTIME = "Runtime";
    private static final String JSON_KEY_GENRE = "Genre";
    private static final String JSON_KEY_RELEASED = "Released";

    private static final String JSON_KEY_DIRECTOR = "Director";
    private static final String JSON_KEY_WRITER = "Writer";
    private static final String JSON_KEY_ACTORS = "Actors";

    private static final String JSON_KEY_LANGUAGE = "Language";
    private static final String JSON_KEY_COUNTRY = "Country";
    private static final String JSON_KEY_AWARDS = "Awards";

    private static final String JSON_KEY_PRODUCTION = "Production";
    private static final String JSON_KEY_BOX_OFFICE = "BoxOffice";

    private static final String JSON_KEY_METASCORE = "Metascore";
    private static final String JSON_KEY_IMDB_RATING = "imdbRating";
    private static final String JSON_KEY_IMDB_VOTES = "imdbVotes";
    private static final String JSON_KEY_RATINGS = "Ratings";

    private static final String JSON_RATING_SOURCE_KEY_SOURCE = "Source";
    private static final String JSON_RATING_SOURCE_KEY_VALUE = "Value";

    private static final String JSON_KEY_POSTER = "Poster";

    private static final String DEFAULT_FIELD_VALUE = "—";

    private <T> T nvl(JSONObject obj, String key, T ifNull, Class<T> clazz) {
        T data = (T)obj.get(key);
        return (data != null ? data : ifNull);
    }

    private String getValue(JSONObject obj, String key) {
        return nvl(obj, key, DEFAULT_FIELD_VALUE, String.class);
    }

    private FilmDTO parse(String json) {
        JSONObject obj = new JSONObject(json);
        FilmDTO filmDTO = new FilmDTO();

        filmDTO.setType(getValue(obj, JSON_KEY_TYPE));
        filmDTO.setTitle(getValue(obj, JSON_KEY_TITLE));
        filmDTO.setYear(getValue(obj, JSON_KEY_YEAR));
        filmDTO.setImdbID(getValue(obj, JSON_KEY_IMDB_ID));

        filmDTO.setRated(getValue(obj, JSON_KEY_RATED));
        filmDTO.setRuntime(getValue(obj, JSON_KEY_RUNTIME));
        filmDTO.setGenre(getValue(obj, JSON_KEY_GENRE));
        filmDTO.setReleased(getValue(obj, JSON_KEY_RELEASED));

        filmDTO.setPlot(getValue(obj, JSON_KEY_PLOT));

        filmDTO.setDirector(getValue(obj, JSON_KEY_DIRECTOR));
        filmDTO.setWriter(getValue(obj, JSON_KEY_WRITER));
        filmDTO.setActors(getValue(obj, JSON_KEY_ACTORS));

        filmDTO.setLanguage(getValue(obj, JSON_KEY_LANGUAGE));
        filmDTO.setCountry(getValue(obj, JSON_KEY_COUNTRY));
        filmDTO.setAwards(getValue(obj, JSON_KEY_AWARDS));

        filmDTO.setProduction(getValue(obj, JSON_KEY_PRODUCTION));
        filmDTO.setBoxOffice(getValue(obj, JSON_KEY_BOX_OFFICE));

        filmDTO.setMetascore(getValue(obj, JSON_KEY_METASCORE));
        filmDTO.setImdbRating(getValue(obj, JSON_KEY_IMDB_RATING));
        filmDTO.setImdbVotes(getValue(obj, JSON_KEY_IMDB_VOTES));

        // рейтинги
        Map<String, Map<String, String>> ratings = new HashMap<>();
        JSONArray jsonArray = obj.getJSONArray(JSON_KEY_RATINGS);

        int size = jsonArray.length();
        JSONObject temp;
        HashMap<String, String> currentMap;
        for (int index = 0; index < size; ++index) {
            temp = jsonArray.getJSONObject(index);
            currentMap = new HashMap<>();
            currentMap.put(JSON_RATING_SOURCE_KEY_SOURCE, getValue(temp, JSON_RATING_SOURCE_KEY_SOURCE));
            currentMap.put(JSON_RATING_SOURCE_KEY_VALUE, getValue(temp, JSON_RATING_SOURCE_KEY_VALUE));
            ratings.put(String.valueOf(index), currentMap);
        }
        filmDTO.setRatings(ratings);

        //...
        filmDTO.setPoster(getValue(obj, JSON_KEY_POSTER));

        return filmDTO;
    }

    /**
     * Повертає тіло відповіді відповідно до параметрів, що містить у собі DTO.
     * @param dto об'єкт для передачі даних про фільм
     * @return тіло відповіді
     */
    private String getURL(GetFilmDataRequest dto) throws FilmServiceException {
        URLBuilder urlb = new URLBuilder(DATA_SOURCE_PROTOCOL, DATA_SOURCE_HOST);
        String title = dto.getTitle();
        String id = dto.getId();
        if (title == null && id == null) {
            throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new PrimaryRequestParamOmitedException("Назва фільму або його IMDb ID повинен бути вказаний"));
        } else if (title != null && id != null) {
            throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new RequestParamsConflictException("Конфлікт параметрів: треба вказувати або назву фільму, або його IMDb ID"));
        } else if (title != null && !title.isEmpty()) {
            urlb.addParameter(DATA_SOURCE_QUERY_KEY_TITLE, title);

            String year = dto.getYear();
            if (year != null && !year.isEmpty()) {
                try {
                    urlb.addParameter(DATA_SOURCE_QUERY_KEY_YEAR, Integer.parseInt(year));
                } catch (NumberFormatException e) {
                    throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new RequestParamInvalidValueException("Некоректне значення параметра року випуску фільму"));
                }
            }

            String plot = dto.getPlot();
            if (plot != null && !plot.isEmpty()) {
                if (requestParamMapper.isParameterValueValid(env.getProperty("application.api.param.plot"), plot)) {
                    urlb.addParameter(DATA_SOURCE_QUERY_KEY_PLOT, plot);
                } else {
                    throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new RequestParamInvalidValueException("Некоректне значення параметра, що відповідає за тип співпадіння"));
                }
            }
        } else if (id != null && !id.isEmpty()) {
            urlb.addParameter(DATA_SOURCE_QUERY_KEY_ID, id);
            // параметр співпадінь враховувати немає потреби, оскільки
            // здійснити пошук за "приблизним" IMDb неможливо (джерело даних
            // не підтримує таку комбінацію параметрів), тому вказання цього
            // параметра буде вважатися надлишковим
            String plot = dto.getPlot();
            if (plot != null && !plot.isEmpty()) {
                throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new RedundantRequestParamException("Переданий надлишковий параметр, який відповідає за тип співпадіння"));
            }
        } else {
            throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new RequestParamInvalidValueException("Значення обов'язкового параметру не може бути порожнім"));
        }

        String format = dto.getFormat();
        if (format != null && !format.isEmpty() && requestParamMapper.isParameterValueValid(env.getProperty("application.api.param.format"), format)) {
            urlb.addParameter(DATA_SOURCE_QUERY_KEY_FORMAT, format);
        } else {
            throw new FilmServiceException(EXCEPTION_MESSAGE_BAD_INPUT, new RequestParamInvalidValueException("Параметр, що відповідає за формат відповіді, не може бути порожнім"));
        }
        urlb.addParameter(DATA_SOURCE_QUERY_KEY_API_KEY, DATA_SOURCE_API_KEY);
        return urlb.toString();
    }

    /**
     * Повертає тіло відповіді відповідно до параметрів, що містить у собі DTO.
     * @param dto об'єкт для передачі даних про фільм
     * @return тіло відповіді
     */
    @Async("threadPoolTaskExecutor")
    public CompletableFuture<String> getRequestBody(GetFilmDataRequest dto) throws FilmServiceException, InterruptedException {
        return CompletableFuture.completedFuture(restTemplate.getForObject(getURL(dto), String.class));
    }

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<XWPFDocument> getDocument(GetFilmDataRequest dto) throws FilmServiceException, URISyntaxException, IOException, XmlException, InterruptedException {
        FilmDTO filmDTO = parse(restTemplate.getForObject(getURL(dto), String.class));
        File file = new File(getClass().getClassLoader().getResource(TEMPLATE_FILENAME).toURI());
        XWPFDocument document = new XWPFDocument(new FileInputStream(file));

        Map<String, String> map = new HashMap<>();
        map.put("type", filmDTO.getType());
        map.put("title", filmDTO.getTitle());
        map.put("year", filmDTO.getYear());
        map.put("imdbID", filmDTO.getImdbID());

        map.put("rated", filmDTO.getRated());
        map.put("runtime", filmDTO.getRuntime());
        map.put("genre", filmDTO.getGenre());
        map.put("released", filmDTO.getReleased());

        map.put("plot", filmDTO.getPlot());

        map.put("director", filmDTO.getDirector());
        map.put("writer", filmDTO.getWriter());
        map.put("actors", filmDTO.getActors());

        map.put("language", filmDTO.getLanguage());
        map.put("country", filmDTO.getCountry());
        map.put("awards", filmDTO.getAwards());

        map.put("production", filmDTO.getProduction());
        map.put("boxOffice", filmDTO.getBoxOffice());

        map.put("metascore", filmDTO.getMetascore());
        map.put("imdbRating", filmDTO.getImdbRating());
        map.put("imdbVotes", filmDTO.getImdbVotes());

        map.put("author", env.getProperty("application.name"));
        map.put("currentYear", String.valueOf((new Date().getYear())+1900));
        XWPFDocumentManipulator.bindFields(document, map);

        // рейтинги
        XWPFTable tableWithRatings = XWPFDocumentManipulator.getTableWithContent(document, "Metascore");
        int size = tableWithRatings.getRows().size();

        Map<String, Map<String, String>> ratings = filmDTO.getRatings();
        Set<String> keys = ratings.keySet();
        for (String key : keys) {
            Map<String, String> value = ratings.get(key);

            XWPFTableRow lastRow = tableWithRatings.getRow(size - 1);
            CTRow ctrow = CTRow.Factory.parse(lastRow.getCtRow().newInputStream());
            XWPFTableRow newRow = new XWPFTableRow(ctrow, tableWithRatings);

            // для видалення тексту, що був отриманий із попереднього рядка шляхом використання його xml-розмітки
            for (XWPFTableCell cell : newRow.getTableCells()) {
                XWPFDocumentManipulator.removeAllParagraphs(cell);
            }

            newRow.getCell(0).setText(value.get(JSON_RATING_SOURCE_KEY_SOURCE));
            newRow.getCell(1).setText(value.get(JSON_RATING_SOURCE_KEY_VALUE));

            tableWithRatings.addRow(newRow, size);
            ++size;
        }

        String poster = filmDTO.getPoster();
        ImageDTO imageDTO;
        BufferedImage bufferedImage;
        byte[] data;
        InputStream io;
        String formatName;

        if (poster != null && !poster.equals(DEFAULT_FIELD_VALUE) && imageFormatMapper.containsKey(formatName = poster.substring(poster.lastIndexOf('.')+1, poster.length()).toLowerCase())) {
            data = restTemplate.getForObject(poster, byte[].class);
        } else {
            FileInputStream fis = new FileInputStream(new File(getClass().getClassLoader().getResource("Image.png").toURI()));
            formatName = "png";
            data = IOUtils.toByteArray(fis);
            fis.close();
        }

        io = new ByteArrayInputStream(data);
        bufferedImage = ImageIO.read(io);
        imageDTO = new ImageDTO(data, bufferedImage.getWidth(), bufferedImage.getHeight(), imageFormatMapper.get(formatName), "Poster");
        XWPFDocumentManipulator.bindImageToField(document, "poster", imageDTO);

        return CompletableFuture.completedFuture(document);
    }

    private final Map<String, Integer> imageFormatMapper;
}
