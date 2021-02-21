package com.fluffy.util;

import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Клас біна, що займається співставленням звернень до API, щоб мати можливість
 * зручного налаштування відповідності. Наприклад, встановлюється зв'язок:
 * title -> t. Аналогічним чином це працює для окремих значень параметрів.
 * Може бути використаний для простої валідації параметрів та їх значень.
 * @author Сивоконь Вадим
 */
public class RequestParamMapper {
    /**
     * Бін, що використовується для отримання змінних оточення, визначених у
     * application.properties.
     */
    private final Environment env;

    /**
     * Виконує роль асоціативного масиву для здійснення співставлень звернень
     * до API додатку із зверненнями до API джерела даних.
     */
    private final Map<String, String> params;

    /**
     * Виконує роль асоціативного масиву для здійснення співставлень значень
     * параметрів під час звернень до API додатку із значеннями параметрів
     * під час звернень до API джерела даних.
     */
    private final Map<String, Map<String, String>> values;

    /**
     * Створює об'єкт (бін), що займається співставленнями звернень до API.
     * @param env
     */
    public RequestParamMapper(Environment env) {
        this.env = env;
        params = new HashMap<>();
        params.put(env.getProperty("application.api.param.title"), env.getProperty("application.data-source.api.param.title"));
        params.put(env.getProperty("application.api.param.year"), env.getProperty("application.data-source.api.param.year"));
        params.put(env.getProperty("application.api.param.plot"), env.getProperty("application.data-source.api.param.plot"));
        params.put(env.getProperty("application.api.param.id"), env.getProperty("application.data-source.api.param.id"));
        params.put(env.getProperty("application.api.param.format"), env.getProperty("application.data-source.api.param.format"));

        values = new HashMap<>();
        Map<String, String> currentMap = new HashMap<>();
        currentMap.put("short", "short");
        currentMap.put("full", "full");
        values.put(env.getProperty("application.api.param.plot"), currentMap);

        currentMap = new HashMap<>();
        currentMap.put("xml", "xml");
        currentMap.put("json", "json");
        currentMap.put("docx", ""); // передачається лише нашим додатком
        values.put(env.getProperty("application.api.param.format"), currentMap);
    }

    /**
     * Повертає співставлення - назву параметра для вказаної назви.
     * @param param назва параметра додатку
     * @return назва параметра джерела даних
     */
    public String mapParam(String param) {
        if (!isParamValid(param)) {
            return null;
        }
        return params.get(param);
    }

    /**
     * Повертає співставлення - значення параметра для вказаної його назви.
     * @param param назва параметра для додатку
     * @param value значення параметра для додатку
     * @return значення відповідного параметра для джерела даних
     */
    public String mapValue(String param, String value) {
        if (!isParamValid(param)) {
            return null;
        }
        return values.get(param).get(value);
    }

    /**
     * Перевіряє, чи існує вказана назва параметра для додатку.
     * @param param назва параметра для додатку
     * @return існує чи ні
     */
    public boolean isParamValid(String param) {
        return params.containsKey(param);
    }

    /**
     * Перевіряє, чи існує вказане значення параметра для додатку.
     * @param param назва параметра
     * @param value значення параметра
     * @return існує чи ні
     */
    public boolean isParameterValueValid(String param, String value) {
        if (!isParamValid(param)) {
            return false;
        }
        return values.get(param).get(value) != null;
    }
}
