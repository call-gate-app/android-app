package app.callgate.android.modules.webhooks.plugins

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val PayloadSigningPlugin =
    createClientPlugin("PayloadSigningPlugin", ::PayloadSigningPluginConfig) {
        val algorithm = pluginConfig.hmacAlgorithm
        val headerName = pluginConfig.signatureHeaderName
        val timestampHeaderName = pluginConfig.timestampHeaderName
        val secretKeyProvider = pluginConfig.secretKeyProvider

        transformRequestBody { request, content, _ ->
            if (content !is OutgoingContent) {
                return@transformRequestBody null
            }

            if (content is TextContent) {
                val secretKey = secretKeyProvider() ?: return@transformRequestBody content

                val timestamp = (System.currentTimeMillis() / 1000).toString()
                val message = content.text + timestamp

                request.header(timestampHeaderName, timestamp)
                request.header(
                    headerName, generateSignature(
                        algorithm,
                        secretKey,
                        message
                    )
                )
            }

            content
        }
    }

class PayloadSigningPluginConfig {
    var secretKeyProvider: () -> String? = { null }
    var signatureHeaderName: String = "X-Signature"
    var timestampHeaderName: String = "X-Timestamp"
    var hmacAlgorithm: String = "HmacSHA256"
}

@OptIn(ExperimentalStdlibApi::class)
fun generateSignature(algorithm: String, secretKey: String, payload: String): String {
    val mac = Mac.getInstance(algorithm)
    val secretKeySpec = SecretKeySpec(secretKey.toByteArray(charset = Charsets.UTF_8), algorithm)
    mac.init(secretKeySpec)
    val hash = mac.doFinal(payload.toByteArray(charset = Charsets.UTF_8))
    return hash.toHexString(HexFormat.Default)
}
