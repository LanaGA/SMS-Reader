package com.rsf.sms_reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


internal class SMSReceiver : BroadcastReceiver() {
    companion object {
        private const val REQ_NUMBER = "900"
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
                        if (senderNo != REQ_NUMBER) {
                            Toast.makeText(context, "other message", Toast.LENGTH_LONG).show()
                            continue
                        }
                        message = currentSMS!!.displayMessageBody
                        //Toast.makeText(context, "message: $message", Toast.LENGTH_LONG).show()
                        try {
                            sendPost("http://34.66.156.110/", message.toString())
                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Sending error", Toast.LENGTH_LONG).show()
                        }
                    }
                    abortBroadcast()
                }
            }
        }
    }

    private fun sendPost(urlAddress: String, message: String) {
        val thread = Thread(Runnable {
            try {
                val url = URL(urlAddress)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.doInput = true
                val jsonParam = JSONObject()
                jsonParam.put("message", URLEncoder.encode(message, "utf-8"))
                val os = DataOutputStream(conn.outputStream)
                os.writeBytes(jsonParam.toString())
                os.flush()
                os.close()
                conn.disconnect()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
        thread.start()
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