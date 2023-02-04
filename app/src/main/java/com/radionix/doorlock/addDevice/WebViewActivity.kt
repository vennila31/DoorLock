package com.radionix.doorlock.addDevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.radionix.doorlock.R
import com.radionix.doorlock.ShutterLockActivity
import com.radionix.doorlock.databinding.ActivityWebViewBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.home.MainViewModel
import com.radionix.doorlock.product.DoorLockActivity

class WebViewActivity : AppCompatActivity() {
    lateinit var binding : ActivityWebViewBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    var deviceName = ""

    var progressBar: ProgressBar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_view)


        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.executePendingBindings()


        progressBar = findViewById(R.id.progressBar)

        val reqCode = intent.getIntExtra("requestCode",0)
        deviceName = intent.getStringExtra("dev_name").toString()


        if(reqCode == 100 || reqCode == 101)
        {
            binding.ok.text = "Done"
        }

        binding.webView.webViewClient = myWebClient()
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.loadUrl("http://192.168.4.1/")

        binding.ok.setOnClickListener {

            if(reqCode == 100 || reqCode == 101)
            {
                finish()
            }
            else
            {
                val appController = AppController(this)
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("")

                databaseReference.child(deviceName).child("deviceName").setValue("Door Lock")
                databaseReference.child("userData").child(appController.getUid()).child(appController.getUsername())
                    .child("deviceList")
                    .child(deviceName)
                    .setValue("Main Door")

                if(deviceName.contains("RDXSHL"))
                {
                    startActivity(
                        Intent(this, ShutterLockActivity::class.java)
                            .putExtra("devId", deviceName)
                            .putExtra("devName","Main Door"))
                }else{
                    startActivity(
                        Intent(this, DoorLockActivity::class.java)
                            .putExtra("devId", deviceName)
                            .putExtra("devName","Main Door"))
                }


            }

        }

    }


    inner class myWebClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            progressBar!!.visibility = View.VISIBLE
            view.loadUrl(url)
            return true

        }

        override fun onPageFinished(view: WebView, url: String) {

            super.onPageFinished(view, url)
            progressBar!!.visibility = View.GONE
        }
    }




}