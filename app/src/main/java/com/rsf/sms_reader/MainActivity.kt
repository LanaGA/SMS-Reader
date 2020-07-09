package com.rsf.sms_reader

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    var smsList: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //  listView = findViewById(R.id.idList);
        check_sms.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
                showContacts()
            else
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), PERMISSION_REQUEST_CODE)
        }
    }

    private fun permissionCheck(permission: String): Int {
        return ContextCompat.checkSelfPermission(this, "Manifest.permission.$permission")
    }

    private fun showContacts() {
        val uri = Uri.parse("content://sms/")
        val contentResolver = contentResolver
        val sms = "address='$REQ_NUMBER'"
        val cursor = contentResolver.query(uri, arrayOf("_id", "body"), sms, null, null) ?: return
        println(cursor.count)

        while (cursor.moveToNext()) {
            val body = cursor.getString(cursor.getColumnIndex("body"))
            Toast.makeText(this, body, Toast.LENGTH_LONG).show()
        }
        cursor.close()
    }

    companion object {
        private const val REQ_NUMBER = "900"
        private const val PERMISSION_REQUEST_CODE = 100
    }
}