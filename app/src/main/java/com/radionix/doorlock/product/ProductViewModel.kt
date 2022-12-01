package com.radionix.doorlock.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.home.AuthenticationActivity
import com.radionix.doorlock.profile.CreateProfileActivity

class ProductViewModel : ViewModel()  {


    var productName :String? = null
    var productCode : String? = null


    fun isOkProduct(view : Context)
    {
        val appController  = AppController(view)
        if (appController.getIsOkProduct()) {
            if(appController.getUsername() == "" || appController.getEmail() == "" || appController.getMobile() == "" )
            {
                Intent(view, CreateProfileActivity::class.java).also {
                    view.startActivity(it)
                    val activity = view as Activity
                    activity.finish()
                }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }else
            {
                Intent(view, AuthenticationActivity::class.java).also {
                    view.startActivity(it)
                    val activity = view as Activity
                    activity.finish()
                }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        }
    }

    fun scan(view: View)
    {
        Intent(view.context, ScannerActivity::class.java).also {
            view.context.startActivity(it)
        }
    }

    fun getCheck(view: View) {

        val appController = AppController(view.context)
        if(!productName.isNullOrEmpty())
        {
            if(productName!!.contains("Rdx2702",false)){
                appController.putIsOkProduct(true)
                Intent(view.context, CreateProfileActivity::class.java).also {
                    view.context.startActivity(it)
                    val activity = view.context as Activity
                    activity.finish()
                }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }else{
                Toast.makeText(
                    view.context, "Incorrect product ID...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else{
            Toast.makeText(
                view.context, "Product code shouldn't be empty",
                Toast.LENGTH_SHORT
            ).show()
        }



    }
}
