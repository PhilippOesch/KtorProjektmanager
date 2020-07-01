package com.example.Routes

import com.example.database.DatabaseObject
import com.example.models.MySession

import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.sessions

fun Routing.login(){
    route("/login") {
        get {
            call.respond(FreeMarkerContent("login.ftl", null))
        }
        authenticate("login") {
            post {
                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")

                val user= DatabaseObject.getUser(principal.name)
                if(user!= null) {
                    call.sessions.set("SESSION", MySession(user.name, user.email))
                    call.respondRedirect("/", permanent = false)
                }
            }
        }

    }
}