package com.mapsted.sample_kt.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mapsted.map.MapApi
import com.mapsted.map.MapstedMapApi
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.CoreApi
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.MessageType
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.positioning.coreObjects.ISearchable
import com.mapsted.sample_kt.SampleMyApplication
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMainBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui.search.SearchCallbacksProvider
import com.mapsted.ui.searchables_list.SearchablesListFragment


class SamplePoiListActivity : AppCompatActivity(), MapstedMapUiApiProvider,
    SearchCallbacksProvider {

    private val TAG: String = SamplePoiListActivity::class.java.simpleName
    private lateinit var mBinding: ActivitySampleMainBinding

    private lateinit var mapUiApi: MapUiApi
    private lateinit var mapApi: MapApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_main)
        val coreApi = (application as SampleMyApplication).coreApi
        mapApi = MapstedMapApi.newInstance(applicationContext, coreApi)
        mapUiApi = MapstedSdkController.newInstance(applicationContext, mapApi)
        Params.initialize(this)
        setupMapstedSdk()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (!mapUiApi.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroy() {
        mapApi.onDestroy()
        mapUiApi.onDestroy()
        super.onDestroy()
    }

    override fun provideMapstedUiApi(): MapUiApi {
        return mapUiApi
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.i(TAG, "::onBackPressed")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mapUiApi.onConfigurationChanged(this, newConfig)
    }

    fun setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk")
        CustomParams.newBuilder()
            .setBaseMapStyle(BaseMapStyle.GREY)
            .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
            .setShowPropertyListOnMapLaunch(false)
            .setMapZoomRange(MapstedMapRange(6.0f, 24.0f))
            .build()

        mapUiApi.initializeMapstedSDK(
            this,
            mBinding.myMapUiTool,
            mBinding.myMapContainer, object : MapstedInitCallback {
                override fun onCoreInitialized() {
                    Log.i(TAG, "::setupMapstedSdk ::onCoreInitialized")
                    mapUiApi.mapApi.coreApi.propertyManager().getCategories(504) {
                        Log.d(TAG, "onCoreInitialized: ${it.rootCategories.size}")
                        Log.d(TAG, "onCoreInitialized: ${it.allCategories.size}")
                    }

                    mapUiApi.mapApi.coreApi.propertyManager().searchPoi(504, null) {
                        showSearchableListFragment(it)
                    }

                }

                override fun onMapInitialized() {
                    Log.i(TAG, "::setupMapstedSdk ::onMapInitialized")
                }

                override fun onSuccess() {
                    Log.i(TAG, "::setupMapstedSdk ::onSuccess")
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.e(TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage)
                }

                override fun onMessage(p0: MessageType?, p1: String?) {
                    Log.d(TAG, "::onMessage: $p1")
                }
            })
    }

    private fun showSearchableListFragment(searchableList: List<ISearchable>) {
        searchableList.forEach {
            Log.d(TAG, "showSearchableListFragment: entityZones.size" + it.entityZones.size)
            it.getLocations(mapApi.coreApi) { it1->
                Log.d(TAG, "showSearchableListFragment: " + it1)
            }
            it.entityZones.forEach { it2 ->
                Log.d(
                    TAG,
                    "showSearchableListFragment: location:" + it2.location + ", entity:" + it2.entity + ", entityId:" + it2.entityId
                )
            }
        }
        val searchablesListFragment =
            SearchablesListFragment.newInstance("My Title", searchableList)
        supportFragmentManager.beginTransaction().add(R.id.container, searchablesListFragment)
            .commit()
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
}