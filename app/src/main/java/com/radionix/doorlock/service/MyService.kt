package com.radionix.doorlock.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radionix.doorlock.R
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.home.MainActivity


class MyService : Service() {

    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationManager: NotificationManager
    lateinit var builder: Notification.Builder
    private val channelId = "12345"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        onTaskRemoved(intent)

        alert()

        val input = intent!!.getStringExtra("inputExtra")
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle("Skelta is running")
            .setContentText(input)
            .setVibrate(null)
            .setSmallIcon(R.mipmap.ic_app_icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    private fun alert() {

         val appController = AppController(this)
         val databaseReference = FirebaseDatabase.getInstance().getReference("")

        databaseReference.child("userData").child(appController.getUsername()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.child("deviceList").children)
                {
                    alertDevice(data.key.toString() , data.value.toString())
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun alertDevice(devId: String , devName : String) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("")
        databaseReference.child(devId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.child("r-alert").value != null && snapshot.child("v-alert").value != null)
                {
                    val rAlert = snapshot.child("r-alert").value.toString().toInt()
                    val vAlert = snapshot.child("v-alert").value.toString().toInt()

                    if(rAlert == 1 && vAlert == 1)
                    {
                        notify("Vibration Detected and Contact sensor broken" , devName)

                    }else if(rAlert == 1){

                        notify("Contact sensor broken",devName)

                    }else if(vAlert == 1)
                    {
                        notify("Vibration Detected",devName)
                    }


                }



            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


    }

    private fun notify(msg : String , msg2 : String)
    {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, LauncherActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            notificationChannel = NotificationChannel(
                channelId,
                msg,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(this, channelId).setContentTitle(msg)
                .setContentText("Kindly check your $msg2")
                .setSmallIcon(R.mipmap.ic_app_icon).setLargeIcon(
                    BitmapFactory.decodeResource(this.resources, R.mipmap.ic_app_icon)
                ).setContentIntent(pendingIntent)

            notificationManager.notify(12345, builder.build())

        } else {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_app_icon)
                .setContentTitle(msg)
                .setContentText("Kindly check your $msg2")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)


            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, builder.build())

        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "ForegroundServiceChannel",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val serviceIntent = Intent(this, MyService::class.java)
       serviceIntent.putExtra("inputExtra", "Tap for more information or to stop background")
       ContextCompat.startForegroundService(this, serviceIntent)
        super.onTaskRemoved(rootIntent)
    }
}