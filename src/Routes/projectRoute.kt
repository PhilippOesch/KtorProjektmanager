package com.example.Routes

import com.example.enums.TaskStatus
import com.example.models.*
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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.and

fun Routing.project() {
    authenticate("login") {
        route("/project/{id}"){
            get {
                val session = call.sessions.get<MySession>()

                val id = call.parameters["id"]!!.toInt()
                val projects = transaction {
                    Projects.select { Projects.id eq id }.map { Projects.toProject(it) }
                }
                val users = transaction {
                    ProjectUsers.join(Users, JoinType.INNER, additionalConstraint = { ProjectUsers.projectId eq id })
                        .selectAll().map { Users.toUser(it) }
                }

                val tasks= transaction {
                    Tasks.select { Tasks.pid eq id }.map { Tasks.toTask(it) }
                }

                if (session != null && tasks.isNotEmpty()) {
                    call.respond(FreeMarkerContent("project.ftl", mapOf("data" to session, "project" to projects.first(), "users" to users, "tasks" to tasks)))
                } else if(session != null){
                    call.respond(FreeMarkerContent("project.ftl", mapOf("data" to session, "project" to projects.first(), "users" to users)))
                }
            }

            post{
                val id = call.parameters["id"]!!.toInt()
                val post = call.receiveParameters()
                val session = call.sessions.get<MySession>()

                transaction {
                    val users= Users.select { Users.email eq post["email"].toString() }.map { Users.toUser(it) }

                    val projectUser= ProjectUsers.select{ ProjectUsers.projectId eq id and (ProjectUsers.userId eq post["email"].toString()) }.map{
                        ProjectUsers.toProjectUser(it)
                    }

                    if(users.isNotEmpty() && projectUser.isEmpty() ){
                        ProjectUsers.insert {
                            it[projectId]= id
                            it[userId]= post["email"].toString()
                        }
                    }
                }


                call.respondRedirect("/project/${id}", permanent = true)
            }
        }

        route("/project/{id}/createtask"){
            get {
                val session = call.sessions.get<MySession>()
                val pid = call.parameters["id"]!!.toInt()

                if (session != null) {
                    call.respond(FreeMarkerContent("createTask.ftl", mapOf("data" to session, "projectId" to pid)))
                }
            }

            post{
                val id = call.parameters["id"]!!.toInt()
                val post = call.receiveParameters()

                transaction {
                    Tasks.insert {
                        it[name]= post["name"].toString()
                        it[description]= post["description"].toString()
                        it[status]= TaskStatus.OPEN.statustype
                        it[pid]= id
                    }
                }

                call.respondRedirect("/project/${id}", permanent = true)
            }
        }
    }
}