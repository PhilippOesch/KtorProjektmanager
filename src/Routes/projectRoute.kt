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
                val files = DatabaseObject.getProjectFiles(id)

                val taskWithUsers = mutableListOf<TaskWithUsers>()
                if(tasks.isNotEmpty()){
                    for(task in tasks){
                        val taskID= task.id
                        val users= DatabaseObject.getTasksUsers(taskID)

                        taskWithUsers.add(TaskWithUsers(task, users))
                    }
                }

                if (session != null && tasks.isNotEmpty()) {
                    call.respond(FreeMarkerContent("project.ftl", mapOf("data" to session, "project" to project, "users" to users, "tasks" to taskWithUsers, "files" to files)))
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

        route("/project/{id}/upload"){
            post{
                val session = call.sessions.get<MySession>()
                val pid = call.parameters["id"]!!.toInt()

                if(session!= null) {
                    var filename= ""
                    val timestamp= System.currentTimeMillis()
                    var originalFileName= ""

                    val multipart = call.receiveMultipart()
                    multipart.forEachPart { part ->
                        // if part is a file (could be form item)
                        if (part is PartData.FileItem) {
                            // retrieve file name of upload
                            originalFileName= part.originalFileName!!
                            filename= "${timestamp}_${pid}_${originalFileName}";
                            val file = File("/uploads/${filename}")

                            // use InputStream from part to save file
                            part.streamProvider().use { its ->
                                // copy the stream to the file with buffering
                                file.outputStream().buffered().use {
                                    // note that this is blocking
                                    its.copyTo(it)
                                }
                            }
                        }
                        // make sure to dispose of the part after use to prevent leaks
                        part.dispose()
                    }
                    DatabaseObject.addFile(pid, session.email, filename, originalFileName, timestamp)

                    call.respondRedirect("/project/${pid}", permanent = true)
                }
            }
        }

        get("/project/{id}/{filename}"){
            val filename = call.parameters["filename"]!!
            val pid = call.parameters["id"]!!

            val file = File("/uploads/$filename")
            if(file.exists()) {
                call.respondFile(file)
            }
            else call.respond(HttpStatusCode.NotFound)

        }
    }
}