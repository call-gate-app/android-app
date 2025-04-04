package app.callgate.android.modules.calls.domain

data class CallDetails(
    val phoneNumber: String?,
    val state: CallState,
)
