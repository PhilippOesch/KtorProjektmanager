package com.example.Routes

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
                if (session != null) {
                    /*       transaction {
                               Users.update({ Users.email eq id }) {
                                   it[name] = post["fullname"].toString()
                                   it[biography] = post["biography"].toString()
                               }
                           }*/
                    transaction {
                        Tasks.update({ Tasks.id eq id }) {
                            it[description] = post["description"].toString()
                        }
                    }
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
                    transaction {
                        Tasks.update({ Tasks.id eq id}){
                            it[status] = newstatus
                        }
                    }
                }

                call.respondRedirect("/project/${post["pId"].toString()}", permanent = true)
            }
        }
    }
}