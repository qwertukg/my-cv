package kz.qwertukg.plugins

import io.ktor.server.application.*
import java.io.File

class Service(private val env: ApplicationEnvironment) {
    val defaultLang = "ru"

    fun getDataByLang(lang: String): String {
        val path = env.config.propertyOrNull("app.pathToData")?.getString()
            ?: throw NullPointerException("Data source not found")

        return File("$path/$lang/cv.json").readText()
    }
}