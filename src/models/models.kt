package com.example.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.time.LocalDate
import java.util.*


data class MySession(val name: String, val email: String)

data class User(val email: String, val name: String, val biography: String)

data class Project(val id: Int, val name: String, val description: String, val deadline: LocalDate)

data class AuthObject(val password: String, val salt: String)

object Users: Table(){
    val email: Column<String> = varchar("email", 50)
    val name: Column<String> = varchar("name", 100)
    val biography: Column<String> = varchar("biography", 500)
    val salt: Column<String> = varchar("salt", 20)
    val password: Column<String> = varchar("password", 100)

    override val primaryKey= PrimaryKey(email, name="PK_User_ID");

    fun toUser(row: ResultRow): User =
        User(
            name = row[name],
            email = row[email],
            biography = row[biography]
        )

    fun toAuth(row: ResultRow): AuthObject =
        AuthObject(
            password = row[password],
            salt = row[salt]
        )
}

object Projects: Table(){
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val description: Column<String> = varchar("description", 500)
    val deadline: Column<String?> = varchar("deadline", 500).nullable()

    override val primaryKey= PrimaryKey(Projects.id, name="PK_Project_ID");

    fun toProject(row: ResultRow): Project =
        Project(
            id = row[id],
            name = row[name],
            description = row[description],
            deadline = LocalDate.parse(row[deadline])
        )
}
