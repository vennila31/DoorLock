package com.radionix.doorlock.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
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

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent!!.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, MyApp.CHANNEL_ID)
            .setContentTitle("Skelta")
            .setContentText(input)
            .setSmallIcon(R.mipmap.ic_skelta)
            .setContentIntent(pendingIntent)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MyApp.CHANNEL_ID,
                MyApp.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(this, MyApp.CHANNEL_ID)
        }
        startForeground(1, notification)

        // we need this lock so our service gets not affected by Doze Mode
/*        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire(10*60*1000L *//*10 minutes*//*)
                }
            }*/

        alert()

        return START_STICKY
    }

    private fun alert() {

         val appController = AppController(this)
         val databaseReference = FirebaseDatabase.getInstance().getReference("")

        databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername()).addValueEventListener(object :
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

                var rAlert = 0
                var vAlert = 0
                var sosAlert = 0
                if(snapshot.child("r-alert").value != null ){
                    rAlert = snapshot.child("r-alert").value.toString().toInt()
                }
                if(snapshot.child("v-alert").value != null){
                    vAlert = snapshot.child("v-alert").value.toString().toInt()
                }

                if(snapshot.child("s-alert").value != null){
                    sosAlert = snapshot.child("s-alert").value.toString().toInt()
                }

                    if(rAlert == 1 && vAlert == 1)
                    {
                        val rawPathUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sound1);
                        val r = RingtoneManager.getRingtone(applicationContext, rawPathUri)
                        r.play()
                        notify("Vibration Detected and Contact sensor broken" , devName)

                    }else if(rAlert == 1){
                        val rawPathUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sound2);
                        val r = RingtoneManager.getRingtone(applicationContext, rawPathUri)
                        r.play()
                        notify("Contact sensor broken",devName)

                    }else if(vAlert == 1)
                    {
                        val rawPathUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sound1);
                        val r = RingtoneManager.getRingtone(applicationContext, rawPathUri)
                        r.play()
                        notify("Vibration Detected",devName)
                    }else if(sosAlert == 1)
                    {
                        val rawPathUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sound1);
                        val r = RingtoneManager.getRingtone(applicationContext, rawPathUri)
                        r.play()
                        notify("SOS Alert",devName)
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
                .setSmallIcon(R.drawable.ic_warning).setLargeIcon(
                    BitmapFactory.decodeResource(this.resources, R.drawable.ic_warning)
                ).setContentIntent(pendingIntent)
                .setSound(Uri.parse("android.resource://"
                    + applicationContext.packageName + "/"
                    + R.raw.sound1))
                .setColor(resources.getColor(R.color.red))


            notificationManager.notify(12345, builder.build())

        } else {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_warning)
                .setColor(resources.getColor(R.color.red))
                .setContentTitle(msg)
                .setContentText("Kindly check your $msg2")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Uri.parse("android.resource://"
                        + applicationContext.packageName + "/"
                        + R.raw.sound1))


            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, builder.build())
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}