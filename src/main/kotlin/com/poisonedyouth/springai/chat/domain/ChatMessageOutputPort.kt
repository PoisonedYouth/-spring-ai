package com.poisonedyouth.springai.chat.domain

import java.util.UUID

interface ChatMessageOutputPort {
    suspend fun save(chatMessage: ChatMessage): ChatMessage

    suspend fun delete(chatId: UUID)

    suspend fun findById(chatId: UUID): ChatMessage?

    suspend fun findAll(): List<ChatMessage>
}
