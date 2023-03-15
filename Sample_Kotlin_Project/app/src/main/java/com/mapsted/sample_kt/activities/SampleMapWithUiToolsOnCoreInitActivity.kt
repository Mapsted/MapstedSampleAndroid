package com.mapsted.sample_kt.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mapsted.corepositioning.cppObjects.swig.Position
import com.mapsted.map.MapApi
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.CoreApi
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.MessageType
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMain1Binding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui.search.SearchCallbacksProvider


class SampleMapWithUiToolsOnCoreInitActivity : AppCompatActivity(), MapstedMapUiApiProvider,
    SearchCallbacksProvider {


    private lateinit var mBinding: ActivitySampleMain1Binding

    private var sdk: MapUiApi? = null
    private var mapApi: MapApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "::onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_main1)
        sdk = MapstedSdkController.newInstance(applicationContext)
        mapApi = sdk?.mapApi
        Params.initialize(this)
        setupMapstedSdk()
        mBinding.checkbox.setOnCheckedChangeListener { button, isCheck ->
            if (isCheck) {
                sdk?.mapApi?.coreApi?.locationManager()?.addPositionChangeListener(positionListener)
            } else {
                sdk?.mapApi?.coreApi?.locationManager()?.removePositionChangeListener(positionListener)
            }
        }
    }

    private val positionListener: CoreApi.LocationManager.PositionChangeListener =
        CoreApi.LocationManager.PositionChangeListener { t ->
            mBinding.tvPositionStatus.text = "${System.currentTimeMillis()} ${t?.x} ${t?.y}"
            Log.d(TAG, "position change: " + t);
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (!sdk!!.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroy() {
        sdk!!.onDestroy()
        super.onDestroy()
    }

    override fun provideMapstedUiApi(): MapUiApi {
        if (sdk == null) sdk = MapstedSdkController.newInstance(applicationContext)
        return sdk!!
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(TAG, "::onBackPressed")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        sdk!!.onConfigurationChanged(this, newConfig)
    }

    fun setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk")
        CustomParams.newBuilder(this)
            .setBaseMapStyle(BaseMapStyle.GREY)
            .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
            .setShowPropertyListOnMapLaunch(true)
            .setMapZoomRange(MapstedMapRange(6.0f, 24.0f))
            .build()

        sdk!!.initializeMapstedSDK(
            this,
            mBinding.myMapUiTool,
            mBinding.myMapContainer,
            object : MapstedInitCallback {
                override fun onCoreInitialized() {
                    Log.d(TAG, "onCoreInitialized: ")
                    val propertyId = 504
                    sdk!!.mapApi.coreApi.propertyManager().getCategories(propertyId) {
                        Log.d(TAG, "onCoreInitialized: ${it.rootCategories.size}")
                        Log.d(TAG, "onCoreInitialized: ${it.allCategories.size}")
                    }

                    sdk?.mapApi?.coreApi?.propertyManager()
                        ?.findEntityByName("Gap", propertyId) {
                            Log.d(TAG, "onCoreInitialized: ${it.joinToString { se -> se.name }}")
                        }
                }

                override fun onMapInitialized() {
                    Log.d(TAG, "onMapInitialized: ")
                }

                override fun onSuccess() {
                    Log.d(TAG, "onSuccess: ")
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.d(TAG, "onFailure: ")
                }

                override fun onMessage(messageType: MessageType, s: String) {
                    Log.d(TAG, "onMessage: $s")
                }
            })
    }

    override fun getSearchCoreSdkCallback(): SearchCallbacksProvider.SearchCoreSdkCallback? {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show()
        return null
    }

    override fun getSearchFeedCallback(): SearchCallbacksProvider.SearchFeedCallback? {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show()
        return null
    }

    override fun getSearchAlertCallback(): SearchCallbacksProvider.SearchAlertCallback? {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show()
        return null
    }

    companion object {
        private val TAG = SampleMapWithUiToolsOnCoreInitActivity::class.java.simpleName
    }
}