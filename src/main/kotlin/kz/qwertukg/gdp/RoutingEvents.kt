package kz.qwertukg.gdp

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kz.qwertukg.gdp.client.ChartService
import kz.qwertukg.gdp.events.EventRequestBody
import kz.qwertukg.gdp.events.EventService
import kz.qwertukg.gdp.gitlab.*

fun Application.configureRoutingEvents() {
    val gitlabService = GitlabService(environment.config)
    val eventService = EventService(gitlabService)
    val chartService = ChartService()

    routing {
        staticResources("/static", "static")
        install(ContentNegotiation) {
            json()
        }

        post("/e") {
            val eventsRequestBody = call.receive<EventRequestBody>()
            val project = eventService.getProjectEventsByUserAndEvents(eventsRequestBody)
            val chartJsData = chartService.getChartJsData(project)
            call.respond(chartJsData)
        }

        get("/c/{projectId}/{after}") {
            call.respondHtml {
                head {
                    link(rel = "stylesheet") { href = "/static/css/gdp.css" }
                    script { src = "https://cdn.jsdelivr.net/npm/chart.js" }
                }
                body {
                    div {
                        canvas { id = "myChart" }
                    }

                    script { src = "/static/js/gdp.js" }
                }
            }
        }

    }
}



