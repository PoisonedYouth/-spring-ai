package com.poisonedyouth.springai.chat.infrastructure

import com.poisonedyouth.springai.chat.domain.ChatMessage
import com.poisonedyouth.springai.chat.domain.ChatMessageOutputPort
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ChatMessageRepository(
    val mongoTemplate: ReactiveMongoTemplate,
) : ChatMessageOutputPort {
    override suspend fun save(chatMessage: ChatMessage): ChatMessage {
        mongoTemplate.save(
            ChatMessageEntity(
                id = chatMessage.id.toString(),
                prompt = chatMessage.prompt,
                response = chatMessage.response,
                createdAt = chatMessage.createdAt,
            ),
        ).awaitSingle()
        return chatMessage
    }

    override suspend fun delete(chatId: UUID) {
        mongoTemplate.remove(
            Query.query(Criteria.where("id").`is`(chatId.toString())),
            ChatMessageEntity::class.java,
        ).awaitSingleOrNull()
    }

    override suspend fun findById(chatId: UUID): ChatMessage? {
        return mongoTemplate.find<ChatMessageEntity>(
            Query.query(Criteria.where("id").`is`(chatId.toString())),
        ).awaitFirstOrNull()?.let {
            ChatMessage(
                id = UUID.fromString(it.id),
                prompt = it.prompt,
                response = it.response,
                createdAt = it.createdAt,
            )
        }
    }

    override suspend fun findAll(): List<ChatMessage> {
        return mongoTemplate.findAll(ChatMessageEntity::class.java)
            .asFlow().map {
                ChatMessage(
                    id = UUID.fromString(it.id),
                    prompt = it.prompt,
                    response = it.response,
                    createdAt = it.createdAt,
                )
            }.toList()
    }
}
