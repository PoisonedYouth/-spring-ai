package com.poisonedyouth.springai.chat.infrastructure

import io.netty.channel.ChannelOption
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun webClient(): WebClient {
        val httpClient: HttpClient =
            HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)

        val webClient =
            WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .build()
        return webClient
    }
}
