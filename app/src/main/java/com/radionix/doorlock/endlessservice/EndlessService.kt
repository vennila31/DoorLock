package com.radionix.doorlock.endlessservice

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
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radionix.doorlock.R
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.home.MainActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class EndlessService : Service() {

    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationManager: NotificationManager
    lateinit var builder: Notification.Builder
    private val channelId = "12345"

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Log.d("Hello","Hello")
            }
        } else {
            Log.d("Hello","Hello")
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Hello","Hello")
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Hello","Hello")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startService() {
        if (isServiceStarted) return
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)
        alert()
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    pingFakeServer()
                }
                delay(1 * 60 * 1000)
            }
        }
    }

    private fun stopService() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.d("Hello","Hello")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun pingFakeServer() {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ")
        val gmtTime = df.format(Date())

        val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)

        val json =
            """
                {
                    "deviceId": "$deviceId",
                    "createdAt": "$gmtTime"
                }
            """
        try {
            Fuel.post("https://jsonplaceholder.typicode.com/posts")
                .jsonBody(json)
                .response { _, _, result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        Log.d("[response bytes]" , String(bytes))
                    } else {
                        Log.d("[response error]", error?.message.toString())
                    }
                }
        } catch (e: Exception) {
            Log.d("Hello","Hello")
        }
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_LOW
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("Skelta")
            .setContentText("Monitoring...")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_skelta)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
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
        databaseReference.child(devId).addValueEventListener(object : ValueEventListener {
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
                .setSound(
                    Uri.parse("android.resource://"
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
                .setSound(
                    Uri.parse("android.resource://"
                        + applicationContext.packageName + "/"
                        + R.raw.sound1))


            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, builder.build())
        }
    }


}