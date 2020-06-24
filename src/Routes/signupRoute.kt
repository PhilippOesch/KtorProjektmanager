package com.example.Routes

import com.example.general.SaltHash
import com.example.general.toHexString
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
fun Routing.signup(){
    route("/signup"){
        get {
            call.respond(FreeMarkerContent("signup.ftl", null))
        }
        post{
            val post = call.receiveParameters()

            val users= transaction {
                Users.select{ Users.email eq post["email"].toString()}.map { Users.toUser(it) }
            }

            if(users.isNotEmpty()){
                call.respond("User already exists")
            } else if(post["password"]!= null && post["password"]== post["confirmPassword"]){
                var salt: ByteArray= SaltHash.createSalt();
                var hashedPassword: String= SaltHash.generateHash(post["password"].toString(), salt)

                transaction {
                    Users.insert {
                        it[email] = post["email"].toString()
                        it[name] = post["name"].toString()
                        it[biography] = ""
                        it[com.example.models.Users.salt] = salt.toHexString()
                        it[password] = hashedPassword
                    }
                }
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