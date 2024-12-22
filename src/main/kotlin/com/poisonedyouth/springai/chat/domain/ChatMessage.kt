package com.poisonedyouth.springai.chat.domain

import java.time.Instant
import java.util.UUID

data class ChatMessage(
    val id: UUID,
    val prompt: String,
    val response: String,
    val createdAt: Instant,
)

data class NewChatMessage(
    val id: UUID,
    val prompt: String,
    val createdAt: Instant = Instant.now(),
)
