package com.radionix.doorlock.product

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityDoorLockBinding


class DoorLockActivity : AppCompatActivity() {

    lateinit var binding: ActivityDoorLockBinding

    var devName : String = ""
    var devId : String = ""

    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_door_lock)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Device Manager"

        databaseReference = FirebaseDatabase.getInstance().getReference("")

        binding.back.setOnClickListener {
            onBackPressed()
        }

        devName = intent.getStringExtra("devName").toString()
        devId = intent.getStringExtra("devId").toString()

        binding.devName.text = devName

        binding.lockBtn.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
            {
                vibrate()
                binding.lockStatus.text = resources.getText(R.string.door_locked)
                binding.lockStatus.setTextColor(resources.getColor(R.color.green))
                binding.lockImg.setImageResource(R.drawable.ic_minus_green)
                databaseReference.child(devId).child("relay").setValue("*1516#")
                val handler = Handler()
                handler.postDelayed({
                    binding.lockStatus.text = resources.getText(R.string.door_unlocked)
                    binding.lockStatus.setTextColor(resources.getColor(R.color.red))
                    binding.lockImg.setImageResource(R.drawable.ic_minus_red)
                    binding.lockBtn.isChecked = false
                }, 3000)



            }


        }


        binding.sos.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
            {
                databaseReference.child(devId).child("reed").setValue("1")
            }else{
                databaseReference.child(devId).child("reed").setValue("0")
            }
        }

        binding.vib.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
            {
                databaseReference.child(devId).child("vib").setValue("1")
            }else{
                databaseReference.child(devId).child("vib").setValue("0")
            }
        }

        getWifi()



    }

    @SuppressLint("MissingPermission")
    private fun vibrate() {
        val vb: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vb.vibrate(100)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.device_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.refresh -> {
                finish()
                overridePendingTransition( 0, 0)
                startActivity(intent)
                overridePendingTransition( 0, 0)

            }
            R.id.settings -> {

                startActivity(
                    Intent(this,DeviceSettingsActivity::class.java)
                        .putExtra("devId",devId))

            }
            R.id.delete -> {

                DeleteDialog(devId).show(supportFragmentManager,"DeleteFragment")

            }
            else -> {
                onBackPressed()
            }
        }


        return super.onOptionsItemSelected(item)
    }



    private fun getWifi() {

        databaseReference.child(devId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var wifi = 0

           /*     if(snapshot.child("relay").value != null)
                {
                    if(snapshot.child("relay").value.toString() == "")
                    {
                        binding.lockStatus.text = resources.getText(R.string.door_unlocked)
                        binding.lockStatus.setTextColor(resources.getColor(R.color.red))
                        binding.lockImg.setImageResource(R.drawable.ic_minus_red)
                        binding.lockBtn.isChecked = false
                    }
                    else
                    {
                        binding.lockStatus.text = resources.getText(R.string.door_locked)
                        binding.lockStatus.setTextColor(resources.getColor(R.color.green))
                        binding.lockImg.setImageResource(R.drawable.ic_minus_green)
                        binding.lockBtn.isChecked = true
                    }
                }*/

                if(snapshot.child("wifirange").value != null)
                {
                    wifi = snapshot.child("wifirange").value.toString().toInt()

                    when {

                        wifi == 0 -> {
                            binding.wifi.setImageResource(R.drawable.zero_signal)
                        }
                        wifi < 20 -> {
                            binding.wifi.setImageResource(R.drawable.one_signal)
                        }
                        wifi < 40 -> {
                            binding.wifi.setImageResource(R.drawable.two_signal)
                        }
                        wifi < 60 -> {
                            binding.wifi.setImageResource(R.drawable.three_signal)
                        }
                        wifi < 80 -> {
                            binding.wifi.setImageResource(R.drawable.four_signal)
                        }
                        wifi > 80 -> {
                            binding.wifi.setImageResource(R.drawable.five_signal)
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}