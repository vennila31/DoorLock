package com.radionix.doorlock.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.radionix.doorlock.data.User
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.helper.AuthListener
import com.radionix.doorlock.pattern.CreatePasswordActivity
import com.radionix.doorlock.profile.CreateProfileActivity
import com.radionix.doorlock.profile.LoginActivity


class MainViewModel : ViewModel() {

    var name: String? = null
    var mobile: String? = null
    var email: String? = null

    var authListener: AuthListener? = null

    var reqCode = 0

    var productCode : String? = null
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("")


    fun signup(view : View)
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
            val data = User(email!!,mobile!!)
            authListener!!.onSuccess("Success")
            databaseReference.child("userData").child(name!!).setValue(data)
            appController.putUsername(name!!)
            appController.putMobile(mobile!!)
            appController.putEmail(email!!)
            Intent(view.context, AuthenticationActivity::class.java).also {
                view.context.startActivity(it)
                val activity = view.context as Activity
                activity.finish()
            }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }


       /* databaseReference.child("userData").child(name!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.child("email").value == null)
                {
                    firebaseAuth.createUserWithEmailAndPassword(email!!, mobile!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                            } else {
                                authListener!!.onFailure("Signup Failure")
                            }
                        }
                }else{
                    authListener!!.onFailure("Username already exists")
                    return
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }


        })

*/

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

        authListener!!.onSuccess("Login Success")

        appController.putEmail(email!!)
        appController.putMobile(mobile!!)

        putUserName(view.context)

        Intent(view.context,AuthenticationActivity::class.java).also {
            view.context.startActivity(it)
            val activity = view.context as Activity
            activity.finish()
        }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

       /* firebaseAuth.signInWithEmailAndPassword(email!!, mobile!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {



                } else {
                    authListener!!.onFailure("Invalid Credentials")
                }
            }*/
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

                        appController.putUsername(i.key.toString())

                    }

                }


            }


        })

    }


}