import com.example.database.DatabaseObject
import com.example.models.MySession
import com.example.models.Users
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.user(){

    authenticate("login") {
        route("/user") {
            get {
                val users = transaction {
                    Users.selectAll().map { Users.toUser(it) }
                }
                call.respond(users);
            }

            route("/{id}") {
                get {
                    val session = call.sessions.get<MySession>()
                    val id = call.parameters["id"]!!.toString()
                    if (session != null && session.email == id) {
                        val users = transaction {
                            Users.select { Users.email eq id }
                                .map { Users.toUser(it) }
                        }
                        call.respond(
                            FreeMarkerContent(
                                "user.ftl",
                                mapOf("data" to users.first())
                            )
                        )
                    } else {
                        call.respond("insufficient authorization")
                    }
                }

                post {
                    val session = call.sessions.get<MySession>()
                    val id = call.parameters["id"]!!.toString()
                    val post = call.receiveParameters()
                    if (session != null) {
                        DatabaseObject.updateUser(id, post["fullname"].toString(), post["biography"].toString())
                        call.respondRedirect("/user/${id}", permanent = true)
                    }
                }
            }
        }
    }
}