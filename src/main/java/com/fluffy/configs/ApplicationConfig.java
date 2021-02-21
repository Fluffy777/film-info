package com.fluffy.configs;

import com.fluffy.util.RequestParamMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

/**
 * Клас, що відповідає за конфігурацію бінів.
 * @author Сивоконь Вадим
 */
@Configuration
public class ApplicationConfig {
    /**
     * Повертає бін, що відповідає за отримання інформації від джерела даних.
     * @return бін для отримання інформації від джерела даних
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RequestParamMapper requestParamMapper(Environment env) {
        return new RequestParamMapper(env);
    }
}
