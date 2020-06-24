package com.example.database

import com.zaxxer.hikari.HikariConfig

val config = HikariConfig().apply {
    jdbcUrl         = "jdbc:postgresql://localhost:5432/projektmanager"
    driverClassName = "org.postgresql.Driver"
    username        = "postgres"
    password        = "123456"
    maximumPoolSize = 10
}