package com.radionix.doorlock.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.CountDownTimer
import android.renderscript.Sampler.Value
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.radionix.doorlock.data.User
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.pattern.CreatePasswordActivity
import com.radionix.doorlock.profile.CreateProfileActivity
import com.radionix.doorlock.profile.LoginActivity
import com.radionix.doorlock.profile.OtpActivity
import java.util.concurrent.TimeUnit


class MainViewModel : ViewModel() {

    var name: String? = null
    var mobile: String? = null
    var email: String? = null

    var authListener: AuthListener? = null

    var reqCode = 0

    var productCode : String? = null
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("")

    var timeLeftInMilliSeconds: Long = 120000
    var timerText: String = ""


    fun signup(view : Context , emailStr : String , mobStr : String , nameStr : String)
    {
        val appController = AppController(view)
        val data = User(emailStr,mobStr)
        authListener!!.onSuccess("Success")
        databaseReference.child("userData").child(nameStr).setValue(data)
        appController.putUsername(nameStr)
        appController.putMobile(mobStr)
        appController.putEmail(emailStr)
        Intent(view, AuthenticationActivity::class.java).also {
            view.startActivity(it)
            val activity = view as Activity
            activity.finish()
        }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


    }


    fun sendOtp(view: View)
    {
        val appController = AppController(view.context)
        if(email.isNullOrEmpty() || mobile.isNullOrEmpty() || name.isNullOrEmpty() )
        {
            authListener!!.onFailure("Please enter all values")
            return
        }
        authListener!!.onStarted()

        if(reqCode == 101)
        {

            if(appController.getUsername() == name && appController.getMobile() == mobile && appController.getEmail() == email)
            {
                authListener!!.onSuccess("Success")
                Intent(view.context, CreatePasswordActivity::class.java).also {
                    view.context.startActivity(it)
                    val activity = view.context as Activity
                    activity.finish()
                }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            }else{
                authListener!!.onFailure("Invalid details")
            }

        }else{
            val activity = view.context as Activity
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91${mobile!!.trim()}",
                60,
                TimeUnit.SECONDS,
                activity,
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        authListener!!.onFailure("Failed to send OTP , check your number")
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        p1: PhoneAuthProvider.ForceResendingToken
                    ) {
                        authListener!!.onSuccess("OTP sent successfully..")

                        view.context.startActivity(Intent(view.context,OtpActivity::class.java)
                            .putExtra("verificationId", verificationId)
                            .putExtra("number", mobile!!.trim())
                            .putExtra("username",name)
                            .putExtra("email",email)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        activity.finish()

                    }


                }
            )
        }


    }


    fun isOnline(c: Context): Boolean {
        val cm =
            c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null && ni.isConnectedOrConnecting
    }
    fun checkCameraPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(context as Activity,arrayOf(Manifest.permission.CAMERA), 101)
        }
        else{
            return
        }

    }

    fun login(view : View)
    {
        val activity = view.context as Activity
        activity.finish()
        view.context.startActivity(Intent(view.context,LoginActivity::class.java))

    }
    fun register(view: View)
    {
        val activity = view.context as Activity
        activity.finish()
        view.context.startActivity(Intent(view.context,CreateProfileActivity::class.java))
    }
    fun signIn(view: View)
    {
        val appController = AppController(view.context)

        if(email.isNullOrEmpty() || mobile.isNullOrEmpty())
        {
            authListener!!.onFailure("Invalid email or Mobile number")
            return
        }

        authListener!!.onStarted()


        databaseReference.child("userData").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                authListener!!.onFailure("Login failed")

            }

            override fun onDataChange(data : DataSnapshot) {


                var isLog = false

                for ( i in data.children){

                    val username = i.child( "mobile").value.toString()

                    if(mobile == username)
                    {
                        isLog = true
                        authListener!!.onSuccess("Login Success")
                        appController.putUsername(i.key.toString())
                        appController.putEmail(email!!)
                        appController.putMobile(mobile!!)
                        Intent(view.context,AuthenticationActivity::class.java).also {
                            view.context.startActivity(it)
                            val activity = view.context as Activity
                            activity.finish()
                        }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }else{
                        isLog = false

                    }



                }

                if(!isLog){
                    authListener!!.onFailure("Invalid credentials")
                }

            }


        })




    }


    fun startTime() {


        authListener!!.onStarted()

        var countDownTimer : CountDownTimer = object : CountDownTimer(timeLeftInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMilliSeconds = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                authListener!!.onFailure("OTP")
                timeLeftInMilliSeconds = 120000
                timerText = ""
            }
        }.start()
    }

    fun updateTimer() {
        val minutes = timeLeftInMilliSeconds.toInt() / 60000
        val seconds = timeLeftInMilliSeconds.toInt() % 60000 / 1000
        timerText = "Time to Expire: $minutes"
        timerText += ":"
        if (seconds < 10) {
            timerText += "0"
        }
        timerText += seconds
        authListener!!.onFailure(timerText)

    }



}