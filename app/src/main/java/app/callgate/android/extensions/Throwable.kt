package app.callgate.android.extensions

val Throwable.description: String
    get() {
        return (localizedMessage ?: message ?: toString()) +
                (cause?.let { ": " + it.description } ?: "")
    }