package com.example.database

import com.example.enums.TaskStatus
import com.example.general.SaltHash
import com.example.general.toHexString
import com.example.models.*
import com.zaxxer.hikari.HikariDataSource
import kotlinx.html.InputType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object DatabaseObject {
    fun init() {
        val dataSource = HikariDataSource(config)
        Database.connect(
            dataSource
        )
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Projects)
            SchemaUtils.create(ProjectUsers)
            SchemaUtils.create(Tasks)
            SchemaUtils.create(TasksUsers)
            SchemaUtils.create(Files)
            SchemaUtils.create(Messages)
        }
    }

    fun createProject(pname: String, pdescription: String, useremail: String) {
        transaction {
            val pId = Projects.insert {
                it[name] = pname
                it[description] = pdescription
            } get Projects.id

            ProjectUsers.insert {
                it[projectId] = pId
                it[userId] = useremail
            }
        }
    }

    fun getProject(pid: Int): Project {
        val projects = transaction {
            Projects.select { Projects.id eq pid }.map { Projects.toProject(it) }
        }

        return projects.first()
    }

    fun getProjectUsers(pid: Int): List<User> {
        return transaction {
            ProjectUsers.join(Users, JoinType.INNER, additionalConstraint = { ProjectUsers.projectId eq pid })
                .selectAll().map { Users.toUser(it) }
        }
    }

    fun getProjectTasks(pid: Int): List<Task> {
        return transaction {
            Tasks.select { Tasks.pid eq pid }.map { Tasks.toTask(it) }
        }
    }

    fun getUsersProjects(email: String): List<Project> {
        return transaction {
            ProjectUsers.join(
                Projects,
                JoinType.INNER,
                additionalConstraint = { ProjectUsers.userId eq email }).selectAll().map { Projects.toProject(it) }

        }
    }

    fun addUsertoProject(pid: Int, email: String) {
        transaction {
            val users = Users.select { Users.email eq email }.map { Users.toUser(it) }

            val projectUser =
                ProjectUsers.select { ProjectUsers.projectId eq pid and (ProjectUsers.userId eq email) }.map {
                    ProjectUsers.toProjectUser(it)
                }

            if (users.isNotEmpty() && projectUser.isEmpty()) {
                ProjectUsers.insert {
                    it[projectId] = pid
                    it[userId] = email
                }
            }
        }
    }

    fun addNewTask(pid: Int, name: String, description: String) {
        transaction {
            Tasks.insert {
                it[Tasks.name] = name
                it[Tasks.description] = description
                it[status] = TaskStatus.OPEN.statustype
                it[Tasks.pid] = pid
            }
        }
    }

    fun getUser(email: String): User? {
        val users = transaction {
            Users.select { Users.email eq email }.map { Users.toUser(it) }
        }

        if (users.isEmpty()) {
            return null
        }
        return users.first()
    }

    @ExperimentalStdlibApi
    fun createUser(email: String, name: String, password: String) {
        var salt: ByteArray = SaltHash.createSalt();
        var hashedPassword: String = SaltHash.generateHash(password, salt)

        transaction {
            Users.insert {
                it[Users.email] = email
                it[Users.name] = name
                it[Users.biography] = ""
                it[Users.salt] = salt.toHexString()
                it[Users.password] = hashedPassword
            }
        }
    }

    fun updateUser(userID: String, userFullname: String, userBiography: String) {
        transaction {
            Users.update({ Users.email eq userID }) {
                it[name] = userFullname
                it[biography] = userBiography
            }
        }
    }

    fun updateTaskDescription(taskID: Int, taskDescription: String) {
        transaction {
            Tasks.update({ Tasks.id eq taskID }) {
                it[Tasks.description] = taskDescription
            }
        }
    }

    fun updateTaskStatus(taskID: Int, taskStatus: String) {
        transaction {
            Tasks.update({ Tasks.id eq taskID }) {
                it[status] = taskStatus
            }
        }
    }

    fun getTasksUsers(taskID: Int): List<User> {
        return transaction {
            TasksUsers.join(
                Users,
                JoinType.INNER,
                additionalConstraint = { TasksUsers.taskId eq taskID }).selectAll().map { Users.toUser(it) }
        }
    }

    fun addTaskUser(taskID: Int, userId: String) {
        transaction {
            TasksUsers.insert {
                it[taskId] = taskID
                it[TasksUsers.userId] = userId
            }
        }
    }

    fun deleteTaskUser(taskID: Int, userId: String){
        transaction {
            TasksUsers.deleteWhere{ TasksUsers.taskId eq taskID and (TasksUsers.userId eq userId)}
        }
    }

    fun addFile(pID: Int, userId: String, filename: String, originalFileName: String, timestamp: Long){
        transaction {
            Files.insert {
                it[Files.filename]= filename
                it[originalfilename]= originalFileName
                it[projectId]= pID
                it[Files.userId]= userId
                it[lastUpdate]= timestamp
            }
        }
    }

    fun getProjectFiles(pid: Int): List<ProjectFile> {
        return transaction {
            Files.select { Files.projectId eq pid }.map{ Files.toFile(it) }
        }
    }

    fun getUserTasks(userId: String): List<Task> {
        return transaction {
            TasksUsers.join(
                Tasks,
                JoinType.INNER,
                additionalConstraint = { TasksUsers.userId eq userId }).selectAll().map { Tasks.toTask(it) }
        }
    }

    fun getFile(filename: String): ProjectFile {
        val files= transaction {
            Files.select { Files.filename eq filename }.map { Files.toFile(it) }
        }

        return files.first()
    }

    fun getProjectMessages(projectId: Int): List<MessageWithUser>{
        return transaction {
            Messages.join(
                Users,
                JoinType.INNER,
                additionalConstraint = { Messages.pid eq projectId }).selectAll().orderBy(Messages.timestamp to SortOrder.DESC).map {
                    val user= Users.toUser(it)
                    val message= Messages.toMessage(it)

                    MessageWithUser(message, user)
                }
        }
    }

    fun createMessage(projectId: Int, userId: String, text: String, timestamp: Long){
        transaction {
            Messages.insert {
                it[pid]= projectId
                it[Messages.userId]= userId
                it[Messages.text]= text
                it[Messages.timestamp]= timestamp
            }
        }
    }
}