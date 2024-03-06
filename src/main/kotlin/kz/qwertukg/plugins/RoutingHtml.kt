package kz.qwertukg.plugins

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureRoutingHtml() {
    val service = Service(environment)

    routing {
        get("/{lang}") {
            val lang = call.parameters["lang"] ?: service.defaultLang
            val cvData = service.getDataByLang(lang)
            val node = jacksonObjectMapper().readTree(cvData)

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
