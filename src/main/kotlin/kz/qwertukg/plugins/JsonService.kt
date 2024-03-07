package kz.qwertukg.plugins

import io.ktor.server.application.*
import java.io.File

class JsonService(private val env: ApplicationEnvironment) {
    val defaultLang = "en"

    fun getDataByLang(lang: String): String {
        val path = env.config.propertyOrNull("app.dataPath")?.getString()
            ?: throw NullPointerException("Data source not found")

        return File("$path/$lang/cv.json").readText()
    }
}