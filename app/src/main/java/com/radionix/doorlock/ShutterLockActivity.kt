package com.radionix.doorlock

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import com.radionix.doorlock.databinding.ActivityShutterLockBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.product.DeleteDialog
import com.radionix.doorlock.product.DeviceSettingsActivity
import java.text.SimpleDateFormat
import java.util.*

class ShutterLockActivity : AppCompatActivity() {

    lateinit var binding : ActivityShutterLockBinding

    var devName : String = ""
    var devId : String = ""

    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shutter_lock)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Device Manager"
        toolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)

        databaseReference = FirebaseDatabase.getInstance().getReference("")

        binding.back.setOnClickListener {
            onBackPressed()
        }

        devName = intent.getStringExtra("devName").toString()
        devId = intent.getStringExtra("devId").toString()



        binding.sosBtn.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
            {
                databaseReference.child(devId).child("reed").setValue("1")
            }else{
                databaseReference.child(devId).child("reed").setValue("0")
            }
        }

        binding.introBtn.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                databaseReference.child(devId).child("buzz").setValue("1")
            }else{
                databaseReference.child(devId).child("buzz").setValue("0")
            }
        }

        binding.timer.setOnClickListener {
            setTimer()
        }


        binding.deleteTimer.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure want to delete timer?")
            builder.setIcon(R.drawable.ic_question)

            builder.setPositiveButton("Yes"){ dialog, _ ->
                binding.linearLayout.visibility = View.GONE
                databaseReference.child(devId).child("timmer").setValue("0")
                dialog.dismiss()

            }

            builder.setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }



            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()

        }



        getWifi()

    }

    @SuppressLint("SimpleDateFormat")
    private fun setTimer() {

        val li = LayoutInflater.from(this)
        val promptsView: View = li.inflate(R.layout.alert_timer_dialog, null)
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(promptsView)


        val setTimeOn = promptsView.findViewById<AppCompatButton>(R.id.setTimerOn)
        val setTimeOff = promptsView.findViewById<AppCompatButton>(R.id.setTimerOff)


        setTimeOff.setOnClickListener {
            val mCurrentTime: Calendar = Calendar.getInstance()
            var hour: Int = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute: Int = mCurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    hour = selectedHour
                    minute = selectedMinute

                    mCurrentTime.set(0, 0, 0, selectedHour, selectedMinute)

                    setTimeOff.text = DateFormat.format("hh:mm a", mCurrentTime)

                },
                12,
                0,
                false
            )

            mTimePicker.updateTime(hour, minute)
            mTimePicker.show()
        }

        setTimeOn.setOnClickListener {
            val mCurrentTime: Calendar = Calendar.getInstance()
            var hour: Int = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute: Int = mCurrentTime.get(Calendar.MINUTE)
            val mTimePicker = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->

                    hour = selectedHour
                    minute = selectedMinute
                    mCurrentTime.set(0, 0, 0, selectedHour, selectedMinute)

                    setTimeOn.text = DateFormat.format("hh:mm a", mCurrentTime)

                },
                12,
                0,
                false
            )

            mTimePicker.updateTime(hour, minute)
            mTimePicker.show()
        }


        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(
                "Save"
            ) { dialog, _ ->

                if (setTimeOn.text.toString() != "" && setTimeOff.text.toString() != "") {
                    val displayFormat = SimpleDateFormat("HH:mm")
                    val parseFormat = SimpleDateFormat("hh:mm a")
                    val date1 = parseFormat.parse(setTimeOn.text.toString())
                    val date2 = parseFormat.parse(setTimeOff.text.toString())

                    val onTime = displayFormat.format(date1!!)
                    val offTime = displayFormat.format(date2!!)



                    binding.linearLayout.visibility = View.VISIBLE
                    binding.timerValue.text = "Timer : $onTime to $offTime"
                    databaseReference.child(devId).child("ontime1").setValue(onTime)
                    databaseReference.child(devId).child("offtime1").setValue(offTime)
                    databaseReference.child(devId).child("timmer").setValue("T")

                    dialog.dismiss()
                } else {
                    Toast.makeText(this,"Time is not selected", Toast.LENGTH_SHORT).show()
                }


            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.dismiss() }


        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()


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
                    Intent(this, DeviceSettingsActivity::class.java)
                        .putExtra("devId",devId)
                        .putExtra("reqCode",100))

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

    override fun onResume() {
        super.onResume()
        getWifi()
    }

    private fun getWifi() {

        val appController = AppController(this)
        databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername())
            .child("deviceList").child(devId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    binding.devName.text = snapshot.value.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        databaseReference.child(devId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var wifi = 0

                if(snapshot.child("ontime1").value != null && snapshot.child("offtime1").value != null && snapshot.child("timmer").value.toString() == "T"  )
                {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.timerValue.text = "Timer : ${snapshot.child("ontime1").value} to ${snapshot.child("offtime1").value}"
                }

                if(snapshot.child("reed").value != null){6
                    binding.sosBtn.isChecked = snapshot.child("reed").value.toString() == "1"
                }


                if(snapshot.child("buzz").value != null){
                    binding.introBtn.isChecked = snapshot.child("buzz").value.toString() == "1"
                }

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