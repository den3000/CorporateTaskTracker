package ru.den.writes.code

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
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
//    install(ContentNegotiation) {
//        json(Json {
//            prettyPrint = true
//            isLenient = true
//            ignoreUnknownKeys = true
//        })
//    }

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        
        get("/api/ping") {
            call.respondText("pong", status = HttpStatusCode.OK)
        }

        route("/api/tasks") {
            get {
                // call.respond(tasks)
                // while no content negotiation added
                call.respondText(tasks.toString(), status = HttpStatusCode.OK)
            }
            post {
                // val task = call.receive<Task>()
                // tasks.add(task)
                // call.respond(HttpStatusCode.Created, task)

                // while no content negotiation added
                val body = call.receiveText()
                val task = Json.decodeFromString<Task>(body)
                tasks.add(task)
                call.respondText("Task added: ${task.title}", status = HttpStatusCode.Created)
            }
            delete("/{id}") {
                val id = call.parameters["id"]
                val removed = tasks.removeIf { it.id.toString() == id }
                if (removed) {
                    call.respond(HttpStatusCode.OK, "Task deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }
        }
    }
}
