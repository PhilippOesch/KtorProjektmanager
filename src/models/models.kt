package com.example.models

import com.example.enums.TaskStatus
import com.example.models.Projects.autoIncrement
import com.example.models.Tasks.autoIncrement
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.time.LocalDate


data class MySession(val name: String, val email: String)

data class User(val email: String, val name: String, val biography: String)

data class Project(val id: Int, val name: String, val description: String, val deadline: LocalDate?)

data class ProjectUser(val projectId: Int, val userId: String)

data class Task(val id: Int, val name: String, val description: String, val pId: Int, val status: TaskStatus)

data class TaskUser(val id: Int, val taskId: Int, val userId: String)

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
            deadline = if(row[deadline]!= null) LocalDate.parse(row[deadline]) else null
        )
}

object ProjectUsers: IntIdTable(){
    val projectId: Column<Int> = integer("pId").references(Projects.id)
    val userId: Column<String> = varchar("userId", 50).references(Users.email)

    fun toProjectUser(row: ResultRow): ProjectUser =
        ProjectUser(
            projectId = row[projectId],
            userId = row[userId]
        )
}

object Tasks: Table(){
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val status: Column<String> = varchar("status", 50)
    val description: Column<String> = varchar("beschreibung", 200)
    val pid: Column<Int> = integer("pId").references(Projects.id)

    override val primaryKey= PrimaryKey(Tasks.id, name="PK_Task_ID");

    fun toTask(row: ResultRow): Task {
        var thestatus= when(row[status]){
            "OPEN" -> TaskStatus.OPEN
            "INWORK" -> TaskStatus.INWORK
            "COMPLETED" -> TaskStatus.COMPLETED
            else -> TaskStatus.OPEN
        }

        return Task(
            id = row[id],
            name = row[name],
            description = row[description],
            pId = row[pid],
            status = thestatus
        )
    }
}

object TasksUser: Table(){
    val id: Column<Int> = integer("id").autoIncrement()
    val taskId: Column<Int> = integer("taskid").references(Tasks.id)
    val userId: Column<String> = varchar("userid", 50).references(Users.email)

    fun toTaskUser(row: ResultRow): TaskUser =
        TaskUser(
            id = row[id],
            taskId = row[taskId],
            userId = row[userId]
        )

    override val primaryKey= PrimaryKey(Tasks.id, name="PK_TaskUser_ID");
}
