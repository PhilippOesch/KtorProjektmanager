package com.example


import com.example.general.SaltHash
import com.example.general.hexStringToByteArray
import com.example.general.toHexString
import com.example.models.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
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
import io.ktor.gson.*
import com.example.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@ExperimentalStdlibApi
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Sessions) {
        cookie<MySession>("SESSION")
    }

    install(Authentication) {
        form("login") {
            skipWhen { call -> call.sessions.get<MySession>() != null }
            userParamName = "email"
            passwordParamName = "password"
            challenge{
                if(call.sessions.get<MySession>() == null){
                    call.respond(FreeMarkerContent("login.ftl", null))
                } else {
                    call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
                }
            }
            validate { credentials ->
                val users = transaction {
                    Users.select { Users.email eq credentials.name }.map { Users.toAuth(it) }
                }
                val thishash= SaltHash.generateHash(credentials.password, users.first().salt.hexStringToByteArray())
/*                println(thishash)
                println(users.first().password)*/
                if (thishash == users.first().password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    Database.connect("jdbc:postgresql://localhost:5432/projektmanager", driver = "org.postgresql.Driver",
        user = "postgres", password = "123456")
    transaction {
        SchemaUtils.create(Users)
    }

    routing {

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        route("/signup"){
            get {
                call.respond(FreeMarkerContent("signup.ftl", null))
            }
            post{
                val post = call.receiveParameters()
                if(post["password"]!= null && post["password"]== post["confirmPassword"]){
                    var salt: ByteArray= SaltHash.createSalt();
                    var hashedPassword: String= SaltHash.generateHash(post["password"].toString(), salt)

                    transaction {
                        Users.insert {
                            it[Users.email] = post["email"].toString()
                            it[Users.name]= post["name"].toString()
                            it[Users.biography]= ""
                            it[Users.salt]= salt.toHexString()
                            it[Users.password]= hashedPassword
                        }
                    }
                    call.respondRedirect("/login", permanent = false)
                } else {
                    call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
                }
            }
        }

        route("/login") {
            get {
                call.respond(FreeMarkerContent("login.ftl", null))
            }
            authenticate("login") {
                post {
                    val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                    val users = transaction {
                        Users.select { Users.email eq principal.name}.map { Users.toUser(it) }
                    }
                    call.sessions.set("SESSION", MySession(users.first().name, users.first().email))
                    call.respondRedirect("/", permanent = false)
                }
            }

        }

        authenticate("login") {
            route("/user"){
                get{
                    val users = transaction {
                        Users.selectAll().map { Users.toUser(it)}
                    }
                    call.respond(users);
                }

                route("/{id}"){
                    get{
                        val session = call.sessions.get<MySession>()
                        val id = call.parameters["id"]!!.toString()
                        if(session!= null && session.email== id) {
                            val users = transaction {
                                Users.select { Users.email eq id }.map { Users.toUser(it) }
                            }
                            call.respond(FreeMarkerContent("user.ftl", mapOf("data" to users.first())))
                        } else {
                            call.respond("insufficient authorization")
                        }
                    }

                    post{
                        val session = call.sessions.get<MySession>()
                        val id = call.parameters["id"]!!.toString()
                        val post = call.receiveParameters()
                        if(session!= null){
                            transaction {
                                Users.update({ Users.email eq id }) {
                                    it[Users.name] = post["fullname"].toString()
                                    it[Users.biography] = post["biography"].toString()
                                }
                            }
                            call.respondRedirect("/user/${id}", permanent = true)
                        }
                    }
                }
            }

            get("/") {
                val session = call.sessions.get<MySession>()

                if (session != null) {
                    println(session.name);
                    //call.respondText("models.kt.User is logged", null)
                    call.respond(FreeMarkerContent("index.ftl", mapOf("data" to session)))
                } else {
                    call.respond(FreeMarkerContent("index.ftl", null))
                }
            }

            get("/logout"){
                call.sessions.clear<MySession>()
                call.respondRedirect("/", permanent = false)
            }

        }

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

