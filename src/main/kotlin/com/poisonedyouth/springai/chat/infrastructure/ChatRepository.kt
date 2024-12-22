package com.poisonedyouth.springai.chat.infrastructure

import com.poisonedyouth.springai.chat.domain.Chat
import com.poisonedyouth.springai.chat.domain.ChatMessage
import com.poisonedyouth.springai.chat.domain.ChatOutputPort
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.awaitFirstOrNull
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ChatRepository(
    val entityTemplate: R2dbcEntityTemplate,
) : ChatOutputPort {
    override suspend fun save(chat: Chat): Chat {
        entityTemplate.insert(
            ChatEntity(
                id = chat.id.toString(),
                createdAt = chat.createdAt,
            )
        ).awaitSingle()
        return chat
    }

    override suspend fun addMessage(chatId: UUID, message: ChatMessage): Chat {
        val chat = findById(chatId) ?: error("Chat with id $chatId does not exist")
        entityTemplate.insert(
            ChatMessageEntity(
                id = message.id.toString(),
                prompt = message.prompt,
                response = message.response,
                createdAt = message.createdAt,
                chatId = chat.id.toString()
            )
        ).awaitSingleOrNull()
        return chat.addMessage(message)
    }

    override suspend fun delete(chatId: UUID) {
        entityTemplate.delete(
            Query.query(where("chat_id").`is`(chatId.toString())),
            ChatMessageEntity::class.java,
        ).awaitSingleOrNull()
        entityTemplate.delete(
            Query.query(where("id").`is`(chatId.toString())),
            ChatEntity::class.java,
        ).awaitSingleOrNull()
    }

    override suspend fun findById(chatId: UUID): Chat? {
        return entityTemplate.select(ChatEntity::class.java)
            .matching(
                Query.query(where("id").`is`(chatId.toString()))
            ).awaitFirstOrNull()?.let { chat ->
                Chat(
                    id = UUID.fromString(chat.id),
                    createdAt = chat.createdAt,
                    messages = entityTemplate.select(ChatMessageEntity::class.java)
                        .matching(
                            Query.query(where("chat_id").`is`(chatId.toString()))
                        ).all().asFlow().toList().map { chatMessage ->
                            ChatMessage(
                                id = UUID.fromString(chatMessage.id),
                                prompt = chatMessage.prompt,
                                response = chatMessage.response,
                                createdAt = chatMessage.createdAt,
                            )
                        },
                )
            }
    }

    override suspend fun findAll(): List<Chat> {
        return entityTemplate.select(ChatEntity::class.java)
            .all()
            .asFlow().map { chat ->
                Chat(
                    id = UUID.fromString(chat.id),
                    messages = entityTemplate.select(ChatMessageEntity::class.java)
                        .matching(
                            Query.query(where("chat_id").`is`(chat.id))
                        ).all().asFlow().map { chatMessage ->
                            ChatMessage(
                                id = UUID.fromString(chatMessage.id),
                                prompt = chatMessage.prompt,
                                response = chatMessage.response,
                                createdAt = chatMessage.createdAt,
                            )
                        }.toList(),
                )
            }.toList()
    }
}
