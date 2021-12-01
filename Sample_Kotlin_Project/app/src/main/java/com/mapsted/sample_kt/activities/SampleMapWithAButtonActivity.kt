package com.mapsted.sample_kt.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.mapsted.map.MapApi
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMainBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController

class SampleMapWithAButtonActivity : AppCompatActivity(), MapstedMapUiApiProvider {

    private val TAG = SampleMapWithAButtonActivity::class.java.simpleName
    private lateinit var mBinding: ActivitySampleMainBinding
    private lateinit var mapUiApi: MapUiApi
    private var myTag: String? = null
    private var mapApi: MapApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_main)
        mapUiApi = MapstedSdkController.newInstance(applicationContext)
        mapApi = mapUiApi.getMapApi()
        Params.initialize(this)
        setupMapstedSdk()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: ")
        if (!mapUiApi.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun provideMapstedUiApi(): MapUiApi {
        return mapUiApi
    }

    private fun setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk")

        CustomParams.newBuilder()
            .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
            .setShowPropertyListOnMapLaunch(true)
            .setEnablePropertyListSelection(true)
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
                    val tag = "com.example.view.mybuttontag"
                    val inflate = LayoutInflater.from(this@SampleMapWithAButtonActivity)
                        .inflate(R.layout.sample_button, null, false)
                    inflate.findViewById<View>(R.id.btn_hello).setOnClickListener { v ->
                        val message = "Hello! You clicked " + (v as Button).text + "."
                        Log.d(TAG, "onClick: ");
                        Snackbar.make(
                        mBinding.rootView,
                        message,
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    }
                    myTag = mapUiApi.addViewToMap(tag, inflate)
                    Log.d(TAG, "onSuccess: myTag= $myTag")
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.e(
                        TAG,
                        "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage
                    )
                }
            })
    }
}