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
import androidx.core.app.ActivityCompat
import java.lang.reflect.Method


class CallsService(
    private val context: Context
) {
    private val telecomManager: TelecomManager =
        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    fun startCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = phoneNumber.toUri()
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

//    @SuppressLint("MissingPermission")
//    fun addCall(phoneNumber: String) {
//        if (!hasCallPermissions()) {
//            throw RuntimeException("Permission not granted")
//        }
//
//        telecomManager.placeCall(phoneNumber.toUri(), null)
//    }

    @SuppressLint("MissingPermission")
    fun endCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            endCallReflection()
            return
        }

        if (!hasAnswerPermissions()) {
            throw RuntimeException("Permission not granted")
        }

        telecomManager.endCall()
    }

    private fun endCallReflection() {
        try {
            // Get the getITelephony() method
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val telephonyManagerClass = Class.forName(telephonyManager.javaClass.getName())
            val getITelephonyMethod: Method =
                telephonyManagerClass.getDeclaredMethod("getITelephony")
            getITelephonyMethod.isAccessible = true

            // Invoke getITelephony() to get the ITelephony interface
            val telephonyInterface = getITelephonyMethod.invoke(telephonyManager)

            // Get the endCall() method
            val telephonyInterfaceClass = Class.forName(telephonyInterface.javaClass.getName())
            val endCallMethod: Method = telephonyInterfaceClass.getDeclaredMethod("endCall")

            // Invoke endCall()
            endCallMethod.invoke(telephonyInterface)
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

    private fun String.toUri() = Uri.parse("tel:$this")
}