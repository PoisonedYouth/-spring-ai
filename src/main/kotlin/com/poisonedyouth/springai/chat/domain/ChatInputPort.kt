package com.poisonedyouth.springai.chat.domain

import java.util.UUID

interface ChatInputPort {
    suspend fun create(chat: NewChat): Chat

    suspend fun addMessage(
        chatId: UUID,
        newChatMessage: NewChatMessage,
    ): Chat

    suspend fun get(chatId: UUID): Chat?

    suspend fun getAll(): List<Chat>

    suspend fun delete(chatId: UUID)
}
