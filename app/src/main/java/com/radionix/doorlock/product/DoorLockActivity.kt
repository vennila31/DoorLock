package com.radionix.doorlock.product

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
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
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityDoorLockBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.profile.LoginActivity
import java.text.SimpleDateFormat
import java.util.*


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
        toolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.white),PorterDuff.Mode.SRC_ATOP)

        databaseReference = FirebaseDatabase.getInstance().getReference("")

        binding.back.setOnClickListener {
            onBackPressed()
        }

        devName = intent.getStringExtra("devName").toString()
        devId = intent.getStringExtra("devId").toString()


        binding.lockBtn.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked)
            {
                vibrate()
                binding.lockStatus.text = resources.getText(R.string.door_locked)
                binding.lockStatus.setTextColor(resources.getColor(R.color.ap_color))
                binding.lockImg.setImageResource(R.drawable.ic_minus_green)
                databaseReference.child(devId).child("relay").setValue("*1516#")
                databaseReference.child(devId).child("status").setValue("Unlocked")

                val handler = Handler()
                handler.postDelayed({
                    binding.lockStatus.text = resources.getText(R.string.door_unlocked)
                    binding.lockStatus.setTextColor(resources.getColor(R.color.editText))
                    binding.lockImg.setImageResource(R.drawable.ic_minus_red)
                    binding.lockBtn.isChecked = false
                    databaseReference.child(devId).child("status").setValue("Locked")
                }, 5000)



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

        binding.intruderBtn.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                databaseReference.child(devId).child("buzz").setValue("1")
            }else{
                databaseReference.child(devId).child("buzz").setValue("0")
            }
        }

        getWifi()


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





    }

    override fun onResume() {
        super.onResume()
        getWifi()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setTimer() {

        vibrate()
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

        val appController = AppController(this)
        databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername())
            .child("deviceList").child(devId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    binding.devName.text = snapshot.value.toString()

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        databaseReference.child(devId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var wifi = 0

                if(snapshot.child("ontime1").value != null && snapshot.child("offtime1").value != null && snapshot.child("timmer").value.toString() == "T"  )
                {
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.timerValue.text = "Timer : ${snapshot.child("ontime1").value} to ${snapshot.child("offtime1").value}"
                }

                if(snapshot.child("reed").value != null){6
                    binding.sos.isChecked = snapshot.child("reed").value.toString() == "1"
                }

                if(snapshot.child("vib").value != null){
                    binding.vib.isChecked = snapshot.child("vib").value.toString() == "1"
                }

                if(snapshot.child("buzz").value != null){
                    binding.intruderBtn.isChecked = snapshot.child("buzz").value.toString() == "1"
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