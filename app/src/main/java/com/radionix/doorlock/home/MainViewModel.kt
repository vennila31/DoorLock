package com.radionix.doorlock.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
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
    var password : String? = null

    var forgetEmail : String? = null
    var forgetPass : String? = null

    var authListener: AuthListener? = null

    var reqCode = 0

    var productCode : String? = null
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("")
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val auth = FirebaseAuth.getInstance()


    var timeLeftInMilliSeconds: Long = 120000
    var timerText: String = ""


    fun signup(view : Context , emailStr : String , mobStr : String , nameStr : String , passStr : String , uid : String)
    {
        val appController = AppController(view)
        val data = User(nameStr, emailStr,passStr,mobStr)
        authListener!!.onSuccess("Success")
        databaseReference.child("userData").child(uid).setValue(data)
        appController.putUid(uid)
        appController.putUsername(nameStr)
        appController.putMobile(mobStr)
        appController.putEmail(emailStr)
        appController.putPass(passStr)
        Intent(view, CreatePasswordActivity::class.java).also {
            view.startActivity(it)
            val activity = view as Activity
            activity.finish()
        }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


    }

    fun forgetPass(view : View)
    {
        val appController = AppController(view.context)
        if(reqCode == 100){
            if(forgetEmail.isNullOrEmpty() || forgetPass.isNullOrEmpty()){
                authListener!!.onFailure("Values should not be empty")
                return
            }
            authListener!!.onStarted()

            if(appController.getEmail() == forgetEmail && appController.getPass() == forgetPass){
                authListener!!.onSuccess("Success")
                view.context.startActivity(Intent(view.context, CreatePasswordActivity::class.java))
                val activity = view.context as Activity
                activity.finish()

            }else{
                authListener!!.onFailure("Invalid Credentials")
            }


        }else{
            if(forgetEmail.isNullOrEmpty()){
                authListener!!.onFailure("Email id should not be empty")
                return
            }
            authListener!!.onStarted()

            auth.sendPasswordResetEmail(forgetEmail!!)
                .addOnCompleteListener{task ->

                    if(task.isSuccessful){
                        authListener!!.onSuccess("We have send you instructions to reset password!")

                    }else{
                        authListener!!.onFailure("Failed to send.. Try again")
                    }

                }
        }



    }


    fun sendOtp(view: View)
    {
        if(email.isNullOrEmpty() || mobile.isNullOrEmpty() || name.isNullOrEmpty() || password.isNullOrEmpty() )
        {
            authListener!!.onFailure("Please enter all values")
            return
        }
        authListener!!.onStarted()

      /*  databaseReference.child("userData").child(name!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.child("email").value == null)
                    {

                    }else{
                        authListener!!.onFailure("Username already exists")
                        return
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                    authListener!!.onFailure(error.message)

                }


            })*/

        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authListener!!.onSuccess("Signup Success")

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
                                    .putExtra("password",password)
                                    .putExtra("uid", firebaseAuth.currentUser!!.uid)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                activity.finish()

                            }


                        }
                    )


                } else {
//                    authListener!!.onFailure("Registration Failure")
                    authListener!!.onFailure(task.exception!!.message.toString())
                }
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
        view.context.startActivity(Intent(view.context,LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))

    }
    fun register(view: View)
    {
        view.context.startActivity(Intent(view.context,CreateProfileActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }
    fun signIn(view: View)
    {
        val appController = AppController(view.context)

        if(email.isNullOrEmpty() || password.isNullOrEmpty())
        {
            authListener!!.onFailure("Invalid email or Password")
            return
        }

        authListener!!.onStarted()


        firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    authListener!!.onSuccess("Login Success")

                    appController.putEmail(email!!)
                    appController.putPass(password!!)
                    appController.putUid(firebaseAuth.currentUser!!.uid)

                    putUserName(view.context)

                    Intent(view.context,CreatePasswordActivity::class.java).also {
                        view.context.startActivity(it)
                        val activity = view.context as Activity
                        activity.finish()
                    }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                } else {
                    authListener!!.onFailure("Invalid credentials")
                }
            }






    }

    private fun putUserName(context: Context)
    {
        val appController = AppController(context)

        databaseReference.child("userData").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data : DataSnapshot) {

                for ( i in data.children){

                    val username = i.child( "email").value.toString()

                    if(username == email ){

                        appController.putUsername(i.child("name").value.toString())
                        appController.putMobile(i.child("mobile").value.toString())


                    }

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