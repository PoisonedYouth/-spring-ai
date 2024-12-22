package com.poisonedyouth.springai.chat.infrastructure

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
class DatabaseConfiguration {
    // @Bean
    fun initializer(connectionFactory: ConnectionFactory) =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(
                CompositeDatabasePopulator()
                    .apply {
                        addPopulators(ResourceDatabasePopulator(FileSystemResource("src/main/resources/sql/init.sql")))
                    },
            )
        }
}
