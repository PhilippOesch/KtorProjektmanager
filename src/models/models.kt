package com.example.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

data class IndexData(val items: List<Int>)

data class MySession(val name: String, val email: String)

data class User(val email: String, val name: String, val biography: String)

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