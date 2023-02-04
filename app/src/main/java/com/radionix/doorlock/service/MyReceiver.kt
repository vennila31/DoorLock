package com.radionix.doorlock.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat


class MyReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context?, p1: Intent?) {
        val serviceIntent = Intent(p0!!, MyService::class.java)
        serviceIntent.putExtra("inputExtra", "Tap for more information or to stop background")
        ContextCompat.startForegroundService(p0, serviceIntent)
    }

}