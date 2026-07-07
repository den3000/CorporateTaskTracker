package ru.den.writes.code

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import ru.den.writes.code.domain.model.Task

val tasks = mutableListOf<Task>()

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        
        get("/api/ping") {
            call.respondText("pong", status = HttpStatusCode.OK)
        }

        route("/api/tasks") {
            get {
                call.respond(tasks)
            }
            post {
                val received = call.receive<Task>()
                // Клиент присылает id=0 для новых задач — назначаем серверный id.
                // Если id уже задан (например, из локальной БД), обновляем/добавляем как есть.
                val task = if (received.id == 0) {
                    val nextId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                    received.copy(id = nextId)
                } else {
                    tasks.removeIf { it.id == received.id }
                    received
                }
                tasks.add(task)
                call.respond(HttpStatusCode.Created, task)
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val removed = tasks.removeIf { it.id == id }
                if (removed) {
                    call.respond(HttpStatusCode.OK, "Task deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }
        }
    }
}
