package com.radionix.doorlock.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityCreateProfileBinding
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.home.MainViewModel

class CreateProfileActivity : AppCompatActivity() , AuthListener {
    lateinit var binding: ActivityCreateProfileBinding

    var reqCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding  =
            DataBindingUtil.setContentView(this,R.layout.activity_create_profile)
        val mainViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        reqCode = intent.getIntExtra("reqCode",0)

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