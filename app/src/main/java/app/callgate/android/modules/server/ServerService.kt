package app.callgate.android.modules.server

import android.content.Context
import android.os.Build

class ServerService(
    val settings: ServerSettings,
) {
    fun start(context: Context) {
        settings.deviceId = settings.deviceId ?: getDeviceId(context)
        
        WebService.start(context)
    }

    fun stop(context: Context) {
        WebService.stop(context)
    }

    fun isActiveLiveData() = WebService.STATUS

    private fun getDeviceId(context: Context): String {
        val firstInstallTime = context.packageManager.getPackageInfo(
            context.packageName,
            0
        ).firstInstallTime
        val deviceName = "${Build.MANUFACTURER}/${Build.PRODUCT}"

        return deviceName.hashCode().toULong()
            .toString(16).padStart(16, '0') + firstInstallTime.toULong()
            .toString(16).padStart(16, '0')
    }
}