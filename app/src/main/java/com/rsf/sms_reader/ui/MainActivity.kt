package com.rsf.sms_reader.ui

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.rsf.sms_reader.R
import com.rsf.sms_reader.data.local.base.NumbersEntity
import com.rsf.sms_reader.data.remote.service.SMSReceiver
import com.rsf.sms_reader.setAdapterAndCleanupOnDetachFromWindow
import com.rsf.sms_reader.setData
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private val viewModel: ViewModel by viewModel()
    private val smsReceiver by lazy { SMSReceiver() }
    private val adapter = ListDelegationAdapter(adapterDelegate {
        viewModel.processUiEvent(UiEvent.DeleteNumber(it))
    })
    private val sp by lazy { getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        sharedCheck()
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setAdapterAndCleanupOnDetachFromWindow(adapter)
        viewModel.viewState.observe(this, Observer(::render))
        buttonAddNumber.setOnClickListener {
            viewModel.processUiEvent(UiEvent.CreateNumber(NumbersEntity(newNumberText.text.toString())))
        }
        buttonSetAddress.setOnClickListener {
            sp.edit().putString(ADDRESS, addressText.toString()).apply()
            addressText.setText(sp.getString(ADDRESS, ""))
        }
        checkPermission()
    }

    private fun sharedCheck() {
        val address = "http://34.66.156.110"
        val hasVisited = sp.getBoolean("hasVisited", false)

        if (!hasVisited) {
            val e: Editor = sp.edit()
            e.putBoolean("hasVisited", true)
            e.putString(ADDRESS, address)
            e.apply()
        }
    }

    private fun checkPermission() {
        if ("RECEIVE_SMS".permissionCheck() == PackageManager.PERMISSION_GRANTED) {
            registerReceiver(smsReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
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

    private fun String.permissionCheck(): Int {
        return ContextCompat.checkSelfPermission(this@MainActivity, "Manifest.permission.${this}")
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}