package com.radionix.doorlock.helper

interface AuthListener {
    fun onStarted()
    fun onSuccess(message: String)
    fun onFailure(message: String)
}