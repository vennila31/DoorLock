package com.radionix.doorlock.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWork(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val serviceIntent = Intent(context, MyService::class.java)
        serviceIntent.putExtra("inputExtra", "Tap for more information or to stop background")
        ContextCompat.startForegroundService(context, serviceIntent)
        return Result.success()
    }
}