package com.poisonedyouth.springai.chat.domain

import java.util.UUID

interface ChatOutputPort {
    suspend fun save(chat: Chat): Chat

    suspend fun addMessage(chatId: UUID, message: ChatMessage): Chat

    suspend fun delete(chatId: UUID)

    suspend fun findById(chatId: UUID): Chat?

    suspend fun findAll(): List<Chat>
}
