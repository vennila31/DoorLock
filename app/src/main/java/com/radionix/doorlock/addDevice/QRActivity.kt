package com.radionix.doorlock.addDevice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.database.*
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityQractivityBinding
import com.radionix.doorlock.helper.AppController
import com.radionix.doorlock.home.MainViewModel

class QRActivity : AppCompatActivity() {

    lateinit var binding: ActivityQractivityBinding
    private lateinit var codeScanner: CodeScanner

    var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("")

    private lateinit var appController : AppController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_qractivity)
        val viewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.executePendingBindings()

        codeScanner = CodeScanner(this@QRActivity, binding.scannerView)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        viewModel.checkCameraPermission(this)

        codeScanner.decodeCallback = DecodeCallback {


            runOnUiThread {

                checkProductCode(it.text)

            }

        }
        codeScanner.errorCallback = ErrorCallback {

        }

        binding.submit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.GONE
            checkProductCode(binding.enterQr.text.toString().trim())
        }

    }


    private fun checkProductCode(text : String)
    {
        when {
            text.isEmpty() -> {
                binding.progressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                Toast.makeText(applicationContext,"Enter valid product code", Toast.LENGTH_SHORT)
                    .show()
            }
            text.contains("RDX",ignoreCase = true) -> {
                if(isNetworkAvailable()){
                    checkDeviceServer(text)

                }else{
                    binding.progressBar.visibility = View.GONE
                    binding.submit.visibility = View.VISIBLE
                    Toast.makeText(applicationContext,"Turn on your internet connection", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else -> {
                binding.progressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                Toast.makeText(applicationContext,"Invalid product code", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null
    }



    private fun checkDeviceServer(device: String) {

        var isOk = false

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

                binding.progressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE

                Toast.makeText(applicationContext,"Error", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(data in p0.children){
                    if(device == data.key.toString()){
                        isOk = true
                        checkIsExistDevice(device)
                    }
                }
                if(!isOk){
                    binding.progressBar.visibility = View.GONE
                    binding.submit.visibility = View.VISIBLE
                    Toast.makeText(applicationContext,"Device not installed...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    fun checkIsExistDevice(text: String?) {

        var isExist = false

        appController = AppController(this)

        databaseReference.child("userData")
            .child(appController.getUid())
            .child(appController.getUsername())
            .child("deviceList")
            .addListenerForSingleValueEvent(   object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                binding.submit.visibility = View.VISIBLE
                Toast.makeText(applicationContext,"Data not found", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onDataChange(data: DataSnapshot) {

                for(i in data.children){
                    if(i.key.toString() == text){
                        isExist = true
                        binding.progressBar.visibility = View.GONE
                        binding.submit.visibility = View.VISIBLE
                        Toast.makeText(applicationContext,"Device already added.", Toast.LENGTH_SHORT)
                            .show()
                        break
                    }
                }

                if(!isExist){
                    binding.progressBar.visibility = View.GONE
                    binding.submit.visibility = View.VISIBLE
                    onSuccess(text!!)
                }
            }


        })
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    fun onSuccess(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your device is ready to configure")
        builder.setIcon(R.drawable.ic_check_green)


        builder.setPositiveButton("OK"){ dialog, _ ->

            startActivity(
                Intent(this, IntroActivity::class.java).putExtra("dev_name",message).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
            dialog.dismiss()

        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}