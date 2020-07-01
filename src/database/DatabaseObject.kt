package com.example.database

import com.example.enums.TaskStatus
import com.example.general.SaltHash
import com.example.general.toHexString
import com.example.models.*
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

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

}