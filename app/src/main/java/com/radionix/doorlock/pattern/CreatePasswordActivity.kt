package com.radionix.doorlock.pattern

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityCreatePasswordBinding
import com.radionix.doorlock.home.MainActivity


class CreatePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityCreatePasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_password)


        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {}
            override fun onProgress(progressPattern: List<PatternLockView.Dot>) {}
            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                val sharedPreferences = getSharedPreferences("Skelta", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(
                    "password",
                    PatternLockUtils.patternToString(binding.patternLockView, pattern)
                )
                editor.apply()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onCleared() {}
        })


    }
}