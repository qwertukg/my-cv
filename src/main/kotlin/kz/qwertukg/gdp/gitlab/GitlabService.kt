package kz.qwertukg.gdp.gitlab

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.cert.X509Certificate
import java.time.LocalDate
import javax.net.ssl.X509TrustManager

class GitlabService(private val config: ApplicationConfig) {
    val host = config. propertyOrNull("app.host")?.getString()
    val token = config.propertyOrNull("app.token")?.getString()
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })

        }
        engine {
            https {
                trustManager = object : X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                }
            }
        }
    }

    suspend inline fun <reified T> getOne(resource: String): T {
        val url = "$host/$resource"
        val response = client.get(url) {
            header("PRIVATE-TOKEN", token)
        }
        return response.body<T>()
    }

    suspend inline fun <reified T> getPage(resource: String, page: Int, after: LocalDate ,scopeAll: Boolean = false): List<T> {
        // "2024-03-01T00:00:00.000Z"
        val url = if (scopeAll) "$host/$resource?per_page=100&page=$page&after=$after&scope=all"
        else "$host/$resource?per_page=100&page=$page&after=$after"

        val response = client.get(url) {
            header("PRIVATE-TOKEN", token)
        }

        return response.body<List<T>>()
    }

    suspend inline fun <reified T> getAll(resource: String, after: LocalDate): List<T> {
        val all = mutableListOf<T>()
        var page = 1
        while (true) {
            val users = getPage<T>(resource, page, after)
            if (users.isEmpty()) break
            all.addAll(users)
            page++
        }
        return all
    }
}



