package com.radionix.doorlock.addDevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.radionix.doorlock.R
import com.radionix.doorlock.adapter.IntroAdapter
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val wormDotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        val adapter = IntroAdapter(this)
        viewPager.adapter = adapter
        wormDotsIndicator.setViewPager(viewPager)

       /* val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Device Configuration"*/

        val next = findViewById<AppCompatButton>(R.id.next)

        val message = intent.getStringExtra("dev_name").toString()

        next.setOnClickListener {
            startActivity(
                Intent(this, WebViewActivity::class.java).putExtra("dev_name",message).addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }

   /* override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }*/
}