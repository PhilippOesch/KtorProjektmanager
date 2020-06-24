package com.example


import com.example.Routes.*
import com.example.database.config
import com.example.general.SaltHash
import com.example.general.hexStringToByteArray
import com.example.models.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import kotlinx.html.*
import kotlinx.css.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.sessions.*
import io.ktor.features.*
import io.ktor.auth.*
import com.example.models.Users
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import user

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@ExperimentalStdlibApi
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    //initialise Ktor Features

    //Templating Engine
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    //Session management
    install(Sessions) {
        cookie<MySession>("SESSION")
    }

    //Authentication Feature
    install(Authentication) {
        form("login") {
            skipWhen { call -> call.sessions.get<MySession>() != null }
            userParamName = "email"
            passwordParamName = "password"
            challenge {

                call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
            }
            validate { credentials ->
                try {
                    val users = transaction {
                        Users.select { Users.email eq credentials.name }.map { Users.toAuth(it) }
                    }

                    val thishash =
                        SaltHash.generateHash(credentials.password, users.first().salt.hexStringToByteArray())
/*                println(thishash)
                println(users.first().password)*/
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

/*
    install(ContentNegotiation) {
        gson {
        }
    }
*/

    val dataSource = HikariDataSource(config)
    Database.connect(
        dataSource
    )
    transaction {
        SchemaUtils.create(Users)
        SchemaUtils.create(Projects)
        SchemaUtils.create(ProjectUsers)
        SchemaUtils.create(Tasks)
        SchemaUtils.create(TasksUser)
    }

    routing {

        // defining a folder with static files
        static("/static") {
            resources("static")
        }

        //Routes defined inside the "Routes" Package
        this.signup()
        this.login()
        this.user()
        this.root()
        this.logout()
        this.createProjekt()
        this.project()
        this.task()

        //Exceptionhandling
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        }

    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

