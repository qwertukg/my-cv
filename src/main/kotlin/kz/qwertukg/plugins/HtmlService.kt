package kz.qwertukg.plugins

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import kotlinx.html.*


fun DIV.renderJsonObject(node: JsonNode, key: String = "") {
        when (node.nodeType) {
            JsonNodeType.OBJECT -> {
                div("object") {
                    printKey(key)
                    node.fieldNames().forEach {
                        val subNode = node.findValue(it)
                        renderJsonObject(subNode, it)
                    }
                }
            }
            JsonNodeType.ARRAY -> {
                div("array") {
                    printKey(key)
                    node.forEach {
                        renderJsonObject(it)
                    }
                }
            }
            else -> {
                div("value") {
                    printKey(key)
                    printValue(node)
                }
            }
        }


    }

fun DIV.printKey(key: String) {
    val ppKey = key.replace("_", " ").capitalize()
    if (key.isNotBlank()) {
        b { +"$ppKey: " }
    }
}

fun DIV.printValue(node: JsonNode) {
    val value = node.asText()
    when (node.nodeType) {
        JsonNodeType.STRING -> {
            if (value.startsWith("http")) {
                span("link") {
                    a(href = value) { +value }
                }
            } else if ("@" in value) {
                span("mail") {
                    a(href = "mailto:$value") { +value }
                }
            } else {
                span("text") { +value }
            }
        }
        JsonNodeType.NUMBER -> {
            span("number") { +value }
        }
        else -> {
            span("text") { +value }
        }
    }
}
