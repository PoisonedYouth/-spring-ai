package com.poisonedyouth.springai.chat.infrastructure

import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Service

@Service
class MarkdownService {
    fun markdownToHtml(markdown: String?): String {
        val parser: Parser = Parser.builder().build()
        val document: Node = parser.parse(markdown)
        return HtmlRenderer.builder().build().render(document)
    }
}
