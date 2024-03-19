package kz.qwertukg.plugins

import io.ktor.server.config.*
import java.io.File

class JsonService(private val config: ApplicationConfig) {
    val defaultLang = "en"
    private val path = config.property("app.dataPath").getString()

    fun getDataByLang(lang: String): String {
        return File("$path/$lang/cv.json").readText()
    }
}