package kz.qwertukg.plugins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.link

fun Application.configureRoutingHtml() {
    val service = JsonService(environment.config)
    val mapper = jacksonObjectMapper()

    routing {
        get("/{lang}") {
            val lang = call.parameters["lang"] ?: service.defaultLang
            val jsonString = service.getDataByLang(lang)
            val node = mapper.readTree(jsonString)

            call.respondHtml {
                head {
                    link(rel = "stylesheet") { href = "/static/css/app.css" }
                }
                body {
                    div {
                        renderJsonObject(node)
                    }
                }
            }
        }

        get("/") {
            call.respondRedirect("/${service.defaultLang}")
        }

        staticResources("/static", "static")

        get("/favicon.ico") {
            call.respondRedirect("/static/favicon.ico")
        }
    }
}

