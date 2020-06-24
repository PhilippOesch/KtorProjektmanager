package com.example.Routes

import com.example.models.MySession
import com.example.models.ProjectUsers
import com.example.models.Projects
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.createProjekt() {
    authenticate("login") {
        route("/createProjekt") {
            get {
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

            post {
                val session = call.sessions.get<MySession>()

                val post = call.receiveParameters()
                if (session != null) {
                    transaction {
                        val pId = Projects.insert {
                            it[name] = post["name"].toString()
                            it[description] = post["description"].toString()
                        } get Projects.id

                        ProjectUsers.insert {
                            it[projectId] = pId
                            it[userId] = session.email
                        }
                    }
                }
                call.respondRedirect("/", permanent = true)
            }
        }
    }
}