package com.radionix.doorlock.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityLoginBinding
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.home.MainViewModel
import com.radionix.doorlock.pattern.ForgetPasswordActivity

class LoginActivity : AppCompatActivity() , AuthListener {

    lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  =
            DataBindingUtil.setContentView(this,R.layout.activity_login)
        val mainViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        binding.executePendingBindings()

        mainViewModel.authListener = this

        binding.eye.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.forgetPassword.setOnClickListener {
            startActivity(
                Intent(this, ForgetPasswordActivity::class.java))
        }

    }

    override fun onStarted() {
        binding.addProfile.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onSuccess(message: String) {
        binding.addProfile.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        Toast.makeText(
            this, message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onFailure(message: String) {
        binding.addProfile.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        Toast.makeText(
            this, message,
            Toast.LENGTH_SHORT
        ).show()
    }
}