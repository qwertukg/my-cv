package kz.qwertukg.plugins

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import kotlinx.html.*

fun DIV.renderJsonObject(node: JsonNode, key: String = "") {
    when (node.nodeType) {
        JsonNodeType.OBJECT -> renderObjectNode(node, key)
        JsonNodeType.ARRAY -> renderArrayNode(node, key)
        else -> renderNormalNode(node, key)
    }
}

fun DIV.printValue(node: JsonNode) {
    val value = node.asText()
    when (node.nodeType) {
        JsonNodeType.STRING -> printStringValue(value)
        JsonNodeType.NUMBER -> printNumberValue(value)
        else -> printDefaultValue(value)
    }
}

fun DIV.renderObjectNode(node: JsonNode, key: String) {
    div("object") {
        printKey(key)
        node.fieldNames().forEach {
            val subNode = node.findValue(it)
            renderJsonObject(subNode, it)
        }
    }
}

fun DIV.renderArrayNode(node: JsonNode, key: String) {
    div("array") {
        printKey(key)
        node.forEach {
            renderJsonObject(it)
        }
    }
}

fun DIV.renderNormalNode(node: JsonNode, key: String) {
    div("value") {
        printKey(key)
        printValue(node)
    }
}

fun DIV.printKey(key: String) {
    val ppKey = key.replace("_", " ").capitalize()
    if (key.isNotBlank()) {
        b { +"$ppKey: " }
    }
}

fun DIV.printStringValue(value: String) {
    when {
        value.startsWith("http") -> span("link") {
            a(href = value) { +value }
        }
        "@" in value -> span("mail") {
            a(href = "mailto:$value") { +value }
        }
        else -> span("text") { +value }
    }
}

fun DIV.printNumberValue(value: String) {
    span("number") {
        +value
    }
}

fun DIV.printDefaultValue(value: String) {
    span("text") {
        +value
    }
}
