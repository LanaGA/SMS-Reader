package com.rsf.sms_reader.data.remote.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import com.rsf.sms_reader.data.local.NumbersRepository
import com.rsf.sms_reader.data.remote.IRetrofit
import com.rsf.sms_reader.ui.utils.ADDRESS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class SMSReceiver : BroadcastReceiver() {
    private val repository: NumbersRepository by inject(NumbersRepository::class.java)

    companion object {
        private val MY_SETTINGS = "my_settings"

    }

    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle?
        var currentSMS: SmsMessage?
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            bundle = intent.extras
            val pduObjects = let { bundle?.get("pdus") as Array<*>? }
            if (pduObjects != null) {
                for (aObject in pduObjects) {
                    currentSMS = aObject?.let { bundle?.let { it1 -> getIncomingMessage(it, it1) } }
                    val senderNo = currentSMS!!.displayOriginatingAddress
                    repository.run {
                        getAll()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            it.forEach { item ->
                                                if (item.number == senderNo)
                                                    send(currentSMS, context)
                                            }
                                        },
                                        {
                                            it
                                        }
                                )
                    }

                }
                abortBroadcast()
            }
        }

    }

    private fun send(currentSMS: SmsMessage, context: Context) {
        try {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val site = sp.getString(ADDRESS, "")
            site?.sendPost(currentSMS.displayMessageBody.toString())
            Toast.makeText(context, "Сообщение отпралено", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Проблемы с сетью", Toast.LENGTH_LONG).show()
        }
    }

    private fun String.sendPost(message: String) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("message_text", message)

        val jsonPostService = ServiceGenerator.createService(IRetrofit::class.java, this)
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
        val format = bundle.getString("format")
        return SmsMessage.createFromPdu(aObject as ByteArray, format)
    }
}
