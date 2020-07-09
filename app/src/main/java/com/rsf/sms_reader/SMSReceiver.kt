package com.rsf.sms_reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import java.util.*


private class SMSReceiver : BroadcastReceiver() {
    companion object {
        private const val REQ_NUMBER = "+79997585058"
    }
    private var bundle: Bundle? = null
    private var currentSMS: SmsMessage? = null
    private var message: String? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            bundle = intent.extras
            if (bundle != null) {
                val pduObjects = bundle!!["pdus"] as Array<*>?
                if (pduObjects != null) {
                    for (aObject in pduObjects) {
                        currentSMS = aObject?.let { getIncomingMessage(it, bundle!!) }
                        val senderNo = currentSMS!!.displayOriginatingAddress
                        if (senderNo != REQ_NUMBER) continue
                        message = currentSMS!!.displayMessageBody
                        Toast.makeText(context, "senderNum: $senderNo :\n message: $message", Toast.LENGTH_LONG).show()
                    }
                    abortBroadcast()
                }
            }
        }
    }

    private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage? {
        val currentSMS: SmsMessage
        currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else {
            SmsMessage.createFromPdu(aObject as ByteArray)
        }
        return currentSMS
    }
}