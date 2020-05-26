package com.example.Routes

import com.example.models.MySession
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.sessions.clear
import io.ktor.sessions.sessions

fun Routing.logout(){
    authenticate("login") {
        get("/logout") {
            call.sessions.clear<MySession>()
            call.respondRedirect("/", permanent = false)
        }
    }
}