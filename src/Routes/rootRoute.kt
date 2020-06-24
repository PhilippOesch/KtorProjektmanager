package com.example.Routes

import com.example.AuthorizationException
import com.example.models.MySession
import com.example.models.ProjectUsers
import com.example.models.Projects
import com.example.models.Users
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.root() {
    authenticate("login") {
        get("/") {
            val session = call.sessions.get<MySession>()

/*                    val projects= transaction {
                        Projects.selectAll().map { Projects.toProject(it) }
                    }*/

            if (session != null) {
                val projects = transaction {
                    ProjectUsers.join(
                        Projects,
                        JoinType.INNER,
                        additionalConstraint = { ProjectUsers.userId eq session.email }).selectAll().map { Projects.toProject(it) }

                }
                println(session.name);
                //call.respondText("models.kt.User is logged", null)
                call.respond(FreeMarkerContent("index.ftl", mapOf("data" to session, "projects" to projects)))
            } else {
                throw AuthorizationException()
            }
        }
    }
}