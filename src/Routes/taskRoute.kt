package com.example.Routes

import com.example.database.DatabaseObject
import com.example.models.MySession
import com.example.models.Tasks
import com.example.models.Users
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Routing.task() {
    authenticate("login") {
        route("/task/{id}"){
            post {
                val session = call.sessions.get<MySession>()
                val id = call.parameters["id"]!!.toInt()

                val post = call.receiveParameters()
                val projectUsers= DatabaseObject.getProjectUsers(post["pId"]!!.toInt())
                val taskUsers= DatabaseObject.getTasksUsers(id)

                for (user in projectUsers){
                    var hasUser= taskUsers.find { it.email == user.email }
                    if(hasUser == null && post["${user.email}"].toString()== "on"){
                        DatabaseObject.addTaskUser(id, user.email)
                    } else if(hasUser!= null && post["${user.email}"].toString()== "null"){
                        DatabaseObject.deleteTaskUser(id, user.email)
                    }
                }

                if (session != null) {
                    DatabaseObject.updateTaskDescription(id, post["description"].toString())
                    call.respondRedirect("/project/${post["pId"].toString()}", permanent = true)
                }

            }
        }

        route("/task/{id}/{status}"){
            post{
                val session = call.sessions.get<MySession>()
                val id = call.parameters["id"]!!.toInt()
                val newstatus = call.parameters["status"]!!.toString()

                val post = call.receiveParameters()

                if (session != null) {
                    DatabaseObject.updateTaskStatus(id, newstatus)
                }

                call.respondRedirect("/project/${post["pId"].toString()}", permanent = true)
            }
        }
    }
}