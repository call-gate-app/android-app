package app.callgate.android.modules.calls

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telecom.TelecomManager
import androidx.core.app.ActivityCompat

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
            throw RuntimeException("Not supported")
        }

        if (!hasAnswerPermissions()) {
            throw RuntimeException("Permission not granted")
        }

        telecomManager.endCall()
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