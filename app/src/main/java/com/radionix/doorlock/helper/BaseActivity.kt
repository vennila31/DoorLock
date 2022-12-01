package com.radionix.doorlock.helper

import android.app.Activity
import android.widget.Toast

open class BaseActivity : Activity(){

    fun makeToast(msg : String)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
}
