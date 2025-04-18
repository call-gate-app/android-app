package app.callgate.android.modules.connection.ip

import android.content.Context
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface


class LocalIPProvider(private val context: Context) : IPProvider {
    override suspend fun getIP(): String? {
        try {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            // If the device is in tethering mode, the following method may return null or a default IP
            wifiManager.connectionInfo?.let { connectionInfo ->
                val ipInt = connectionInfo.ipAddress
                if (ipInt != 0) {
                    return java.net.InetAddress.getByAddress(
                        byteArrayOf(
                            (ipInt and 0xFF).toByte(),
                            ((ipInt shr 8) and 0xFF).toByte(),
                            ((ipInt shr 16) and 0xFF).toByte(),
                            ((ipInt shr 24) and 0xFF).toByte()
                        )
                    ).hostAddress
                }
            }

            // If the above doesn't work, try to find the WiFi network interface directly
            val wifiInterface = NetworkInterface.getNetworkInterfaces().asSequence()
                .find { it.name.contains("wlan", ignoreCase = true) }

            wifiInterface?.inetAddresses?.asSequence()
                ?.find { !it.isLoopbackAddress && it is Inet4Address }
                ?.let { inetAddress ->
                    return inetAddress.hostAddress
                }

            // Check any other network interface
            NetworkInterface.getNetworkInterfaces().asSequence()
                .flatMap { intf -> intf.inetAddresses.asSequence() }
                .find { !it.isLoopbackAddress && it is Inet4Address }
                ?.let { return it.hostAddress }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}