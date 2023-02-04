package com.radionix.doorlock.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.pattern.CreatePasswordActivity
import com.radionix.doorlock.pattern.InputPasswordActivity
import com.radionix.doorlock.profile.LoginActivity

class ProductViewModel : ViewModel()  {


    var productName :String? = null
    var productCode : String? = null


    fun isOkProduct(view : Context)
    {
        val appController  = AppController(view)
        if (appController.getIsOkProduct()) {
            if(appController.getUsername() == "" || appController.getEmail() == "" || appController.getMobile() == "" )
            {
                Intent(view, LoginActivity::class.java).also {
                    view.startActivity(it)
                    val activity = view as Activity
                    activity.finish()
                }.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }else
            {

                val sharedPreferences = view.getSharedPreferences("Skelta",
                    AppCompatActivity.MODE_PRIVATE
                )
                val password = sharedPreferences.getString("password", "0")
                if (password == "0") {
                    val intent = Intent(view, CreatePasswordActivity::class.java)
                    view.startActivity(intent)
                    val activity = view as Activity
                    activity.finish()
                }
                else {
                    val intent = Intent(view, InputPasswordActivity::class.java)
                    view.startActivity(intent)
                    val activity = view as Activity
                    activity.finish()
                }

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
                Intent(view.context, LoginActivity::class.java).also {
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
