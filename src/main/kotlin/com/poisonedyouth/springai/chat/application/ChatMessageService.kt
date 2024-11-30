package com.poisonedyouth.springai.chat.application

import com.poisonedyouth.springai.chat.domain.ChatMessage
import com.poisonedyouth.springai.chat.domain.ChatMessageInputPort
import com.poisonedyouth.springai.chat.domain.ChatMessageOutputPort
import com.poisonedyouth.springai.chat.domain.NewChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatMessageService(
    private val chatMessageOutputPort: ChatMessageOutputPort,
    private val ollamaChatModel: OllamaChatModel,
) : ChatMessageInputPort {
    override suspend fun create(chatMessage: NewChatMessage): ChatMessage {
        val response =
            withContext(Dispatchers.IO) {
                withTimeout(60000) {
                    ollamaChatModel.stream(chatMessage.prompt).asFlow()
                }
            }

        return chatMessageOutputPort.save(
            ChatMessage(
                id = UUID.randomUUID(),
                prompt = chatMessage.prompt,
                response = response.toList().joinToString(),
                createdAt = chatMessage.createdAt,
            ),
        )
    }

    override suspend fun get(chatId: UUID): ChatMessage? {
        return chatMessageOutputPort.findById(chatId)
    }

    override suspend fun getAll(): List<ChatMessage> {
        return chatMessageOutputPort.findAll()
    }

    override suspend fun delete(chatId: UUID) {
        return chatMessageOutputPort.delete(chatId)
    }
}
