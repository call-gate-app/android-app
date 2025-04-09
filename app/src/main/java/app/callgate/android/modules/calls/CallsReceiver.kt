package app.callgate.android.modules.calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import app.callgate.android.modules.calls.events.CallEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class CallsReceiver : BroadcastReceiver(), KoinComponent {
    private val callsService: CallsService by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED != intent.action) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        @Suppress("DEPRECATION")
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

//        Log.d(
//            "CallsReceiver",
//            "Extras: ${
//                intent.extras?.keySet()?.joinToString { "${it}: ${intent.extras?.get(it)}" }
//            }"
//        )

        val event = when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                // Phone ringing
                Log.d("CallsReceiver", "Incoming call from $incomingNumber")
                CallEvent(CallEvent.Type.Ringing, incomingNumber)
            }

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                // Call active
                Log.d("CallsReceiver", "Call active with $incomingNumber")
                CallEvent(CallEvent.Type.Started, incomingNumber)
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                // Call ended
                Log.d("CallsReceiver", "Call ended with $incomingNumber")
                CallEvent(CallEvent.Type.Ended, incomingNumber)
            }

            else -> {
                Log.d("CallsReceiver", "Unknown state $state")
                return
            }
        }

        callsService.processEvent(event)
    }
}