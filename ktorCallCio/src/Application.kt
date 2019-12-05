package com.foo.ktorclientscale

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import io.ktor.application.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.features.CallLogging
import io.ktor.response.respond
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(CallLogging)
    val url = ConfigFactory.load().extract<String>("ktorclientscale.clientUrl")

    routing {
        get("/") {
            HttpClient(CIO).use { client ->
                client.get<HttpResponse>(url)
                    .let { call.respond(it.readText()) }
            }
        }
    }
}
