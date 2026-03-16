package com.inwave.backend.di

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.koin.dsl.module

class Env(
    private val dotenv: Dotenv
) {
    val databaseUrl: String get() {
        return dotenv["DB_URL"]
    }

    val databaseDriver: String get() {
        return dotenv["DB_DRIVER"]
    }

    val databaseUser: String get() {
        return dotenv["DB_USER"]
    }
    val databasePassword: String get() {
        return dotenv["DB_PASS"]
    }

    val isDatabaseInitRequired: Boolean get() {
        return runCatching {
            dotenv["DB_INIT"].toBoolean()
        }.fold(
            { it },
            { false }
        )
    }
}

val envModule = module {
    single<Env> {
        Env(get())
    }

    single<Dotenv> {
        dotenv {
            directory = "backend/"
            filename = ".env"
            systemProperties = true
        }
    }
}