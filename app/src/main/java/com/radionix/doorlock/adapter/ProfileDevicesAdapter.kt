package com.radionix.doorlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.radionix.doorlock.data.Devices
import com.radionix.doorlock.databinding.ProfileDeviceListBinding

class ProfileDevicesAdapter(private val deviceList : ArrayList<Devices>, var context : Context) :
    RecyclerView.Adapter<ProfileDevicesAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProfileDeviceListBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(deviceList[position])

    inner class ViewHolder(val binding: ProfileDeviceListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Devices) {
            binding.data = item
            binding.executePendingBindings()
        }
    }



}