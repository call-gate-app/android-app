package app.callgate.android.modules.webhooks.domain

import com.google.gson.annotations.SerializedName

enum class WebHookEvent {
    @SerializedName("call:ringing")
    CallRinging,

    @SerializedName("call:started")
    CallStarted,

    @SerializedName("call:ended")
    CallEnded,
}