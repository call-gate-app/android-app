package app.callgate.android.modules.server.routes

import app.callgate.android.domain.EntitySource
import app.callgate.android.modules.webhooks.WebHooksService
import app.callgate.android.modules.webhooks.domain.WebHookDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

class WebhooksRoutes(
    private val webHooksService: WebHooksService,
) {
    fun register(routing: Route) {
        routing.apply {
            webhooksRoutes()
        }
    }

    private fun Route.webhooksRoutes() {
        get {
            call.respond(webHooksService.select(EntitySource.Local))
        }
        post {
            val webhook = call.receive<WebHookDTO>()
            val updated = webHooksService.replace(EntitySource.Local, webhook)

            call.respond(HttpStatusCode.Created, updated)
        }
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            webHooksService.delete(EntitySource.Local, id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}