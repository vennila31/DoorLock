package com.radionix.doorlock.product

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.firebase.database.*
import com.radionix.doorlock.R
import com.radionix.doorlock.addDevice.WebViewActivity
import com.radionix.doorlock.helper.AppController

class DeviceSettingsActivity  : AppCompatActivity() {

    var deviceName = ""
    lateinit var databaseReference: DatabaseReference

    lateinit var doorName : AppCompatEditText
    lateinit var doorStatus : AppCompatTextView
    lateinit var doorLabel1 : AppCompatTextView
    lateinit var doorLabel : AppCompatTextView
    lateinit var doorLabel0 : AppCompatTextView
    lateinit var ssid : AppCompatTextView
    lateinit var ip : AppCompatTextView
    lateinit var reset : AppCompatImageView
    lateinit var wifi : AppCompatImageView
    lateinit var spinKit : ProgressBar

    lateinit var back : AppCompatImageView

    lateinit var save : AppCompatButton

    var reqCode = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_settings)

        deviceName = intent.getStringExtra("devId").toString()
        databaseReference = FirebaseDatabase.getInstance().getReference("")

        reqCode = intent.getIntExtra("reqCode",0)

        doorStatus = findViewById(R.id.density_value)
        doorName = findViewById(R.id.tank_value)
        doorLabel = findViewById(R.id.tank_density)
        doorLabel0 = findViewById(R.id.info)
        doorLabel1 = findViewById(R.id.tank_name)
        ssid = findViewById(R.id.ssid)
        ip = findViewById(R.id.ip)
        reset = findViewById(R.id.reset)
        wifi = findViewById(R.id.network_credentials)
        spinKit = findViewById(R.id.spin_kit)

        back = findViewById(R.id.back)
        save = findViewById(R.id.save)

        initializeView()

        if(reqCode == 100)
        {
            doorStatus.visibility = View.GONE
            doorLabel.visibility = View.GONE
            doorLabel0.text = "Shutter Info"
            doorLabel1.text = "Shutter Name"
        }

        back.setOnClickListener {
            onBackPressed()
        }

        reset.setOnClickListener {
            reset.visibility = View.GONE
            spinKit.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({

                reset.setImageResource(R.drawable.ic_check_green)
                reset.visibility = View.VISIBLE
                spinKit.visibility = View.GONE

                databaseReference.child(deviceName).child("devicereset").setValue("1")

            }, 10000)

        }

        wifi.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java)
                    .putExtra("requestCode",101))
        }

        save.setOnClickListener {
            saveToServer()
        }

    }

    private fun saveToServer() {

        val appController = AppController(this)
        databaseReference.child(deviceName).child("deviceName").setValue(doorName.text.toString())
        databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername())
            .child("deviceList").child(deviceName).setValue(doorName.text.toString())
        showSuccess()

    }

    private fun showSuccess() {
        Toast.makeText(this,"Updated successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initializeView() {
        databaseReference.child(deviceName).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(data: DataSnapshot) {

                var ssidVal = ""
                var pass = ""

                if(data.child("status").value != null)
                {
                    doorStatus.text = data.child("status").value.toString()

                }

                if(data.child("ssid").value != null)
                {
                    ssidVal = data.child("ssid").value.toString()
                }

                if(data.child("password").value != null)
                {
                    pass = data.child("password").value.toString()
                }

                if(data.child("deviceName").value != null)
                {
                    doorName.setText(data.child("deviceName").value.toString())
                }

                ssid.text = ssidVal
                ip.text = pass

            }


        })
    }

}