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

fun Routing.root(){
    authenticate("login") {
        get("/") {
            val session = call.sessions.get<MySession>()

            if (session != null) {
                println(session.name);
                //call.respondText("models.kt.User is logged", null)
                call.respond(FreeMarkerContent("index.ftl", mapOf("data" to session)))
            } else {
                call.respond(FreeMarkerContent("index.ftl", null))
            }
        }
    }
}