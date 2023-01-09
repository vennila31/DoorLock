package com.radionix.doorlock.profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import com.mukesh.OtpView

class OtpReceiver : BroadcastReceiver() {


    companion object{

        private lateinit var editText : OtpView

        fun setEditText(editText : OtpView){

            this.editText = editText

        }

    }



    override fun onReceive(context: Context?, intent: Intent?) {

        val messages : Array<out SmsMessage>? = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (sms in messages!!){

            val msg = sms.messageBody
            val otp = msg.split(": ")[0]

            editText.setText(otp)


        }

    }




}