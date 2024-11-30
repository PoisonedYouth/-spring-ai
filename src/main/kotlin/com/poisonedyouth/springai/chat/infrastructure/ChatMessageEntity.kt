package com.poisonedyouth.springai.chat.infrastructure

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class ChatMessageEntity(
    @Id
    val id: String,
    val prompt: String,
    val response: String,
    val createdAt: Instant,
)
