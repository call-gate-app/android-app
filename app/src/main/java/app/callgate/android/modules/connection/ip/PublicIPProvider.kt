package app.callgate.android.modules.connection.ip

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get

class PublicIPProvider : IPProvider {
    override suspend fun getIP(): String? {
        val client = HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT_MS
            }
        }

        return try {
            client.get(PUBLIC_IP_URL).body<String>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            client.close()
        }
    }

    companion object {
        private const val PUBLIC_IP_URL = "https://api.ipify.org"
        private const val REQUEST_TIMEOUT_MS = 5000L
    }
}