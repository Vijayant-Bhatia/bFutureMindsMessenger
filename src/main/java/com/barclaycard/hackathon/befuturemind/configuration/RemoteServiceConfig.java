package com.barclaycard.hackathon.befuturemind.configuration;

import com.barclaycard.hackathon.befuturemind.interceptor.ServiceHeaderInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created by vbhatia on 11/22/2017.
 */
@Configuration
public class RemoteServiceConfig {

    @Bean
    public RestTemplate facebookGraphRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate servicesRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new ServiceHeaderInterceptor());
        return restTemplate;
    }

    @Bean
    public RestTemplate appServiceRestTemplate() {
        return new RestTemplate();
    }
}
