package com.poisonedyouth.springai.chat.domain

import java.util.UUID

interface ChatMessageInputPort {
    suspend fun create(chatMessage: NewChatMessage): ChatMessage

    suspend fun get(chatId: UUID): ChatMessage?

    suspend fun getAll(): List<ChatMessage>

    suspend fun delete(chatId: UUID)
}
