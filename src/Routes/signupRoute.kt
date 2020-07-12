package com.example.Routes

import com.example.database.DatabaseObject
import com.example.general.SaltHash
import com.example.general.toHexString
import com.example.models.User
import com.example.models.Users
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@ExperimentalStdlibApi
fun Routing.signup() {
    route("/signup") {
        get {
            call.respond(FreeMarkerContent("signup.ftl", null))
        }
        post {
            val post = call.receiveParameters()
            val user: User? = DatabaseObject.getUser(post["email"].toString());
            val password = post["password"].toString();

            if (user != null) {
                call.respond(
                    FreeMarkerContent(
                        "signup.ftl",
                        mapOf("error" to "Email ist bereits vorhanden")
                    )
                )
            } else if (password != "" && post["password"] == post["confirmPassword"]) {
                DatabaseObject.createUser(
                    post["email"].toString(),
                    post["name"].toString(),
                    post["password"].toString()
                )

                call.respondRedirect("/login", permanent = true)
            } else {
                call.respond(
                    FreeMarkerContent(
                        "signup.ftl",
                        mapOf("error" to "Invalid login")
                    )
                )
            }
        }
    }
}