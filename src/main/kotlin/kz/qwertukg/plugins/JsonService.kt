package kz.qwertukg.plugins

import io.ktor.server.application.*
import io.ktor.server.config.*
import java.io.File

class JsonService(private val config: ApplicationConfig) {
    val defaultLang = "en"

    fun getDataByLang(lang: String): String {
        val path = config.propertyOrNull("app.dataPath")?.getString()
            ?: throw NullPointerException("Data source not found")

        return File("$path/$lang/cv.json").readText()
    }
}