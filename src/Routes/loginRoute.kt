package com.example.Routes

import com.example.models.MySession
import com.example.models.Users
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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.login(){
    route("/login") {
        get {
            call.respond(FreeMarkerContent("login.ftl", null))
        }
        authenticate("login") {
            post {
                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                val users = transaction {
                    Users.select { Users.email eq principal.name}.map { Users.toUser(it) }
                }
                call.sessions.set("SESSION", MySession(users.first().name, users.first().email))
                call.respondRedirect("/", permanent = false)
            }
        }

    }
}