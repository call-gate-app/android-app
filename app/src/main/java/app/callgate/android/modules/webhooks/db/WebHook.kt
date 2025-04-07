package app.callgate.android.modules.webhooks.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.callgate.android.domain.EntitySource
import app.callgate.android.modules.webhooks.domain.WebHookEvent

@Entity
data class WebHook(
    @PrimaryKey
    val id: String,
    val url: String,
    val event: WebHookEvent,
    val source: EntitySource,
)