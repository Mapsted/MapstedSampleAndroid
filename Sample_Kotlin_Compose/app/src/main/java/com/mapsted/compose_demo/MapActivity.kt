package com.mapsted.compose_demo

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.mapsted.compose_demo.databinding.MapUiBinding
import com.mapsted.map.MapApi
import com.mapsted.map.MapApi.DefaultSelectPropertyListener
import com.mapsted.map.MapstedMapApi
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.CoreApi
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.SdkStatusUpdate
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.search.SearchCallbacksProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MapActivity : AppCompatActivity(), MapstedMapUiApiProvider, SearchCallbacksProvider {

    private lateinit var binding: MapUiBinding

    private var coreApi: CoreApi? = null
    private var mapApi: MapApi? = null
    private var mapUiApi: MapUiApi? = null

    private var tActivityStart = 0L
    private var tStartInitMapsted = 0L
    private var tStartPlotRequest = 0L
    private var tPlotFinished = 0L

    companion object {
        private val TAG: String = MapActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.map_ui)
        binding.mapSpinner.show()

        coreApi = (application as DemoApplication).getCoreApi(this)
        mapApi = MapstedMapApi.newInstance(application, coreApi!!)
        mapUiApi = MapstedMapUiApi.newInstance(application, mapApi!!)

        tActivityStart = System.currentTimeMillis()

        setupMapUiApi()
    }

    override fun onDestroy() {
        Log.i(TAG, "::onDestroy")
        mapApi?.lifecycle()?.onDestroy()
        mapUiApi?.lifecycle()?.onDestroy()

        super.onDestroy()
    }

    override fun provideMapstedUiApi(): MapUiApi? {
        return mapUiApi
    }

    override fun provideMapstedMapApi(): MapApi? {
        return mapApi
    }

    override fun provideMapstedCoreApi(): CoreApi? {
        return coreApi
    }

    override fun onBackPressed() {

        if (mapUiApi != null && mapUiApi!!.lifecycle().onBackPressed()) {
            Log.i(TAG, "::onBackPressed - Handled by Mapsted")
            return
        }

        Log.i(TAG, "::onBackPressed - Processed")
        onBackPressedDispatcher.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mapUiApi?.lifecycle()?.onConfigurationChanged(this, newConfig)
    }

    private fun setupMapUiApi() {

        val params = CustomParams.newBuilder(this, binding.flBaseMap, binding.flMapUi)
            .setBaseMapStyle(BaseMapStyle.GREY)
            .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
            .setMapZoomRange(MapstedMapRange(6.0f, 24.0f))
            .setEnableMapOverlayFeature(true)
            .build()

        tStartInitMapsted = System.currentTimeMillis()

        mapUiApi?.setup()?.initialize(
            params,
            object : MapUiApi.MapUiInitCallback {
                override fun onCoreInitialized() {
                    Log.d(TAG, "setupMapUiApi: onCoreInitialized")
                }

                override fun onMapInitialized() {
                    Log.d(TAG, "setupMapUiApi: onMapInitialized")
                }

                override fun onSuccess() {
                    binding.mapSpinner.hide()
                    Log.d(TAG, "setupMapUiApi: onSuccess")

                    // Grab first property in licence
                    val propertyInfos = coreApi?.properties()?.infos
                    val numProperties = propertyInfos?.size
                    val propertyId = if (numProperties != null && numProperties > 0)
                        propertyInfos.keys.first()
                    else -1;

                    tStartPlotRequest = System.currentTimeMillis()

                    mapApi?.data()?.selectPropertyAndDrawIfNeeded(
                        propertyId,
                        object : DefaultSelectPropertyListener() {
                            override fun onPlotted(isSuccess: Boolean, propertyId: Int) {

                                tPlotFinished = System.currentTimeMillis()

                                val dtActivityToPlotSec = (tPlotFinished - tActivityStart) / 1000.0
                                val dtMapInitSec = (tStartPlotRequest - tStartInitMapsted) / 1000.0
                                val dtPlotSec = (tPlotFinished - tStartPlotRequest) / 1000.0

                                val msg = String.format(Locale.CANADA, "Property ($propertyId): %s -> PlotTime: %.1f s",
                                    if (isSuccess) "Success" else "Failed",
                                    dtPlotSec)

                                Log.d(TAG, msg)

                                Log.d(TAG, String.format(Locale.CANADA,
                                    "dtActivityToPlot_s: %.3f s, dtMapInit_s: %.3f, dtPlot_s: %.3f",
                                    dtActivityToPlotSec, dtMapInitSec, dtPlotSec))

                                lifecycleScope.launch(Dispatchers.Main) {
                                    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                                }

                                super.onPlotted(isSuccess, propertyId)
                            }
                        }
                    )

                    coreApi?.setup()?.startLocationServices(this@MapActivity,
                        object : CoreApi.LocationServicesCallback {
                        override fun onSuccess() {
                            Log.d(TAG, "coreApi callback -> LocServices: onSuccess")
                        }
                        override fun onFailure(sdkError: SdkError) {
                            Log.d(TAG, "coreApi callback -> LocServices: onFailure: $sdkError")
                        }
                    })
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.d(TAG, "setupMapUiApi: onFailure: $sdkError")
                }

                override fun onStatusUpdate(sdkUpdate: SdkStatusUpdate) {
                    Log.d(TAG, "setupMapUiApi: onStatusUpdate: $sdkUpdate")
                }
            },
            object : CoreApi.LocationServicesCallback {
                override fun onSuccess() {
                    Log.d(TAG, "LocServices: onSuccess")
                }
                override fun onFailure(sdkError: SdkError) {
                    Log.d(TAG, "LocServices: onFailure: $sdkError")
                }
            }
        )
    }

    override fun getSearchCoreSdkCallback(): SearchCallbacksProvider.SearchCoreSdkCallback? {
        return null
    }

    override fun getSearchFeedCallback(): SearchCallbacksProvider.SearchFeedCallback? {
        return null
    }

    override fun getSearchAlertCallback(): SearchCallbacksProvider.SearchAlertCallback? {
        return null
    }
}

