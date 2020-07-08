package com.example.Routes

import com.example.database.DatabaseObject
import com.example.models.MySession
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
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
import java.io.File

fun Routing.projectFiles(){
    authenticate("login") {
        route("/project/{id}/files"){
            get {
                val session = call.sessions.get<MySession>()
                val id = call.parameters["id"]!!.toInt()
                val project= DatabaseObject.getProject(id)
                val files = DatabaseObject.getProjectFiles(id)

                if (session != null){
                    call.respond(FreeMarkerContent("projectFiles.ftl", mapOf("project" to project,"data" to session,"files" to files)))
                }
            }
        }

        route("/project/{id}/files/upload"){
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

                    call.respondRedirect("/project/${pid}/files", permanent = true)
                }
            }
        }

        get("/project/{id}/files/{filename}"){

            val filename = call.parameters["filename"]!!
            val pid = call.parameters["id"]!!
            val originalname= DatabaseObject.getFile(filename).originalFilename

            val file = File("/uploads/$filename")
            if(file.exists()) {
                call.response.header("Content-Disposition", "attachment; filename=\"${originalname}\"")
                call.respondFile(file)
            }
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
