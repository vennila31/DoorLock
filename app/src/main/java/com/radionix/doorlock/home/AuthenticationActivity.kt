package com.radionix.doorlock.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityAuthenticationBinding
import com.radionix.doorlock.pattern.CreatePasswordActivity
import com.radionix.doorlock.pattern.InputPasswordActivity


class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityAuthenticationBinding =
            DataBindingUtil.setContentView(this,R.layout.activity_authentication)
        val handler = Handler()
        handler.postDelayed({

            val sharedPreferences = getSharedPreferences("Skelta", MODE_PRIVATE)
            val password = sharedPreferences.getString("password", "0")
            if (password == "0") {
                val intent = Intent(applicationContext, CreatePasswordActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(applicationContext, InputPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }
}