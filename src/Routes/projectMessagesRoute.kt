package com.example.Routes

import com.example.database.DatabaseObject
import com.example.models.MySession
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

fun Routing.projectMessages() {
    authenticate("login") {
        route("/project/{id}/messages") {
            get {
                val session = call.sessions.get<MySession>()
                val id = call.parameters["id"]!!.toInt()
                val project = DatabaseObject.getProject(id)
                val messages = DatabaseObject.getProjectMessages(id)
                val site= call.request.queryParameters["param2"]

                if (session != null) {
                    call.respond(
                        FreeMarkerContent(
                            "projectMessages.ftl",
                            mapOf("project" to project, "data" to session, "messages" to messages, "site" to site)
                        )
                    )

                }
            }

            post {
                val session = call.sessions.get<MySession>()
                val id = call.parameters["id"]!!.toInt()
                val post = call.receiveParameters()
                val timestamp= System.currentTimeMillis()

                if(session!= null){
                    DatabaseObject.createMessage(id, session.email, post["text"].toString(), timestamp)
                }

                call.respondRedirect("/project/${id}/messages", permanent = true)
            }

        }
    }
}