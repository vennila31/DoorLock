package com.radionix.doorlock.product

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.radionix.doorlock.home.MainActivity
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityScannerBinding
import com.radionix.doorlock.helper.AppController

class ScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner : CodeScanner
    lateinit var appController: AppController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityScannerBinding =
            DataBindingUtil.setContentView(this,R.layout.activity_scanner)
        binding.lifecycleOwner = this

        codeScanner  =  CodeScanner(this , binding.codeScan )

        appController = AppController(this)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        checkCameraPermission()

        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {

                when (it.text) {
                    null -> {
                        Toast.makeText(
                            applicationContext, "Result not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        checkDevice(it.text.toString())
                    }
                }

            }

        }
        codeScanner.errorCallback = ErrorCallback {
            val returnIntent = Intent()
            returnIntent.putExtra("result", "hello from scanner")
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        binding.codeScan.setOnClickListener {

            codeScanner.startPreview()

        }



    }

    private fun checkDevice(device : String) {
        if(device == "Rdx2702"){
            appController.putIsOkProduct(true)
            startActivity(Intent(this@ScannerActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }else{
            Toast.makeText(
                applicationContext, "Incorrect product ID...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA), 101)
        }
        else{
            return
        }

    }
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,ProductActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}