package app.callgate.android.modules.connection.ip

interface IPProvider {
    suspend fun getIP(): String?
}