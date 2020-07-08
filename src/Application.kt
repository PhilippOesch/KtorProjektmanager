package com.example


import com.example.Routes.*
import com.example.database.DatabaseObject
import com.example.database.config
import com.example.general.SaltHash
import com.example.general.hexStringToByteArray
import com.example.general.loginform
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
import java.io.File
import java.io.IOException

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
        this.loginform()
    }

    //Upload Ordner
    val uploadDir = File("/uploads")
    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }

    DatabaseObject.init() //init Database
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
        this.projectFiles()
        this.projectSettings()
        this.projectMessages()
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

