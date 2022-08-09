package com.mapsted.sample_kt.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mapsted.map.MapApi
import com.mapsted.map.MapApi.DefaultSelectPropertyListener
import com.mapsted.map.MapSelectionChangeListener
import com.mapsted.map.models.layers.BaseMapStyle
import com.mapsted.map.views.MapPanType
import com.mapsted.map.views.MapstedMapRange
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.MessageType
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.positioning.coreObjects.Entity
import com.mapsted.positioning.coreObjects.SearchEntity
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleMainBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui.search.SearchCallbacksProvider


class SampleSelectAnEntityActivity : AppCompatActivity(), MapstedMapUiApiProvider,
    SearchCallbacksProvider {

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
                    mapApi!!.selectPropertyAndDrawIfNeeded(
                        propertyId,
                        object : DefaultSelectPropertyListener() {
                            override fun onPlotted(isSuccess: Boolean, propertyId: Int) {
                                super.onPlotted(isSuccess, propertyId)
                                Log.d(TAG, "onPlotted: propertyId=$propertyId success=$isSuccess")
                                selectAnEntityOnMap()
                            }
                        })
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.d(TAG, "onFailure: ")
                }

                override fun onMessage(messageType: MessageType, s: String) {
                    Log.d(TAG, "onMessage: $s")
                }
            })
    }

    private fun selectAnEntityOnMap() {
        Log.d(TAG, "selectAnEntityOnMap: ")
        val coreApi = sdk!!.mapApi.coreApi
        val propertyId = mapApi!!.selectedPropertyId
        Toast.makeText(this, "Selecting Gap store on map", Toast.LENGTH_LONG).show()
        coreApi.propertyManager().findEntityByName(
            "Gap", propertyId!!
        ) { filteredResult: List<SearchEntity> ->
            if (filteredResult.size > 0) {
                val searchEntity = filteredResult[0]
                //while it may have multiple entityZones if it spans multiple floors, we will select the first one.
                val entityZone = searchEntity.entityZones.first()
                coreApi.propertyManager().getEntity(
                    entityZone
                ) { entity: Entity ->
                    Log.d(
                        TAG,
                        "selectAnEntityOnMap: $entity"
                    )
                    mapApi!!.addMapSelectionChangeListener(object : MapSelectionChangeListener {
                        override fun onPropertySelectionChange(propertyId: Int, previousPropertyId: Int) {
                            Log.d(TAG, "onPropertySelectionChange: propertyId $propertyId, previous $previousPropertyId")
                        }

                        override fun onBuildingSelectionChange(propertyId: Int, buildingId: Int, previousBuildingId: Int) {
                            Log.d(TAG, "onBuildingSelectionChange: propertyId $propertyId, buildingId $buildingId, previousBuildingId $previousBuildingId")
                        }

                        override fun onFloorSelectionChange(propertyId: Int, buildingId: Int, floorId: Int) {
                            Log.d(TAG, "onFloorSelectionChange: buildingId $buildingId, floorId $floorId")
                        }

                        override fun onEntitySelectionChange(entityId: Entity) {
                            Log.d(TAG, "onEntitySelectionChange: entityId $entityId")
                        }
                    })
                    mapApi!!.selectEntity(entity)
                }
            }
        }
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
        private val TAG = SampleSelectAnEntityActivity::class.java.simpleName
    }
}