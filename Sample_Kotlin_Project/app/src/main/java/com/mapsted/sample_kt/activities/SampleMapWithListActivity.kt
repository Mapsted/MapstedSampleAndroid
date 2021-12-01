package com.mapsted.sample_kt.activities

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.network.property_metadata.model.Category
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMainBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui_components.list.MapstedListAdapter
import com.mapsted.ui_components.list.MapstedListAdapter.MapstedListViewHolder

class SampleMapWithListActivity : AppCompatActivity(), MapstedMapUiApiProvider {

    private val TAG = SampleMapWithListActivity::class.java.simpleName
    private lateinit var mapUiApi: MapUiApi
    private var context: Context? = null
    private lateinit var mBinding: ActivitySampleMainBinding
    private val myTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this.applicationContext
        mapUiApi = MapstedSdkController.newInstance(context!!)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_main)
        setupMapstedSdk()
    }

    override fun provideMapstedUiApi(): MapUiApi {
        return mapUiApi
    }

    override fun onStop() {
        mapUiApi.removeViewFromMap(myTag)
        super.onStop()
    }

    override fun onDestroy() {
        mapUiApi.onDestroy()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mapUiApi.onConfigurationChanged(this, newConfig)
    }

    private fun setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk")

        // TODO: This needs to be in the app itself not in app template...

        // TODO: This needs to be in the app itself not in app template...
        CustomParams.newBuilder()
            .setBaseMapStyle(BaseMapStyle.DARK)
            .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
            .setMapZoomRange(MapstedMapRange(6.0f, 24.0f))
            .build()

        mapUiApi.initializeMapstedSDK(
            this,
            mBinding.myMapUiTool,
            mBinding.myMapContainer,
            object : MapstedInitCallback {
                override fun onCoreInitialized() {
                    Log.i(TAG, "::setupMapstedSdk ::onCoreInitialized")
                }

                override fun onMapInitialized() {
                    Log.i(TAG, "::setupMapstedSdk ::onMapInitialized")
                }

                override fun onSuccess() {
                    Log.i(TAG, "::setupMapstedSdk ::onSuccess")
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.e(
                        TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage
                    )
                }
            })
    }


    private val listener: MapstedListAdapter.Listener<Category> =
        object : MapstedListAdapter.Listener<Category> {
            override fun onItemClicked(position: Int, view: View?, item: Category?) {
                //                val message = "item clicked position=" + position + ", item=" + item.getName()
//                /*Log.d(TAG, message);*/
//                Snackbar.make(mBinding.rootView, message, Snackbar.LENGTH_SHORT).show()
            }

            override fun getMapstedListViewHolder(
                parent: ViewGroup?,
                viewType: Int
            ): MapstedListViewHolder<Category> {
                val itemLayout = LayoutInflater.from(context)
                    .inflate(R.layout.sample_category_item_layout, null, false)
                val mapstedListViewHolder: MapstedListViewHolder<Category> =
                    object : MapstedListViewHolder<Category>(itemLayout) {
                        override fun bind(item: Category?) {
                            if (item == null) return
                            val textView = itemView.findViewById<TextView>(R.id.tv_parent_category)
                            textView.text = item.getName()
                            val imageView =
                                itemView.findViewById<ImageView>(R.id.iv_parent_category)
                            setParentCategory(item, imageView)
                            //Glide.with(getApplicationContext()).load(item.getImageGuid()).into(imageView);

                        }
                    }

                return mapstedListViewHolder
            }
        }

    fun setParentCategory(parentCategory: Category, ivCategoryImage: ImageView) {
        val target: CustomViewTarget<ImageView, Drawable> =
            object : CustomViewTarget<ImageView, Drawable>(ivCategoryImage) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    fetchColorFromResource(resource)
                    ivCategoryImage.setImageDrawable(resource)
                }

                override fun onResourceCleared(placeholder: Drawable?) {

                }
            }
        val imageUrl = "http://www.example.com/" + parentCategory.imageGuid
        Log.d(TAG, "setParentCategory: imageUrl=$imageUrl")
        Glide.with(ivCategoryImage)
            .load(imageUrl)
            .placeholder(R.drawable.ic_category_shopping_active)
            .transform(MultiTransformation(RoundedCorners(8), FitCenter()))
            .into(target)
    }

    private fun fetchColorFromResource(drawable: Drawable) {
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val palette = Palette.from(bitmap).generate()
            val vibrantSwatch = palette.dominantSwatch
            if (vibrantSwatch != null) {
                val rgbInt = vibrantSwatch.rgb
                val red = Color.red(rgbInt)
                val green = Color.green(rgbInt)
                val blue = Color.blue(rgbInt)
            }
        }
    }

}