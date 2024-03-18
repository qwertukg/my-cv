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
import kotlinx.serialization.Serializable
import kz.qwertukg.gdp.events.EventService
import kz.qwertukg.gdp.gitlab.*
import java.time.LocalDate

fun Application.configureRoutingEvents() {
    val gitlabService = GitlabService(environment.config)
    val eventService = EventService(gitlabService)

    routing {
        staticResources("/static", "static")
        install(ContentNegotiation) {
            json()
        }


//        get("/e/{projectId}/{after}") {
//            val projectId = call.parameters["projectId"]!!.toInt()
//            val after = call.parameters["after"]!!.toString()
//            val afterDate = LocalDate.parse(after)
//            val project = eventService.getProjectEventsByUser(projectId, afterDate)
//            val chartJsData = eventService.getChartJsData(project)
//            call.respond(chartJsData)
//        }

        post("/e") {
            val eventsRequestBody = call.receive<EventRequestBody>()
            val project = eventService.getProjectEventsByUserAndEvents(eventsRequestBody)
            val chartJsData = eventService.getChartJsData(project)
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

@Serializable
data class EventRequestBody(
    val projectId: Int,
    val after: String,
    val possibleEvents: List<String>,
    val possibleUsers: List<String>
)

