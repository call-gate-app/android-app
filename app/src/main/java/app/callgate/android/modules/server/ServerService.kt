package app.callgate.android.modules.server

import android.content.Context

class ServerService {
    fun start(context: Context) {
        WebService.start(context)
    }

    fun stop(context: Context) {
        WebService.stop(context)
    }

    fun isActiveLiveData(context: Context) = WebService.STATUS
}