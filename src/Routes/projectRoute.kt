package com.example.Routes

import com.example.database.DatabaseObject
import com.example.models.*
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.http.parseRangesSpecifier
import io.ktor.request.receiveMultipart
import io.ktor.request.receiveParameters
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlinx.css.time
import java.io.File
import java.time.LocalDate
import java.util.*

fun Routing.project() {
    authenticate("login") {
        route("/project/{id}"){
            get {
                val session = call.sessions.get<MySession>()

                val id = call.parameters["id"]!!.toInt()
                val project= DatabaseObject.getProject(id)
                val users = DatabaseObject.getProjectUsers(id)
                val tasks = DatabaseObject.getProjectTasks(id)

                val taskWithUsers = mutableListOf<TaskWithUsers>()
                if(tasks.isNotEmpty()){
                    for(task in tasks){
                        val taskID= task.id
                        val users= DatabaseObject.getTasksUsers(taskID)

                        taskWithUsers.add(TaskWithUsers(task, users))
                    }
                }

                if (session != null && tasks.isNotEmpty()) {
                    call.respond(FreeMarkerContent("project.ftl", mapOf("data" to session, "project" to project, "users" to users, "tasks" to taskWithUsers)))
                } else if(session != null){
                    call.respond(FreeMarkerContent("project.ftl", mapOf("data" to session, "project" to project, "users" to users)))
                }
            }

            post{
                val id = call.parameters["id"]!!.toInt()
                val post = call.receiveParameters()
                val session = call.sessions.get<MySession>()

                DatabaseObject.addUsertoProject(id, post["email"].toString())

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

                DatabaseObject.addNewTask(id, post["name"].toString(), post["description"].toString())

                call.respondRedirect("/project/${id}", permanent = true)
            }
        }

    }
}