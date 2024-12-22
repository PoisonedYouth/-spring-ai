package com.poisonedyouth.springai.chat.domain

import java.time.Instant
import java.util.UUID

data class Chat(
    val id: UUID,
    val messages: List<ChatMessage>,
    val createdAt: Instant = Instant.now(),
) {

    fun addMessage(chatMessage: ChatMessage): Chat {
        return this.copy(
            messages = this.messages + chatMessage,
        )
    }
}

data class NewChat(
    val id: UUID,
    val createdAt: Instant = Instant.now(),
)
