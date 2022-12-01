package com.radionix.doorlock.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.radionix.doorlock.data.Devices
import com.radionix.doorlock.databinding.SlideItemContainerBinding

class DevicesAdapter(private val items: List<Devices> ,var listener : DevicesListener) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SlideItemContainerBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.bind(items[position])

    }

    inner class ViewHolder(val binding: SlideItemContainerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Devices) {
            binding.data = item
            binding.listener = listener
        }
    }

    interface DevicesListener{
        fun onItemClicked(data : Devices)
    }
}