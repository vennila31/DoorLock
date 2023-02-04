package com.radionix.doorlock.pattern

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityForgetPasswordBinding
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.home.MainViewModel

class ForgetPasswordActivity : AppCompatActivity() , AuthListener {

    var reqCode = 0

    lateinit var binding : ActivityForgetPasswordBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password)

        reqCode = intent.getIntExtra("reqCode",0)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.authListener = this
        viewModel.reqCode = reqCode
        binding.executePendingBindings()

        if(reqCode == 100){
            binding.resetLink.text = "Verify"
            binding.passValue.visibility = View.VISIBLE
            binding.eye.visibility = View.VISIBLE

        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.eye.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.passValue.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                binding.passValue.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }


    }

    override fun onStarted() {
        binding.spinKit.visibility = View.VISIBLE
        binding.resetLink.visibility = View.GONE
    }

    override fun onSuccess(message: String) {
        binding.spinKit.visibility = View.GONE
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        binding.spinKit.visibility = View.GONE
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }
}