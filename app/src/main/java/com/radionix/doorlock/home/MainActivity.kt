package com.radionix.doorlock.home


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.database.*
import com.radionix.doorlock.R
import com.radionix.doorlock.ShutterLockActivity
import com.radionix.doorlock.adapter.DevicesAdapter
import com.radionix.doorlock.addDevice.QRActivity
import com.radionix.doorlock.data.Devices
import com.radionix.doorlock.databinding.ActivityMainBinding
import com.radionix.doorlock.endlessservice.Actions
import com.radionix.doorlock.endlessservice.EndlessService
import com.radionix.doorlock.endlessservice.ServiceState
import com.radionix.doorlock.endlessservice.getServiceState
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.product.DoorLockActivity
import com.radionix.doorlock.profile.ProfileActivity
import com.radionix.doorlock.service.MyService
import com.radionix.doorlock.service.MyWork
import java.util.concurrent.TimeUnit


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
            startService(Actions.START)
    }

  /*  override fun onStart() {
        super.onStart()

    *//*    val uploadWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<WorkerClass>().build()
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)*//*



    }*/

    private fun startService(action : Actions){
     /*   val serviceIntent = Intent(this, MyService::class.java)
        serviceIntent.putExtra("inputExtra", "Tap for more information or to stop background")
        ContextCompat.startForegroundService(this, serviceIntent)
*/

        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
                return
            }
            startService(it)
        }
    }



    private fun getDevices()
    {
        list.clear()

        val appController = AppController(this)
        databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername()).child("deviceList").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                binding.spinKit.visibility = View.GONE
            }
            override fun onDataChange(p0: DataSnapshot) {

                list.clear()

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

    override fun onResume() {
        super.onResume()
        getDevices()
    }

    override fun onItemClicked(data: Devices) {
        if(data.id.contains("RDXSHL"))
        {
            startActivity(Intent(this,ShutterLockActivity::class.java)
                .putExtra("devId",data.id)
                .putExtra("devName",data.name))
        }else{
            startActivity(Intent(this,DoorLockActivity::class.java)
                .putExtra("devId",data.id)
                .putExtra("devName",data.name))
        }


    }
}