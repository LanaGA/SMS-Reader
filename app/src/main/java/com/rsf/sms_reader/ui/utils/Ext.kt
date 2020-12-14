package com.rsf.sms_reader.ui.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Activity.checkPermissionForReadSms(): Boolean {
    val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
    )

    return result == PackageManager.PERMISSION_GRANTED
}