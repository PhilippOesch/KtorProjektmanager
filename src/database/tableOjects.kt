package com.example.database

import com.example.enums.TaskStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.sql.Timestamp
import java.time.LocalDate
import com.example.models.*

object Users : Table() {
    val email: Column<String> = varchar("email", 50)
    val name: Column<String> = varchar("name", 100)
    val biography: Column<String> = varchar("biography", 500)
    val salt: Column<String> = varchar("salt", 20)
    val password: Column<String> = varchar("password", 100)

    override val primaryKey = PrimaryKey(email, name = "PK_User_ID");

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

object Projects : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val description: Column<String> = varchar("description", 500)
    val deadline: Column<String?> = varchar("deadline", 500).nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_Project_ID");


    fun toProject(row: ResultRow): Project =
        Project(
            id = row[id],
            name = row[name],
            description = row[description],
            deadline = if (row[deadline] != null) LocalDate.parse(row[deadline]) else null
        )
}

object ProjectUsers : IntIdTable() {
    val projectId: Column<Int> = integer("pId").references(Projects.id)
    val userId: Column<String> = varchar("userId", 50).references(Users.email)

    fun toProjectUser(row: ResultRow): ProjectUser =
        ProjectUser(
            projectId = row[projectId],
            userId = row[userId]
        )
}

object Tasks : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 50)
    val status: Column<String> = varchar("status", 50)
    val description: Column<String> = varchar("beschreibung", 200)
    val pid: Column<Int> = integer("pId").references(Projects.id)

    override val primaryKey = PrimaryKey(id, name = "PK_Task_ID");

    fun toTask(row: ResultRow): Task {
        var thestatus = when (row[status]) {
            "OPEN" -> TaskStatus.OPEN
            "INWORK" -> TaskStatus.INWORK
            "COMPLETED" -> TaskStatus.COMPLETED
            else -> TaskStatus.OPEN
        }

        return Task(
            id = row[id],
            name = row[name],
            description = row[description],
            pid = row[pid],
            status = thestatus
        )
    }
}

object TasksUsers : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val taskId: Column<Int> = integer("taskid").references(Tasks.id)
    val userId: Column<String> = varchar("userid", 50).references(Users.email)

    override val primaryKey = PrimaryKey(id, name = "PK_TaskUser_ID");

    fun toTaskUser(row: ResultRow): TaskUser =
        TaskUser(
            id = row[id],
            taskId = row[taskId],
            userId = row[userId]
        )
}

object Files : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val filename: Column<String> = varchar("filename", 300)
    val originalfilename: Column<String> = varchar("originalFilename", 300)
    val projectId: Column<Int> = integer("projectId").references(Projects.id)
    val userId: Column<String> = varchar("userId", 50).references(Users.email)
    val lastUpdate: Column<Long> = long("updateDate")

    override val primaryKey = PrimaryKey(id, name = "PK_File_ID");

    fun toFile(row: ResultRow): ProjectFile =
        ProjectFile(
            id = row[id],
            filename = row[filename],
            originalFilename = row[originalfilename],
            pid = row[projectId],
            userId = row[userId],
            lastUpdate = Timestamp(row[lastUpdate])
        )
}

object Messages : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val text: Column<String> = varchar("text", 500)
    val pid: Column<Int> = integer("pid").references(Projects.id)
    val userId: Column<String> = varchar("userId", 50).references(Users.email)
    val timestamp: Column<Long> = long("timestamp")

    override val primaryKey = PrimaryKey(id, name = "PK_Messages_ID");

    fun toMessage(row: ResultRow): Message =
        Message(
            id = row[id],
            text = row[text],
            pid = row[pid],
            userId = row[userId],
            timestamp = Timestamp(row[timestamp])
        )
}