package com.example.general

import com.example.models.MySession
import com.example.models.Users
import io.ktor.application.call
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.form
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@ExperimentalStdlibApi
fun Authentication.Configuration.loginform() {
    form("login") {
        skipWhen { call -> call.sessions.get<MySession>() != null }
        userParamName = "email"
        passwordParamName = "password"
        challenge { credentials ->
            if (credentials?.name != null) {
                call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
            } else {
                call.respond(FreeMarkerContent("login.ftl", null))
            }
        }
        validate { credentials ->
            try {
                val users = transaction {
                    Users.select { Users.email eq credentials.name }.map { Users.toAuth(it) }
                }

                val thishash =
                    SaltHash.generateHash(credentials.password, users.first().salt.hexStringToByteArray())

                if (thishash == users.first().password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            } catch (ex: NoSuchElementException) /* catches Exception when there is no entry for this email */ {
                null
            }
        }
    }
}