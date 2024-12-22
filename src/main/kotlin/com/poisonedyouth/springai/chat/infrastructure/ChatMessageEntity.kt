package com.poisonedyouth.springai.chat.infrastructure

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant


@Table("chats")
data class ChatEntity(
    @Id val id: String,
    val createdAt: Instant,
)

@Table("chat_messages")
data class ChatMessageEntity(
    @Id val id: String,
    val chatId: String,
    val prompt: String,
    val response: String,
    val createdAt: Instant,
)