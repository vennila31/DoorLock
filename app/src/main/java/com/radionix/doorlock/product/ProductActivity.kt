package com.radionix.doorlock.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.radionix.doorlock.R
import com.radionix.doorlock.databinding.ActivityProductBinding

class ProductActivity : AppCompatActivity() {


    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityProductBinding =
            DataBindingUtil.setContentView(this,R.layout.activity_product)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.executePendingBindings()

        viewModel.isOkProduct(this)

    }
}