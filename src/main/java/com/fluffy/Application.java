package com.fluffy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Клас, що містить точку входу в додаток.
 * @author Сивоконь Вадим
 */
@SpringBootApplication(scanBasePackages = "com.fluffy")
public class Application {
    private Application() { }

    /**
     * Точка входу в додаток.
     * @param args параметри запуску
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
