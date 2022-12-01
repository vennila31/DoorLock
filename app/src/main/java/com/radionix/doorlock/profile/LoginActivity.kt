package com.radionix.doorlock.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityLoginBinding
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.home.MainViewModel

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

    }

    override fun onStarted() {
        binding.addProfile.visibility = View.GONE
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