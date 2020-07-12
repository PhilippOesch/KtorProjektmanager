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

fun Routing.projectSettings(){
    authenticate("login") {
        route("/project/{id}/settings"){
            get {
                val session = call.sessions.get<MySession>()
                val id = call.parameters["id"]!!.toInt()

                val project= DatabaseObject.getProject(id)
                val users = DatabaseObject.getProjectUsers(id)

                if (session != null) {
                    call.respond(FreeMarkerContent("projectSettings.ftl", mapOf("data" to session, "project" to project, "users" to users, "site" to "settings")))
                }
            }

            post{
                val id = call.parameters["id"]!!.toInt()
                val post = call.receiveParameters()
                val session = call.sessions.get<MySession>()

                DatabaseObject.addUsertoProject(id, post["email"].toString())

                call.respondRedirect("/project/${id}/settings", permanent = true)
            }
        }
    }
}