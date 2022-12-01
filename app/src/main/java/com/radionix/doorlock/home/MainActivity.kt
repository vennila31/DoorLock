package com.radionix.doorlock.home


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.*
import com.radionix.doorlock.R
import com.radionix.doorlock.adapter.DevicesAdapter
import com.radionix.doorlock.addDevice.QRActivity
import com.radionix.doorlock.data.Devices
import com.radionix.doorlock.databinding.ActivityMainBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.product.DoorLockActivity
import com.radionix.doorlock.profile.ProfileActivity
import com.radionix.doorlock.service.MyService


class MainActivity : AppCompatActivity() , DevicesAdapter.DevicesListener {

    lateinit var adapter :DevicesAdapter
    lateinit var list: ArrayList<Devices>

    lateinit var binding: ActivityMainBinding

    lateinit var appController: AppController

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://radionix-design-gateways-default-rtdb.firebaseio.com/").getReference("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding  =
            DataBindingUtil.setContentView(this,R.layout.activity_main)

        val mainViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        appController = AppController(this)

        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        binding.executePendingBindings()

        list = ArrayList()

        binding.welcome.text = "Welcome ${appController.getUsername()},"

        if(mainViewModel.isOnline(this))
        {
            getDevices()
        }else
        {
            binding.spinKit.visibility = View.GONE
            binding.offline.visibility = View.VISIBLE
        }


        binding.addNew.setOnClickListener {
            startActivity(Intent(this,QRActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, MyService::class.java)
        serviceIntent.putExtra("inputExtra", "Tap for more information or to stop background")
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun getDevices()
    {
        val appController = AppController(this)
        databaseReference.child("userData").child(appController.getUsername()).child("deviceList").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                binding.spinKit.visibility = View.GONE
            }
            override fun onDataChange(p0: DataSnapshot) {
                for(data in p0.children){

                    list.add(Devices(data.value.toString(),data.key.toString()))

                }

                binding.spinKit.visibility = View.GONE
                binding.noDevices.visibility = View.GONE

                adapter = DevicesAdapter(list,this@MainActivity)
                binding.recyclerview.adapter = adapter

                if(list.size == 0){
                    binding.noDevices.visibility = View.VISIBLE
                }
            }
        })



    }

    override fun onItemClicked(data: Devices) {
        startActivity(Intent(this,DoorLockActivity::class.java)
            .putExtra("devId",data.id)
            .putExtra("devName",data.name))

    }
}