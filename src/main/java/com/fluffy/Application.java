package com.fluffy;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;
import java.util.Arrays;

/**
 * Клас, що містить точку входу в додаток.
 * @author Сивоконь Вадим
 */
@SpringBootApplication(scanBasePackages = "com.fluffy")
public class Application {
    /**
     * Для забезпечення логування.
     */
    private static final Logger logger = Logger.getLogger(Application.class);

    /**
     * Створює бін основного класу додатку.
     */
    public Application() {
        logger.debug("Бін основного класу " + getClass().getSimpleName() + " додатку створений");
    }

    /**
     * Точка входу в додаток.
     * @param args параметри запуску
     */
    public static void main(final String[] args) {
        logger.info("Додаток розпочав свою роботу із параметрами: " + Arrays.toString(args));
        SpringApplication.run(Application.class, args);
    }

    /**
     * Для можливості виконання додаткових дій на момент завершення роботи
     * додатку.
     */
    @PreDestroy
    public void onExit() {
        logger.info("Додаток завершив свою роботу");
    }
}
