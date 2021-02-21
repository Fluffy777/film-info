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

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    private final Environment env;

    public AsyncConfig(Environment env) {
        this.env = env;
    }

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

    @Bean
    protected WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(getTaskExecutor());
            }
        };
    }

    @Bean
    protected ConcurrentTaskExecutor getTaskExecutor() {
        return new ConcurrentTaskExecutor(this.getAsyncExecutor());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
