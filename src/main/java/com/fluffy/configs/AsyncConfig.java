package com.fluffy.configs;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Клас, що відповідає за налаштування роботи асинхронних запитів.
 * @author Сивоконь Вадим
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    /**
     * Бін, що використовується для отримання змінних оточення, визначених у
     * application.yml.
     */
    private final Environment env;

    /**
     * Повертає бін, що здійснює керування властивостями асинхронних потоків.
     * @param env бін для читання значень змінних оточення
     */
    public AsyncConfig(final Environment env) {
        this.env = env;
    }

    /**
     * Перевизначений метод - встановлює для використання власний executor, що
     * відрізняється встановленими параметрами від того, що пропонується за
     * замовчуванням (SimpleAsyncTaskExecutor - не передбачає перевикористання
     * потоків, що може становити загрозу для функціонування під великим
     * навантаженням).
     * @return бін конфігурації асинхронних запитів
     */
    @Override
    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // мінімальна кількість потоків, що будуть використовуватися
        executor.setCorePoolSize(env.getProperty("application.async.core-pool-size", int.class));
        // максимальна кількість потоків
        executor.setMaxPoolSize(env.getProperty("application.async.max-pool-size", int.class));
        // додаток не буде закриватися, поки не виконається запит
        executor.setWaitForTasksToCompleteOnShutdown(env.getProperty("application.async.wait-for-tasks-to-complete-on-shutdown", boolean.class));
        // використовуваний префікс для іменування потоків
        executor.setThreadNamePrefix(env.getProperty("application.async.thread-name-prefix"));
        return executor;
    }

    /**
     * Повертає бін-конфігуратор, що може бути використаний для MVC патерну.
     * @return бін-конфігуратор асинхронних запитів на випадок використання MVC
     */
    @Bean
    protected WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(getTaskExecutor());
            }
        };
    }

    /**
     * Повертає бін, що буде підтримувати функціонування багатопоточності на
     * основі створення біна-конфігуратора.
     * @return бін для підтримки функціонування багатопоточності
     */
    @Bean
    protected ConcurrentTaskExecutor getTaskExecutor() {
        return new ConcurrentTaskExecutor(this.getAsyncExecutor());
    }

    /**
     * Повертає об'єкт-обробник виняткових ситуацій, пов'язаних із
     * використанням асинхронності.
     * @return обробник виняткових ситуацій, пов'язаних із використанням
     *         асинхронності
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
