package com.radionix.doorlock.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.radionix.doorlock.R
import com.radionix.doorlock.adapter.ProfileDevicesAdapter
import com.radionix.doorlock.data.Devices
import com.radionix.doorlock.databinding.ActivityProfileBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.pattern.InputPasswordActivity

class ProfileActivity : AppCompatActivity() {

    lateinit var appController : AppController
    lateinit var databaseReference: DatabaseReference
    lateinit var deviceList : ArrayList<Devices>

    lateinit var binding: ActivityProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this,R.layout.activity_profile)

        binding.lifecycleOwner = this
        binding.executePendingBindings()

        deviceList = ArrayList()
        appController = AppController(this)
        databaseReference = FirebaseDatabase.getInstance().getReference("")


        binding.pattern.setOnClickListener {

            startActivity(Intent(this,InputPasswordActivity::class.java)
                .putExtra("reqCode",100))

        }

        binding.logout.setOnClickListener {
            val appController = AppController(this)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure want to logout?")
            builder.setIcon(R.drawable.ic_question)

            builder.setPositiveButton("Yes"){ dialog, _ ->

                appController.putEmail("")
                appController.putPass("")

                Intent(this,LoginActivity::class.java).also {
                   startActivity(it)
                    val activity = this as Activity
                    activity.finish()
                }
                dialog.dismiss()

            }

            builder.setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }



            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        }


        getServerData()
    }

    private fun getServerData() {

        binding.progressBar.visibility = View.VISIBLE

        binding.nameValue.text  = appController.getUsername()
        binding.emailValue.text = appController.getEmail()
        binding.mobileValue.text = appController.getMobile()
        binding.userValue.text = appController.getUsername()

        databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername()).child("deviceList").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                binding.progressBar.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                for(data in p0.children){
                    deviceList.add(Devices(data.value.toString(),data.key.toString()))
                }
                val adapter = ProfileDevicesAdapter(deviceList,this@ProfileActivity)
                binding.deviceList.layoutManager = LinearLayoutManager(this@ProfileActivity)
                binding.deviceList.adapter = adapter

            }

        })



    }
}