package com.rsf.sms_reader

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val smsReceiver by lazy { SMSReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (permissionCheck("RECEIVE_SMS") == PackageManager.PERMISSION_GRANTED) {
            registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        } else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_CODE)

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