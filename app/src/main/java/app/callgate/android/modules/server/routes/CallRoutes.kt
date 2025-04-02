package app.callgate.android.modules.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import app.callgate.android.modules.calls.CallsService
import io.ktor.server.routing.delete
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CallRoutes : KoinComponent {
    private val callsService: CallsService by inject()

    fun register(routing: Route) {
        routing.registerRoutes()
    }

    private fun Route.registerRoutes() {
        post("") {
            val phoneNumber = call.receive<String>()
            callsService.startCall(phoneNumber)
            call.respond(HttpStatusCode.OK, "Calling $phoneNumber...")
        }

        delete("") {
            callsService.endCall()
            call.respond(HttpStatusCode.OK, "Ending all calls...")
        }
    }
}
