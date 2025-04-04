package app.callgate.android.modules.calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log


class CallsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!intent.action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

//        Log.d(
//            "CallsReceiver",
//            "Extras: ${
//                intent.extras?.keySet()?.joinToString { "${it}: ${intent.extras?.get(it)}" }
//            }"
//        )

        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            // Phone ringing
            Log.d("CallsReceiver", "Incoming call from $incomingNumber")
        } else if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            // Call active
            Log.d("CallsReceiver", "Call active with $incomingNumber")
        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
            // Call ended
            Log.d("CallsReceiver", "Call ended with $incomingNumber")
        }
    }
}