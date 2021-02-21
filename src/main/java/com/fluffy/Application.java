package com.fluffy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Клас, що містить точку входу в додаток.
 * @author Сивоконь Вадим
 */
@SpringBootApplication(scanBasePackages = "com.fluffy")
public class Application {
    /**
     * Точка входу в додаток.
     * @param args параметри запуску
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println();

        /**
         * Tasks:
         * 1. Комментарии (+репозиторий)
         * 2. Логирование
         *
         *
         * -----
         * 1. Spring не резолвит application-property в params для @RequestMapping
         * 2. Нужно ли объявлять константы статическими, если они касаются бина,
         *    что будет создаваться единожды?
         * 3.
         * -----
         * http://localhost:8080/film?title=Pirates&year=&plot=short&format=json
         * http://localhost:8080/film?title=Pirates&year=1987&plot=short&format=json
         * http://localhost:8080/film?title=City&year=&plot=short&format=json
         * http://localhost:8080/film?title=Alien&year=&plot=short&format=json
         * http://localhost:8080/film?title=Jagten&year=&plot=short&format=json
         * http://localhost:8080/film?title=Metropolis&year=&plot=short&format=json
         * http://localhost:8080/film?title=Soul&year=&plot=short&format=json
         */
    }
}
