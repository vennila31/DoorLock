package com.radionix.doorlock.helper

import android.content.Context
import android.content.SharedPreferences

class AppController(context : Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("skelta",
        Context.MODE_PRIVATE)
    private val editor : SharedPreferences.Editor = sharedPreferences.edit()

    fun putUid(uid : String){
        sharedPreferences.edit()
            .putString("uid",uid)
            .apply()
    }

    fun getUid(): String{

        return sharedPreferences.getString("uid","").toString()
    }
    fun putIsOkProduct(yes : Boolean){

        sharedPreferences.edit()
            .putBoolean("okproduct",yes)
            .apply()

    }

    fun getIsOkProduct() : Boolean{

        return sharedPreferences.getBoolean("okproduct",false)
    }

    fun putUsername(user : String){
        editor.putString("username",user)
        editor.apply()
        editor.commit()
    }

    fun getUsername() : String{

        return sharedPreferences.getString("username","").toString()
    }

    fun putEmail(email : String){
        editor.putString("email",email)
        editor.apply()
        editor.commit()
    }

    fun getEmail() : String{

        return sharedPreferences.getString("email","").toString()
    }
    fun putMobile(mobile : String){
        editor.putString("mobile",mobile)
        editor.apply()
        editor.commit()
    }

    fun getMobile() : String{

        return sharedPreferences.getString("mobile","").toString()
    }

    fun putPass(mobile : String){
        editor.putString("password",mobile)
        editor.apply()
        editor.commit()
    }

    fun getPass() : String{

        return sharedPreferences.getString("password","").toString()
    }

}