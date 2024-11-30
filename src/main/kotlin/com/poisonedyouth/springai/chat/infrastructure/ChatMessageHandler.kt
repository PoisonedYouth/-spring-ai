package com.poisonedyouth.springai.chat.infrastructure

import com.poisonedyouth.springai.chat.domain.ChatMessageInputPort
import com.poisonedyouth.springai.chat.domain.NewChatMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
class ChatMessageHandler(
    private val chatMessageInputPort: ChatMessageInputPort,
) {
    @PostMapping("/chat")
    suspend fun createChatMessage(
        @RequestBody chatMessageRequest: ChatMessageRequest,
    ): ResponseEntity<ChatMessageResponse> {
        return chatMessageInputPort.create(
            NewChatMessage(
                id = UUID.randomUUID(),
                prompt = chatMessageRequest.prompt,
                createdAt = Instant.now(),
            ),
        ).let {
            ResponseEntity.ok(
                ChatMessageResponse(
                    id = it.id,
                    prompt = it.prompt,
                    response = it.response,
                    createdAt = it.createdAt,
                ),
            )
        }
    }

    @GetMapping("/chat/{id}")
    suspend fun getChatMessage(
        @PathVariable id: UUID,
    ): ResponseEntity<ChatMessageResponse> {
        return chatMessageInputPort.get(id).let {
            if (it == null) {
                ResponseEntity.notFound().build()
            } else {
                ResponseEntity.ok(
                    ChatMessageResponse(
                        id = it.id,
                        prompt = it.prompt,
                        response = it.response,
                        createdAt = it.createdAt,
                    ),
                )
            }
        }
    }

    @DeleteMapping("/chat/{id}")
    suspend fun deleteChatMessage(
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        chatMessageInputPort.delete(id)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("/chat")
    suspend fun getAllChatMessages(): ResponseEntity<List<ChatMessageResponse>> {
        return chatMessageInputPort.getAll().map {
            ChatMessageResponse(
                id = it.id,
                prompt = it.prompt,
                response = it.response,
                createdAt = it.createdAt,
            )
        }.let { ResponseEntity.ok(it) }
    }
}

data class ChatMessageRequest(
    val prompt: String,
)

data class ChatMessageResponse(
    val id: UUID,
    val prompt: String,
    val response: String,
    val createdAt: Instant,
)
