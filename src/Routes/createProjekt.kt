package com.example.Routes

import com.example.models.MySession
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Routing.createProjekt() {
    authenticate("login") {
        get("/createProjekt") {
            val session = call.sessions.get<MySession>()

            if (session != null) {
                call.respond(
                    FreeMarkerContent(
                        "createProjekt.ftl",
                        mapOf("data" to session)
                    )
                )
            }
        }
    }
}