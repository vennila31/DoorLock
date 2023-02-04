package com.radionix.doorlock.pattern

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.andrognito.patternlockview.PatternLockView.Dot
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityInputPasswordBinding
import com.radionix.doorlock.home.MainActivity
import com.radionix.doorlock.profile.CreateProfileActivity
import java.util.concurrent.Executor


class InputPasswordActivity : AppCompatActivity() {

    lateinit var password : String

    var reqCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityInputPasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_input_password)

        val sharedPreferences = getSharedPreferences("Skelta", MODE_PRIVATE)
        password = sharedPreferences.getString("password", "0")!!

        reqCode = intent.getIntExtra("reqCode",0)

        useBiometric()


        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {}
            override fun onProgress(progressPattern: List<Dot>) {}
            override fun onComplete(pattern: List<Dot>) {
                if (password == PatternLockUtils.patternToString(binding.patternLockView, pattern)) {
                    if(reqCode == 100)
                    {
                        val intent = Intent(applicationContext, ForgetPasswordActivity::class.java)
                            .putExtra("reqCode",100)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Toast.makeText(this@InputPasswordActivity, "Incorrect Password", Toast.LENGTH_SHORT)
                        .show()
                    binding.patternLockView.clearPattern()
                }
            }

            override fun onCleared() {}
        })

        binding.forgotPass.setOnClickListener {
            startActivity(Intent(this,ForgetPasswordActivity::class.java)
                .putExtra("reqCode",100))
        }

    }

    private fun useBiometric()
    {

        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this@InputPasswordActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()

                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if(reqCode == 100)
                    {
                        val intent = Intent(applicationContext, CreatePasswordActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Fingerprint doesn't match", Toast.LENGTH_SHORT).show()

                }
            })

        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Skelta")
            .setDescription("Use your fingerprint").setNegativeButtonText("Cancel")
            .build()
        biometricPrompt.authenticate(promptInfo)

        val biometricManager: BiometricManager = BiometricManager.from(this)
       /* when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                msgtex.setText("You can use the fingerprint sensor to login")
                msgtex.setTextColor(Color.parseColor("#fafafa"))
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                msgtex.setText("This device doesnot have a fingerprint sensor")
                loginbutton.setVisibility(View.GONE)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                msgtex.setText("The biometric sensor is currently unavailable")
                loginbutton.setVisibility(View.GONE)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                msgtex.setText("Your device doesn't have fingerprint saved,please check your security settings")
                loginbutton.setVisibility(View.GONE)
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }*/
        }




}