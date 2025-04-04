package app.callgate.android.modules.webhooks.workers

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import app.callgate.android.BuildConfig
import app.callgate.android.R
import app.callgate.android.extensions.configure
import app.callgate.android.modules.notifications.NotificationsService
import app.callgate.android.modules.webhooks.TemporaryStorage
import app.callgate.android.modules.webhooks.WebhooksSettings
import app.callgate.android.modules.webhooks.domain.WebHookEventDTO
import app.callgate.android.modules.webhooks.plugins.PayloadSigningPlugin
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import io.ktor.serialization.gson.gson
import org.json.JSONException
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SendWebhookWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), KoinComponent {

    private val notificationsSvc: NotificationsService by inject()
//    private val logsSvc: LogsService by inject()

    private val settings: WebhooksSettings by inject()
    private val storage: TemporaryStorage by inject()

    override suspend fun doWork(): ListenableWorker.Result {
        val storageKey = inputData.getString(INPUT_STORAGE_KEY)
        val payload = storageKey?.let {
            storage.get(it)
        } ?: inputData.getString("data")

        if (payload == null) {
//            logsSvc.insert(
//                priority = LogEntry.Priority.ERROR,
//                module = NAME,
//                message = "Empty payload",
//                context = mapOf(
//                    "url" to inputData.getString(INPUT_URL),
//                    "storageKey" to inputData.getString(INPUT_STORAGE_KEY),
//                )
//            )
            return ListenableWorker.Result.failure()
        }

        return when (val result = sendData(payload)) {
            SendResult.Success -> {
//                logsSvc.insert(
//                    priority = LogEntry.Priority.INFO,
//                    module = NAME,
//                    message = "Webhook sent successfully",
//                    context = mapOf(
//                        "url" to inputData.getString(INPUT_URL),
//                        "data" to payload,
//                    )
//                )

                storageKey?.let {
                    storage.remove(it)
                }
                ListenableWorker.Result.success()
            }

            is SendResult.Failure -> {
//                logsSvc.insert(
//                    priority = LogEntry.Priority.ERROR,
//                    module = NAME,
//                    message = "Webhook failed: ${result.error}",
//                    context = mapOf(
//                        "url" to inputData.getString(INPUT_URL),
//                        "data" to payload,
//                    )
//                )

                storageKey?.let {
                    storage.remove(it)
                }
                ListenableWorker.Result.failure()
            }

            is SendResult.Retry -> {
//                logsSvc.insert(
//                    priority = LogEntry.Priority.WARN,
//                    module = NAME,
//                    message = "Webhook failed with retry: ${result.reason}",
//                    context = mapOf(
//                        "url" to inputData.getString(INPUT_URL),
//                        "data" to payload,
//                    )
//                )
                ListenableWorker.Result.retry()
            }
        }
    }

    private suspend fun sendData(payload: String): SendResult {
        try {
            if (runAttemptCount >= settings.retryCount) {
                return SendResult.Failure("Retry limit exceeded")
            }

            val url = inputData.getString(INPUT_URL)
                ?: return SendResult.Failure("Empty url")
            val data = gson.fromJson(payload, JsonObject::class.java)
                ?: return SendResult.Failure("Empty data")

            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(data)
            }

            if (response.status.value !in 200..299) {
                return SendResult.Retry("Status code: ${response.status.value}")
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return SendResult.Failure(e.message ?: e.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            return SendResult.Failure(e.message ?: e.toString())
        } catch (e: Throwable) {
            e.printStackTrace()
            return SendResult.Retry(e.message ?: e.toString())
        } finally {
            client.close()
        }

        return SendResult.Success
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(): ForegroundInfo {
        val notificationId = NotificationsService.NOTIFICATION_ID_WEBHOOK_WORKER
        val notification = notificationsSvc.makeNotification(
            applicationContext,
            notificationId,
            applicationContext.getString(R.string.sending_webhook)
        )

        return ForegroundInfo(notificationId, notification)
    }

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }
        install(ContentNegotiation) {
            gson {
                configure()
            }
        }
        install(DefaultRequest) {
            userAgent("${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}")
        }
        install(PayloadSigningPlugin) {
            secretKeyProvider = { settings.signingKey }
        }
    }

    private sealed class SendResult {
        data object Success : SendResult()
        class Failure(val error: String) : SendResult()
        class Retry(val reason: String) : SendResult()
    }

    companion object : KoinComponent {
        fun start(
            context: Context,
            url: String,
            data: WebHookEventDTO,
            internetRequired: Boolean
        ) {
            get<TemporaryStorage>().put(data.id, gson.toJson(data))

            val work = OneTimeWorkRequestBuilder<SendWebhookWorker>()
                .setInputData(
                    workDataOf(
                        INPUT_URL to url,
                        INPUT_STORAGE_KEY to data.id,
                    )
                )
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .apply {
                    if (internetRequired) {
                        setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                    }
                }
                .build()

            WorkManager.getInstance(context)
                .enqueue(work)
        }

        private val gson = GsonBuilder().configure().create()

        private const val INPUT_URL = "url"
        private const val INPUT_STORAGE_KEY = "storageKey"
    }
}