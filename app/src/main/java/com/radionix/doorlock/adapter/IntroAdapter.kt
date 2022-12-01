package com.radionix.doorlock.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.radionix.doorlock.R

class IntroAdapter(var context: Context) : PagerAdapter() {

    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val GalImages = intArrayOf(
        R.drawable.dev_config1,
        R.drawable.dev_config2,
        R.drawable.dev_config3,
        R.drawable.dev_config4,
        R.drawable.dev_config5,
        R.drawable.dev_config6,
        R.drawable.dev_config7
    )


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object`) as LinearLayout
    }

    override fun getCount(): Int {
        return GalImages.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater.inflate(R.layout.pager_item, container, false)

        val imageView: ImageView = itemView.findViewById(R.id.imageView) as ImageView
        imageView.setImageResource(GalImages[position])

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}