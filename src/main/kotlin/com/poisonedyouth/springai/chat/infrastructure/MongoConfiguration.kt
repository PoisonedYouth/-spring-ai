package com.poisonedyouth.springai.chat.infrastructure

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories
class MongoConfiguration : AbstractReactiveMongoConfiguration() {
    override fun getDatabaseName(): String {
        return "chat"
    }

    @Bean
    override fun reactiveMongoClient(): MongoClient {
        return MongoClients.create("mongodb://root:mongopw@localhost:27017")
    }

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(reactiveMongoClient(), databaseName)
    }
}
