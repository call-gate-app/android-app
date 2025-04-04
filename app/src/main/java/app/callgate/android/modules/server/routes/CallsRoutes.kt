package app.callgate.android.modules.server.routes

import app.callgate.android.modules.calls.CallsService
import app.callgate.android.modules.calls.domain.CallState
import app.callgate.android.modules.server.domain.PostCallsRequest
import app.callgate.android.modules.server.domain.PostCallsResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CallsRoutes : KoinComponent {
    private val callsService: CallsService by inject()

    fun register(routing: Route) {
        routing.registerRoutes()
    }

    private fun Route.registerRoutes() {
//        get("") {
//            val currentCall = callsService.getCall()
//
//            call.respond(currentCall)
//        }

        post("") {
            val request = call.receive<PostCallsRequest>()

            val phoneNumber = request.call.phoneNumber
            callsService.startCall(phoneNumber)
            call.respond(HttpStatusCode.OK, PostCallsResponse(request.call))
        }

        delete("") {
            val currentCall = callsService.getCall()
            if (currentCall.state == CallState.Idle) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("message" to "No ringing or active call")
                )
                return@delete
            }

            if (!callsService.endCall()) {
                throw RuntimeException("Failed to end call")
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
