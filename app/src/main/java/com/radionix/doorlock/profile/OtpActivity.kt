package com.radionix.doorlock.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityOtpBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.home.MainViewModel
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() , AuthListener {

    lateinit var binding : ActivityOtpBinding


    var verificationId : String? = ""
    var emailStr : String? = ""
    var usernameStr : String? = ""
    var passStr : String? = ""
    var uid : String? = ""
    lateinit var appController: AppController

    var phoneNumber:String? = ""
    var reqCode = ""

    lateinit var databaseReference: DatabaseReference

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)

        databaseReference = FirebaseDatabase.getInstance().getReference("")

        binding.lifecycleOwner = this
        binding.executePendingBindings()
        viewModel.authListener = this
        viewModel.startTime()


        appController = AppController(this)

        phoneNumber = intent.getStringExtra("number")
        verificationId = intent.getStringExtra("verificationId")
        emailStr = intent.getStringExtra("email")
        usernameStr = intent.getStringExtra("username")
        passStr = intent.getStringExtra("password")
        reqCode = intent.getStringExtra("reqCode").toString()
        uid = intent.getStringExtra("uid")


        /** auto detect otp **/

        if(checkPermission()){
            OtpReceiver.setEditText(binding.otpView)
        }else{
            requestPermission()
            OtpReceiver.setEditText(binding.otpView)
        }

        binding.regenOtp.setOnClickListener {
            viewModel.startTime()
            sendOtp()
        }



        binding.otpView.setOtpCompletionListener {otp ->
            binding.verify.setOnClickListener {

                if(!verificationId.isNullOrEmpty()){

                    binding.progressBar.visibility = View.VISIBLE
                    binding.verify.visibility = View.GONE

                    val phoneAuthCredential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId!!,otp.toString()
                    )

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener {

                            binding.progressBar.visibility = View.GONE
                            binding.verify.visibility = View.VISIBLE
                            if(it.isSuccessful){

                                viewModel.signup(this, emailStr!!,phoneNumber!!, usernameStr!!,passStr!!,uid!!)

                            }else{
                                Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                            }

                        }

                }


            }

        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),50
        )
    }
    private fun checkPermission() : Boolean{

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){

            return true
        }

        return false

    }

    private fun sendOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91${phoneNumber!!.trim()}",
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(this@OtpActivity,"Failed to send OTP , check your number",Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    Toast.makeText(this@OtpActivity,"OTP sent successfully..",Toast.LENGTH_SHORT).show()


                }


            }
        )
    }


    override fun onStarted() {

        binding.time.visibility = View.VISIBLE
        binding.regenOtp.isEnabled = false

    }

    override fun onSuccess(message: String) {

      /*  binding.verify.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        Toast.makeText(
            this, message,
            Toast.LENGTH_SHORT
        ).show()

*/
    }

    override fun onFailure(message: String) {
        if(message == "OTP")
        {
            binding.time.visibility = View.GONE
            binding.regenOtp.isEnabled = true
            binding.regenOtp.background = resources.getDrawable(R.drawable.bg_button)
        }else
        {
            binding.time.text = message
        }
     /*   Toast.makeText(
            this, message,
            Toast.LENGTH_SHORT
        ).show()*/
    }
}