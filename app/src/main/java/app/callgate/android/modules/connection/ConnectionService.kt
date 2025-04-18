package app.callgate.android.modules.connection

import android.content.Context
import app.callgate.android.modules.connection.ip.LocalIPProvider
import app.callgate.android.modules.connection.ip.PublicIPProvider

class ConnectionService(
    context: Context
) {
    private val localIPProvider = LocalIPProvider(context)
    private val publicIPProvider = PublicIPProvider()

    suspend fun getLocalIP(): String? = localIPProvider.getIP()
    suspend fun getPublicIP(): String? = publicIPProvider.getIP()
}