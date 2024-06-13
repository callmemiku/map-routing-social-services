package ru.moscow.hackathon.coordinator.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfiguration {


    @Value("${app.web-client.connection-timeout}")
    Integer timeout;

    @Bean
    public WebClient webClient() {

        var httpClient = HttpClient.create()
                .option(
                        ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        timeout
                ).responseTimeout(
                        Duration.ofMillis(timeout)
                ).doOnConnected(conn ->
                        conn.addHandlerLast(
                                new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS)
                        ).addHandlerLast(
                                new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS)
                        )
                );

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(log())
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient)
                ).build();
    }

    private static ExchangeFilterFunction log() {
        return (ClientRequest request, ExchangeFunction next) -> {
            log.info("WEB CLIENT | Performing {} {} request", request.method(), request.url());
            return next.exchange(request)
                    .doOnNext((ClientResponse response) -> {
                        log.info("WEB CLIENT | {} {} Respond with {} status code", request.method(), request.url(),
                                response.statusCode().value());
                    });
        };
    }
}
