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

    /**
     * Повертає бін словника, що слугує для встановлення відповідності між API
     * додатку та API джерела даних.
     * @param env бін для можливості отримання значень властивостей
     * @return бін словника, що встановлює відповідності API
     */
    @Bean
    public RequestParamMapper requestParamMapper(final Environment env) {
        return new RequestParamMapper(env);
    }
}
