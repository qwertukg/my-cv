package kz.qwertukg

import io.ktor.server.application.*
import kz.qwertukg.gdp.configureRoutingEvents
import kz.qwertukg.plugins.configureRoutingHtml

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRoutingHtml()
    configureRoutingEvents()
}
