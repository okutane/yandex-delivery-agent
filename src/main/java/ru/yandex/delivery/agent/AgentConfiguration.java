package ru.yandex.delivery.agent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Feign;
import feign.Logger.Level;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

@Configuration
public class AgentConfiguration {
    @Bean
    B2bClient b2bClient() {
        String token = System.getenv("ACCESS_TOKEN");
        String url = System.getenv("API_URL");

        if (token == null) {
            throw new IllegalStateException("Missing or invalid property value for ACCESS_TOKEN");
        }

        return Feign.builder()
                .requestInterceptor(t -> {
                    t.header("Authorization", "Bearer " + token);
                })
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger())
                .logLevel(Level.FULL)
                .target(B2bClient.class, url);
    }

    @Bean
    CallbackClient callbackClient() {
        String url = System.getenv("CALLBACK_URL");

        if (url == null) {
            throw new IllegalStateException("Missing or invalid property value for CALLBACK_URL");
        }

        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Slf4jLogger())
                .logLevel(Level.FULL)
                .target(CallbackClient.class, url);
    }
}
