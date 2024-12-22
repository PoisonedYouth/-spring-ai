package com.poisonedyouth.springai.chat.application

import com.poisonedyouth.springai.chat.domain.Chat
import com.poisonedyouth.springai.chat.domain.ChatMessage
import com.poisonedyouth.springai.chat.domain.ChatInputPort
import com.poisonedyouth.springai.chat.domain.ChatOutputPort
import com.poisonedyouth.springai.chat.domain.NewChat
import com.poisonedyouth.springai.chat.domain.NewChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatMessageService(
    private val chatMessageOutputPort: ChatOutputPort,
    private val ollamaChatModel: OllamaChatModel,
) : ChatInputPort {

    override suspend fun create(chat: NewChat): Chat {
        return chatMessageOutputPort.save(
            Chat(
                id = chat.id,
                createdAt = chat.createdAt,
                messages = emptyList()
            )
        )
    }

    override suspend fun addMessage(chatId: UUID, newChatMessage: NewChatMessage): Chat {
        val response =
            withContext(Dispatchers.IO) {
                withTimeout(60000) {
                    ollamaChatModel.stream(Prompt(newChatMessage.prompt)).asFlow()
                }
            }

        val chat = chatMessageOutputPort.findById(chatId) ?: error("Chat does not exist")
        return chatMessageOutputPort.addMessage(
            chatId = chat.id,
            message = ChatMessage(
                id = UUID.randomUUID(),
                prompt = newChatMessage.prompt,
                response = response.toList().joinToString("") { it.result.output.content },
                createdAt = newChatMessage.createdAt,
            )
        )
    }

    override suspend fun get(chatId: UUID): Chat? {
        return chatMessageOutputPort.findById(chatId)
    }

    override suspend fun getAll(): List<Chat> {
        return chatMessageOutputPort.findAll()
    }

    override suspend fun delete(chatId: UUID) {
        return chatMessageOutputPort.delete(chatId)
    }
}
