package com.mapsted.sample_kt.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mapsted.corepositioning.cppObjects.swig.CppRouteResponse
import com.mapsted.map.MapApi
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.MessageType
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.positioning.coreObjects.*
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMainBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui.search.SearchCallbacksProvider


class SampleRoutePreviewActivity : AppCompatActivity(), MapstedMapUiApiProvider,
    SearchCallbacksProvider {

    private lateinit var context: Context
    private lateinit var mBinding: ActivitySampleMainBinding

    private var sdk: MapUiApi? = null
    private var mapApi: MapApi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "::onCreate")
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_main)
        sdk = MapstedSdkController.newInstance(applicationContext)
        mapApi = sdk?.mapApi
        Params.initialize(this)
        setupMapstedSdk()
        context = this;
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
        CustomParams.newBuilder()
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
                }

                override fun onMapInitialized() {
                    Log.d(TAG, "onMapInitialized: ")
                }

                override fun onSuccess() {
                    Log.d(TAG, "onSuccess: ")
                    val propertyId = 504
                    val propertyManager = sdk?.mapApi?.coreApi?.propertyManager()
                    propertyManager?.findEntityByName("Gap", propertyId) { results1 ->
                        val r1 = results1[0]
                        Log.d(TAG, "onSuccess: findEntityByName: name= ${r1.name}")
                        propertyManager.findEntityByName("Starbucks", propertyId) { results2 ->
                            val r2 = results2[0]
                            Log.d(TAG, "onSuccess: findEntityByName: name= ${r2.name}")
                            //Gap to Starbucks
                            showRoutingPreview(r1, r2)
                        }
                    }
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.d(TAG, "onFailure: ")
                }

                override fun onMessage(messageType: MessageType, s: String) {
                    Log.d(TAG, "onMessage: $s")
                }
            })
    }

    private fun showRoutingPreview(start: ISearchable, destination: ISearchable) {
        Log.d(TAG, "showRoutingPreview: start=${start} , destination=${destination}")

        val destinationWaypoint = WaypointHelper.from(destination)
        val startWaypoint = WaypointHelper.from(start)
        val routeRequest = RouteRequest.Builder()
            .setRouteOptions(RouteOptions(true, true, true, false , false))
            .setStartWaypoint(startWaypoint)
            .addDestinationWaypoint(destinationWaypoint)
            .build();
        sdk?.mapApi?.requestRouting(routeRequest, object : RoutingRequestCallback {
            override fun onSuccess(routeResponse: RoutingResponse?) {
                sdk?.mapFragment?.showRoutePreviewFragment(routeResponse)
            }

            override fun onError(errorType: CppRouteResponse.ErrorType?, error: String?, alertIds: MutableList<String>?) {
                Log.d(TAG, "onError: errorType $errorType $error")
                runOnUiThread {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
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
        private val TAG = SampleRoutePreviewActivity::class.java.simpleName
    }
}