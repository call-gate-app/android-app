package app.callgate.android.modules.server

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.callgate.android.BuildConfig
import app.callgate.android.R
import app.callgate.android.extensions.configure
import app.callgate.android.extensions.description
import app.callgate.android.modules.notifications.NotificationsService
import app.callgate.android.modules.server.routes.CallsRoutes
import app.callgate.android.modules.server.routes.WebhooksRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.http.toHttpDate
import io.ktor.serialization.gson.gson
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.date.GMTDate
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import kotlin.concurrent.thread

class WebService : Service() {
    private val settings: ServerSettings by inject()
    private val notificationsService: NotificationsService by inject()

    private val port = settings.port
    private val username = settings.username
    private val password = settings.password

    private val wakeLock: PowerManager.WakeLock by lazy {
        (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.javaClass.name)
        }
    }
    private val wifiLock: WifiManager.WifiLock by lazy {
        @Suppress("DEPRECATION")
        (getSystemService(Context.WIFI_SERVICE) as WifiManager).createWifiLock(
            WifiManager.WIFI_MODE_FULL_HIGH_PERF,
            this.javaClass.name
        )
    }

    private val server by lazy {
        embeddedServer(
            Netty,
            port = port,
            watchPaths = emptyList(),
        ) {
            install(Authentication) {
                basic("auth-basic") {
                    realm = "Access to CallGate"
                    validate { credentials ->
                        when {
                            credentials.name == username
                                    && credentials.password == password -> UserIdPrincipal(
                                credentials.name
                            )

                            else -> null
                        }
                    }
                }
            }
            install(ContentNegotiation) {
                gson {
                    if (BuildConfig.DEBUG) {
                        setPrettyPrinting()
                    }
                    configure()
                }
            }
            install(StatusPages) {
                exception<Throwable> { call, cause ->
                    call.respond(
                        when (cause) {
                            is IllegalArgumentException -> HttpStatusCode.BadRequest
                            is BadRequestException -> HttpStatusCode.BadRequest
                            is NotFoundException -> HttpStatusCode.NotFound
                            else -> HttpStatusCode.InternalServerError
                        },
                        mapOf("message" to cause.description)
                    )
                }
            }
            install(createApplicationPlugin(name = "DateHeader") {
                onCall { call ->
                    call.response.header(
                        "Date",
                        GMTDate(null).toHttpDate()
                    )
                }
            })
            routing {
                authenticate("auth-basic") {
                    route("/api/v1") {
                        route("/calls") {
                            CallsRoutes().register(this)
                        }
                        route("/webhooks") {
                            WebhooksRoutes(get()).register(this)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        server.start()
        wakeLock.acquire()
        wifiLock.acquire()

        status.postValue(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationsService.makeNotification(
            this,
            NotificationsService.NOTIFICATION_ID_LOCAL_SERVICE,
            getString(
                R.string.server_is_running_on_port_d,
                port
            )
        )

        startForeground(NotificationsService.NOTIFICATION_ID_LOCAL_SERVICE, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        wifiLock.release()
        wakeLock.release()
        thread { server.stop() }

        @Suppress("DEPRECATION")
        stopForeground(true)

        status.postValue(false)

        super.onDestroy()
    }

    companion object {
        private val status = MutableLiveData<Boolean>(false)
        val STATUS: LiveData<Boolean> = status

        fun start(context: Context) {
            val intent = Intent(context, WebService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WebService::class.java))
        }
    }
}