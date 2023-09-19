package net.knowfx.yaodonghui.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.knowfx.yaodonghui.R
import net.knowfx.yaodonghui.base.BaseActivity
import net.knowfx.yaodonghui.databinding.ActivityImageBinding
import net.knowfx.yaodonghui.entities.ImageData

class Imagectivity : BaseActivity(), ViewPager.OnPageChangeListener {
 lateinit var mBinding: ActivityImageBinding
    lateinit var  imageList: List<ImageData>
    override fun getContentView(): View {
        mBinding = ActivityImageBinding.inflate(layoutInflater)
    return mBinding.root
    }

    override fun setData(savedInstanceState: Bundle?) {
        super.setData(savedInstanceState)
       mBinding.balc.setOnClickListener {
           finish()
       }
        val data = intent.getStringExtra("url")
        val listType = object : TypeToken<List<ImageData>>() {}.type
         imageList = Gson().fromJson(data, listType)
        var adapter = ImagePagerAdapter(this, imageList)
        mBinding.viewPager.setAdapter(adapter)
        adapter.notifyDataSetChanged()
        mBinding.tvText.setText("1"+"/"+imageList.size)
        mBinding.viewPager.addOnPageChangeListener(this)
    }
    class ImagePagerAdapter(
        private val context: Context,
        imageUrls: List<ImageData>
    ) :
        PagerAdapter() {
        private val imageUrls:  List<ImageData>
        init {
            this.imageUrls = imageUrls
        }

        override fun getCount(): Int {
            return imageUrls.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        @SuppressLint("MissingInflatedId")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.item_image, container, false)
            val imageView = view.findViewById<ImageView>(R.id.imageView)

            val path = when {
                imageUrls[position].picLocalPath.isNotEmpty() -> {
                    imageUrls[position].picLocalPath
                }
                imageUrls[position].picServicePath.isNotEmpty() -> {
                    imageUrls[position].picServicePath
                }
                else -> {
                    ""
                }
            }

            // 使用 Glide 加载图片
            Glide.with(context)
                .load(path)
                .into(imageView)
            container.addView(view)
            return view
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        val i = position + 1

        mBinding.tvText.setText(i.toString()+"/"+imageList.size)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}