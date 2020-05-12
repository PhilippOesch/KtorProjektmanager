package com.example

import com.example.general.SaltHash
import com.example.general.hexStringToByteArray
import com.example.general.toHexString
import com.sun.org.apache.xerces.internal.impl.dv.xs.HexBinaryDV
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.sessions.*
import io.ktor.features.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.html.insert
import io.ktor.utils.io.core.toByteArray
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.charset.Charset

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
            userParamName = "email"
            passwordParamName = "password"
            challenge{
                call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
            }
            validate { credentials ->
                val users = transaction {
                    Users.select { Users.email eq credentials.name }.map { Users.toAuth(it) }
                }
                val thishash= SaltHash.generateHash(credentials.password, users.first().salt.hexStringToByteArray())
                println(thishash)
                println(users.first().password)
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

        get("/") {
            val session = call.sessions.get<MySession>()
            if (session != null) {
                //call.respondText("User is logged", null)
                call.respond(FreeMarkerContent("index.ftl", mapOf("username" to session.fullname)))
            } else {
                call.respond(FreeMarkerContent("index.ftl", null))
            }
        }

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
                            it[Users.salt]= salt.toHexString()
                            it[Users.password]= hashedPassword
                        }
                    }
                    call.respondText("OK")
                } else {
                    call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
                }
            }
        }

        route("/user"){
            get{
                val users = transaction {
                    Users.selectAll().map { Users.toUser(it)}
                }
                call.respond(users);
            }
        }

        get("/logout"){
            call.sessions.clear<MySession>()
            call.respondRedirect("/", permanent = false)
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

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
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

data class IndexData(val items: List<Int>)

data class MySession(val fullname: String, val email: String)

data class User(val email: String, val name: String)

data class AuthObject(val password: String, val salt: String)

object Users: Table(){
    val email: Column<String> = varchar("email", 50)
    val name: Column<String> = varchar("name", 100)
    val salt: Column<String> = varchar("salt", 20)
    val password: Column<String> = varchar("password", 100)

    override val primaryKey= PrimaryKey(email, name="PK_User_ID");

    fun toUser(row: ResultRow): User =
        User(
            name = row[Users.name],
            email = row[Users.email]
        )

    fun toAuth(row: ResultRow): AuthObject=
        AuthObject(
            password = row[Users.password],
            salt = row[Users.salt]
        )
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

