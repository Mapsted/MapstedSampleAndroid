package com.mapsted.sample_kt.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mapsted.map.MapApi
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.MessageType
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.positioning.coreObjects.Entity
import com.mapsted.positioning.coreObjects.ISearchable
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMainBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui.search.SearchCallbacksProvider
import com.mapsted.ui.searchables_list.SearchablesListFragment


class SearchableListActivity : AppCompatActivity(), MapstedMapUiApiProvider,
    SearchCallbacksProvider {

    private val TAG: String = SearchableListActivity::class.java.simpleName
    private lateinit var mBinding: ActivitySampleMainBinding

    private var sdk: MapUiApi? = null
    private var mapApi: MapApi? = null

    val description = "Show searchable list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_main)
        sdk = MapstedSdkController.newInstance(applicationContext)
        mapApi = sdk?.mapApi
        Params.initialize(this)
        setupMapstedSdk()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
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

    override fun provideMapstedUiApi(): MapUiApi? {
        if (sdk == null) sdk = MapstedSdkController.newInstance(applicationContext)
        return sdk
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
            .setShowPropertyListOnMapLaunch(false)
            .setMapZoomRange(MapstedMapRange(6.0f, 24.0f))
            .build()

        sdk?.initializeMapstedSDK(
            this,
            mBinding.myMapUiTool,
            mBinding.myMapContainer, object : MapstedInitCallback {
                override fun onCoreInitialized() {
                    Log.i(TAG, "::setupMapstedSdk ::onCoreInitialized")
                    sdk!!.mapApi.coreApi.propertyManager().getCategories(504) {
                        Log.d(TAG, "onCoreInitialized: ${it.rootCategories.size}")
                        Log.d(TAG, "onCoreInitialized: ${it.allCategories.size}")
                    }

                    sdk!!.mapApi.coreApi.propertyManager().searchPoi(504, null) {
                        showSearchableListFragment(it);
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
                    Log.d(TAG, "::onMessage: $p1");
                }
            })
    }

    private fun showSearchableListFragment(searchableList: List<ISearchable>) {
        Log.d(TAG, "showSearchableListFragment: ${searchableList.size}")
        val context = this;
        searchableList.forEach {
            Log.d(TAG, "showSearchableListFragment: entityZones.size" + it.entityZones.size)
            it.getLocations(mapApi?.coreApi) { it1 -> Log.d(TAG, "showSearchableListFragment: $it1") }
            it.entityZones.forEach { it2 -> Log.d(TAG, "showSearchableListFragment: location:${it2.location}, entityId:${it2.entityId}") }
        }
        val searchablesListFragment =
            SearchablesListFragment.newInstance("My Title", searchableList)
        searchablesListFragment.setListener { entity ->
            Toast.makeText(context, "clicked $entity", Toast.LENGTH_SHORT).show()
            supportFragmentManager.beginTransaction().remove(searchablesListFragment).commitAllowingStateLoss()
            mapApi?.selectEntity(entity)
        }
        supportFragmentManager.beginTransaction().add(R.id.container, searchablesListFragment)
            .commit();
    }

    override fun getSearchCoreSdkCallback(): SearchCallbacksProvider.SearchCoreSdkCallback? {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show()
        return null;
    }

    override fun getSearchFeedCallback(): SearchCallbacksProvider.SearchFeedCallback? {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show()
        return null;
    }

    override fun getSearchAlertCallback(): SearchCallbacksProvider.SearchAlertCallback? {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show()
        return null;
    }
}