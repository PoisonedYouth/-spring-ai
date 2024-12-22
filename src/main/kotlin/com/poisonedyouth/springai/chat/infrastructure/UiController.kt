package com.poisonedyouth.springai.chat.infrastructure

import com.poisonedyouth.springai.chat.domain.ChatInputPort
import com.poisonedyouth.springai.chat.domain.NewChat
import com.poisonedyouth.springai.chat.domain.NewChatMessage
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


@Controller
class UiController(
    private val chatInputPort: ChatInputPort,
    private val markdownService: MarkdownService,
) {
    @GetMapping(value = ["/index"])
    suspend fun showIndexView(model: Model): String {
        model.addAttribute("chats", chatInputPort.getAll().sortedByDescending {
            it.createdAt
        }.map { chat ->
            ChatViewModel(
                id = chat.id.toString(),
                messages = chat.messages.map {
                    ChatMessageViewModel(
                        id = it.id.toString(),
                        prompt = it.prompt,
                        response = markdownService.markdownToHtml(it.response),
                        createdAt = it.createdAt.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    )
                },
                createdAt = chat.createdAt.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

            )

        })
        return "index"
    }

    @GetMapping(value = ["/new"])
    suspend fun showNewChatView(model: Model): String {
        model.addAttribute("newChat", NewChatMessageViewModel())
        return "new"
    }

    @GetMapping(value = ["/edit/{chatId}"])
    suspend fun editMessage(@PathVariable chatId: UUID, model: Model): String {
        val chat = chatInputPort.get(chatId) ?: error("Chat with id $chatId does not exist")
        model.addAttribute(
            "chatResponse", ChatViewModel(
                id = chat.id.toString(),
                messages = chat.messages.map {
                    ChatMessageViewModel(
                        id = it.id.toString(),
                        prompt = it.prompt,
                        response = markdownService.markdownToHtml(it.response),
                        createdAt = it.createdAt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    )
                },
                createdAt = chat.createdAt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            )
        )
        model.addAttribute("newChat", NewChatMessageViewModel(
            chatId = chat.id.toString(),
        ))
        return "edit"
    }

    @PostMapping(value = ["/new"])
    suspend fun addMessage(@ModelAttribute newChat: NewChatMessageViewModel, model: Model): String {
        val chatId = if(!newChat.chatId.isNullOrBlank()) {
            UUID.fromString(newChat.chatId)
        }else  {
            null
        }
        val chat = if (chatId != null) {
            chatInputPort.get(chatId) ?: error("Chat with id $chatId does not exist")
        } else {
            chatInputPort.create(
                NewChat(id = UUID.randomUUID())
            )
        }


        val updatedChat = chatInputPort.addMessage(
            chatId = chat.id,
            newChatMessage = NewChatMessage(
                id = UUID.randomUUID(),
                prompt = newChat.prompt,
            )
        )


        model.addAttribute(
            "chatResponse", ChatViewModel(
                id = updatedChat.id.toString(),
                messages = updatedChat.messages.map {
                    ChatMessageViewModel(
                        id = it.id.toString(),
                        prompt = it.prompt,
                        response = markdownService.markdownToHtml(it.response),
                        createdAt = it.createdAt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    )
                },
                createdAt = chat.createdAt.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            )
        )
        model.addAttribute("newChat", NewChatMessageViewModel(
            chatId = updatedChat.id.toString(),
        ))
        return "new"
    }

    @GetMapping(value = ["/delete/{chatId}"])
    suspend fun deleteMessage(@PathVariable chatId: UUID): String {
        chatInputPort.delete(chatId)
        return "redirect:/index"
    }
}

data class ChatViewModel(
    val id: String,
    val messages: List<ChatMessageViewModel>,
    val createdAt: String
)

data class ChatMessageViewModel(
    val id: String,
    val prompt: String,
    val response: String,
    val createdAt: String,
)

data class NewChatMessageViewModel(
    val chatId: String? = null,
    val prompt: String = "",
)