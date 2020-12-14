package com.rsf.sms_reader.ui

import android.Manifest
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.rsf.sms_reader.R
import com.rsf.sms_reader.data.local.base.NumbersEntity
import com.rsf.sms_reader.data.remote.service.SMSReceiver
import com.rsf.sms_reader.setAdapterAndCleanupOnDetachFromWindow
import com.rsf.sms_reader.setData
import com.rsf.sms_reader.ui.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: ViewModel by viewModel()
    private val smsReceiver by lazy { SMSReceiver() }
    private val adapter = ListDelegationAdapter(adapterDelegate {
        viewModel.processUiEvent(UiEvent.DeleteNumber(it))
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedCheck(preferences)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setAdapterAndCleanupOnDetachFromWindow(adapter)
        addressText.setText(preferences.getString(ADDRESS, ""))
        viewModel.viewState.observe(this, Observer(::render))
        buttonAddNumber.setOnClickListener {
            viewModel.processUiEvent(UiEvent.CreateNumber(NumbersEntity(newNumberText.text.toString())))
        }
        buttonSetAddress.setOnClickListener {
            preferences.edit().putString(ADDRESS, addressText.text.toString()).commit()
        }
        checkPermission()
    }

    private fun sharedCheck(preferences: SharedPreferences) {
        val address = "http://34.66.156.110"
        val hasVisited = preferences.getBoolean("hasVisited", false)

        if (!hasVisited) {
            val e: Editor = preferences.edit()
            e.putBoolean("hasVisited", true)
            e.putString(ADDRESS, address)
            e.commit()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkPermissionForReadSms()) {
            registerSmsReceiver()
        } else {
            Toast.makeText(this, "Пожалуйста, предоставьте разрешение на чтение и обработку смс.", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerSmsReceiver() = registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))

    private fun checkPermission() {
        if (checkPermissionForReadSms()) {
            registerSmsReceiver()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), PERMISSION_REQUEST_CODE)
        }
    }

    private fun render(viewState: ViewState) {
        when (viewState.status) {
            STATUS.LOAD -> {
                viewModel.processUiEvent(UiEvent.GetAllNumbers)
            }
            STATUS.CONTENT -> adapter.setData(viewState.numbersList)
            STATUS.ERROR -> {
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}