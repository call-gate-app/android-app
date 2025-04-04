package app.callgate.android.modules.calls.events

data class CallEvent(
    val type: Type,
    val phoneNumber: String?,
) {
    enum class Type {
        Ringing,
        Started,
        Ended,
    }
}