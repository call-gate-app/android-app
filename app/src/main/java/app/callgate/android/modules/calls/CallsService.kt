package app.callgate.android.modules.calls

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import app.callgate.android.modules.calls.domain.CallDetails
import app.callgate.android.modules.calls.domain.CallState
import app.callgate.android.modules.calls.events.CallEvent
import app.callgate.android.modules.calls.webhooks.CallEventPayload
import app.callgate.android.modules.webhooks.WebHooksService
import app.callgate.android.modules.webhooks.domain.WebHookEvent
import java.lang.reflect.Method


class CallsService(
    private val context: Context,
    private val webHooksService: WebHooksService,
) {
    private val telephonyManager: TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val telecomManager: TelecomManager =
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    fun startCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = phoneNumber.toUri()
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    fun getCall(): CallDetails {
        if (!hasReadPhoneStatePermissions()) {
            throw RuntimeException("Permission not granted")
        }

        // Get current call state
        return when (val callState = telephonyManager.callState) {
            TelephonyManager.CALL_STATE_IDLE -> CallDetails(null, CallState.Idle)
            TelephonyManager.CALL_STATE_RINGING -> CallDetails(
                telephonyManager.line1Number,
                CallState.Ringing,
            )

            TelephonyManager.CALL_STATE_OFFHOOK -> CallDetails(
                telephonyManager.line1Number,
                CallState.Active,
            )

            else -> throw RuntimeException("Unknown call state $callState")
        }
    }

    @SuppressLint("MissingPermission")
    fun endCall(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return endCallReflection()
        }

        if (!hasAnswerPermissions()) {
            throw RuntimeException("Permission not granted")
        }

        @Suppress("DEPRECATION")
        return telecomManager.endCall()
    }

    fun processEvent(event: CallEvent) {
        val webhookEvent = when (event.type) {
            CallEvent.Type.Ringing -> WebHookEvent.CallRinging
            CallEvent.Type.Started -> WebHookEvent.CallStarted
            CallEvent.Type.Ended -> WebHookEvent.CallEnded
        }

        try {
            webHooksService.emit(
                webhookEvent,
                CallEventPayload(event.phoneNumber)
            )
        } catch (th: Throwable) {
            Log.e("CallsService", "Failed to emit webhook event", th)
        }
    }

    private fun endCallReflection(): Boolean {
        try {
            // Get the getITelephony() method
            val telephonyManagerClass = Class.forName(telephonyManager.javaClass.name)
            val getITelephonyMethod: Method =
                telephonyManagerClass.getDeclaredMethod("getITelephony")
            getITelephonyMethod.isAccessible = true

            // Invoke getITelephony() to get the ITelephony interface
            val telephonyInterface = getITelephonyMethod.invoke(telephonyManager)

            // Get the endCall() method
            val telephonyInterfaceClass = Class.forName(telephonyInterface.javaClass.name)
            val endCallMethod: Method = telephonyInterfaceClass.getDeclaredMethod("endCall")

            // Invoke endCall()
            return endCallMethod.invoke(telephonyInterface) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Failed to end call", e)
        }
    }

    private fun hasCallPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasAnswerPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ANSWER_PHONE_CALLS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasReadPhoneStatePermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun String.toUri() = Uri.parse("tel:$this")
}