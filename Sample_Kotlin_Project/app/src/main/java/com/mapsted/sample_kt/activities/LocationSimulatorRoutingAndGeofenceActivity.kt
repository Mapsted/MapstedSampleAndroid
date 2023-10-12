package com.mapsted.sample_kt.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mapsted.corepositioning.cppObjects.swig.LatLng
import com.mapsted.corepositioning.cppObjects.swig.MercatorZone
import com.mapsted.corepositioning.cppObjects.swig.Position
import com.mapsted.geofence.GeofenceApi
import com.mapsted.geofence.MapstedGeofenceApi
import com.mapsted.geofence.triggers.*
import com.mapsted.map.MapApi
import com.mapsted.map.MapstedMapApi
import com.mapsted.map.models.MapInitializationDetails
import com.mapsted.positioning.*
import com.mapsted.positioning.core.network.property_metadata.model.Category
import com.mapsted.positioning.core.utils.Logger
import com.mapsted.positioning.core.utils.calcs.MapCalc
import com.mapsted.positioning.core.utils.common.Params
import com.mapsted.positioning.coreObjects.EntityZone
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController
import com.mapsted.ui.R
import com.mapsted.ui.databinding.MapstedMapActivityBinding
import com.mapsted.ui.map.MapViewModel
import com.mapsted.ui.map.UserPositionInitResult
import com.mapsted.ui.search.Poi
import com.mapsted.ui.search.SearchCallbacksProvider
import com.mapsted.ui.search.SearchCallbacksProvider.*
import java.util.*
import java.util.function.Consumer

/**
 * This activity uses Mapsted's location simulator feature
 * to demonstrate the blue dot positioning/routing/turn-by-turn navigation experience.
 * This will also demonstrate the Geofence SDK feature
 */
class LocationSimulatorRoutingAndGeofenceActivity : AppCompatActivity(), MapstedMapUiApiProvider,
    SearchCallbacksProvider {
    private lateinit var mBinding: MapstedMapActivityBinding
    private lateinit var coreApi: CoreApi
    private lateinit var mapApi: MapApi
    private lateinit var mapUiApi: MapUiApi
    private lateinit var geofenceApi: GeofenceApi
    private lateinit var mapViewModel: MapViewModel
    private val propertyId = 504

    private enum class SimulatorPath {
        LevelOneToFido, LevelOneLevelTwoToFootLocker
    }

    // Change enum if different path is desired
    private val simulatorPath = SimulatorPath.LevelOneLevelTwoToFootLocker

    // Change value to adjust speed of blue dot (e.g., 1.0 is normal walking pace, 4.0 can reduce weight time while developing)
    private val simulatorWalkSpeedModifier = 2.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Params.initialize(this)

        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.mapsted_map_activity
        )
        mBinding.progressBar.visibility = View.VISIBLE

        // Initialize objects
        coreApi = MapstedCoreApi.newInstance(this, MapstedForegroundService::class.java)
        mapApi = MapstedMapApi.newInstance(this, coreApi)
        mapUiApi = MapstedSdkController.newInstance(applicationContext, mapApi)

        initializeMapstedSdk { success: Boolean ->
            Logger.v("initializeMapstedSdk: %s", if (success) "Success" else "Failed")
            mBinding.progressBar.visibility = View.GONE
            setupTestGeofenceSdk()

            // Delay a bit and then notify where the user can request a route to
            Handler(Looper.getMainLooper()).postDelayed({
                val destination: String = when (simulatorPath) {
                    SimulatorPath.LevelOneToFido -> "Fido"
                    SimulatorPath.LevelOneLevelTwoToFootLocker -> "Foot Locker"
                }

                Toast.makeText(
                    this@LocationSimulatorRoutingAndGeofenceActivity,
                    "To test routing, please request a route to: $destination",
                    Toast.LENGTH_LONG
                ).show()
            }, 5000)
        }
    }
    // region SDK Initialization
    /**
     * Initialize all Mapsted SDK's
     */
    private fun initializeMapstedSdk(onComplete: Consumer<Boolean>) {
        Logger.d("initializeMapstedSdk: ")
        initializeCoreSdk { coreSuccess: Boolean ->
            Logger.v("coreSuccess: %s", if (coreSuccess) "Success" else "Failure")
            if (coreSuccess) {
                initializeMapSdk { mapSuccess: Boolean ->
                    Logger.v("mapSuccess: %s", if (mapSuccess) "Success" else "Failure")
                    if (mapSuccess) {
                        initializeMapUiSdk { mapUiSuccess: Boolean ->
                            Logger.v("mapUiSuccess: %s", if (mapUiSuccess) "Success" else "Failure")
                            onComplete.accept(mapUiSuccess)
                        }
                    } else {
                        onComplete.accept(false)
                    }
                }
            } else {
                onComplete.accept(false)
            }
        }
    }

    /**
     * Initialize Core SDK
     */
    private fun initializeCoreSdk(onComplete: Consumer<Boolean>) {

        val path: List<MercatorZone> = getLocationSimulatorPath(simulatorPath)

        val mapstedCoreDetail: MapstedCoreDetail = MapstedCoreDetail.Builder(this)
            .setSimulatorUserPath(path)
            .setSimulatorWalkSpeedMultiplier(simulatorWalkSpeedModifier)
            .build()

        coreApi.initialize(mapstedCoreDetail, object : CoreApi.CoreInitCallback {
            override fun onSuccess() {
                Logger.v("CoreSdk: Initialize: onSuccess")
                onComplete.accept(true)
            }

            override fun onFailure(sdkError: SdkError) {
                Logger.v("CoreSdk: Initialize: onFailure")
                onComplete.accept(false)
            }

            override fun onMessage(messageType: MessageType, message: String) {}
        })

        coreApi.locationManager()
            .addPositionChangeListener { position: Position ->
                Logger.w(
                    "PositionChange: (%d, %d, %d) -> (%.1f, %.1f)",
                    position.propertyId, position.buildingId, position.floorId,
                    position.x, position.y
                )
            }
    }

    /**
     * Initialize Map SDK
     */
    private fun initializeMapSdk(onComplete: Consumer<Boolean>) {
        Logger.d("initializeMapUiSdk: ")
        val flMapContent: FrameLayout = mBinding.flMapContent
        val mapInitializationDetails = MapInitializationDetails(this, flMapContent)

        mapApi.setup().initialize(mapInitializationDetails, object : MapApi.MapInitCallback {
            override fun onFailure(sdkError: SdkError) {
                Logger.v("MapSdk: Initialize: onFailure")
                onComplete.accept(false)
            }

            override fun onSuccess() {
                Logger.v("MapSdk: Initialize: onSuccess")
                onComplete.accept(true)
            }

            override fun onCoreInitiated() {
                Logger.v("MapSdk: onCoreInitiated")
            }

            override fun onMessage(messageType: MessageType, message: String) {}
        })
    }

    /**
     * Initialize Map UI SDK
     */
    private fun initializeMapUiSdk(onComplete: Consumer<Boolean>) {
        Logger.d("initializeMapUiSdk: ")
        val flMapContent: FrameLayout = mBinding.flMapContent
        val flMapUi: FrameLayout = mBinding.flMapFragment
        mapUiApi.initializeMapstedSDK(
            this,
            flMapUi,
            flMapContent,
            object : MapstedInitCallback {
                override fun onCoreInitialized() {
                    Logger.d("initializeMapUiSdk: onCoreInitialized")
                }

                override fun onMapInitialized() {
                    Logger.d("initializeMapUiSdk: onMapInitialized")
                }

                override fun onSuccess() {
                    Logger.v("Initialize: onSuccess")
                    val provider = ViewModelProvider(
                        this@LocationSimulatorRoutingAndGeofenceActivity,
                        MapViewModel.createProvider(application, mapUiApi)
                    )
                    mapViewModel = provider.get(MapViewModel::class.java)
                    mapViewModel.init()
                    mapViewModel.setPropertyId(propertyId)
                    mapViewModel.initPositioningOrPropertiesSetup()
                    onComplete.accept(true)

                    mapViewModel.userPositionInitResult.observe(
                        this@LocationSimulatorRoutingAndGeofenceActivity
                    ) { result: UserPositionInitResult ->
                        Logger.d("onChange: %s", result)
                        if (result.resultType == UserPositionInitResult.ResultType.SUCCESS) {
                            mapUiApi.setMapViewVisibility(View.VISIBLE)
                        }
                    }
                }

                override fun onFailure(sdkError: SdkError) {
                    Logger.v(
                        "Initialize:  onFailure (Error: %d: %s)",
                        sdkError.errorCode,
                        sdkError.errorMessage
                    )
                    mapViewModel.sdkInitFailure(sdkError)
                    onComplete.accept(false)
                }

                override fun onMessage(messageType: MessageType, message: String) {
                    Logger.v("Initialize: onMessage: %s", message)
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }
    //endregion
    //region Simulator Paths
    /** Generates location simulator path based on provided enum scenario  */
    private fun getLocationSimulatorPath(simulatorPath: SimulatorPath): List<MercatorZone> {
        return when (simulatorPath) {
            SimulatorPath.LevelOneToFido -> locationsimulatorpathLeveloneTofido
            SimulatorPath.LevelOneLevelTwoToFootLocker -> locationsimulatorpathLeveloneLeveltwoTofootlocker
        }
    }

    /**
     * Generates location simulator path along level one
     */
    private val locationsimulatorpathLeveloneTofido: List<MercatorZone>
        get() {
            val levelOneLatLngs: Array<LatLng> = arrayOf(
                LatLng(43.59270591410157, -79.64468396358342),
                LatLng(43.59275554576186, -79.64405625196909),
                LatLng(43.59293620465817, -79.64386985725366),
                LatLng(43.59302554127498, -79.64237869953212),
                LatLng(43.59312480402684, -79.64235677074178),
                LatLng(43.5931704648377, -79.64221149250811),
                LatLng(43.593152597568235, -79.6420333210891),
                LatLng(43.59316116651411, -79.64169529771698),
                LatLng(43.593177048529554, -79.64133621378046),
                LatLng(43.59336167665214, -79.64134443707688),
                LatLng(43.59344307166634, -79.6413142849905)
            )

            val simulatorPath: MutableList<MercatorZone> = ArrayList<MercatorZone>()

            Arrays.stream(levelOneLatLngs).forEach { latLng: LatLng? ->
                simulatorPath.add(
                    MercatorZone(
                        504,
                        504,
                        941,
                        MapCalc.toMercator(latLng)
                    )
                )
            }

            return simulatorPath
        }

    /**
     * Generates location simulator path along level one and level two to Foot Locker
     */
    private val locationsimulatorpathLeveloneLeveltwoTofootlocker: List<MercatorZone>
        get() {
            val levelOneLatLngs: Array<LatLng> = arrayOf(
                LatLng(43.59270506141888, -79.64467918464558),
                LatLng(43.59274299108304, -79.64407554834054),
                LatLng(43.59284979290362, -79.6439460008228),
                LatLng(43.59286875769334, -79.64355460194005),
                LatLng(43.59291567056778, -79.64341265093671),
                LatLng(43.59290777113583, -79.64330951843408)
            )

            val levelTwoLatLngs: Array<LatLng> = arrayOf(
                LatLng(43.59290777113583, -79.64330951843408),
                LatLng(43.59291305771916, -79.64321462730396),
                LatLng(43.59354343449601, -79.64326847002535),
                LatLng(43.59363085147197, -79.64315247246824),
                LatLng(43.59367739812217, -79.6420787112964),
                LatLng(43.59363085147197, -79.64207400869265)
            )

            val simulatorPath: MutableList<MercatorZone> = ArrayList<MercatorZone>()

            Arrays.stream(levelOneLatLngs).forEach { latLng: LatLng? ->
                simulatorPath.add(
                    MercatorZone(
                        504,
                        504,
                        941,
                        MapCalc.toMercator(latLng)
                    )
                )
            }

            Arrays.stream(levelTwoLatLngs).forEach { latLng: LatLng? ->
                simulatorPath.add(
                    MercatorZone(
                        504,
                        504,
                        942,
                        MapCalc.toMercator(latLng)
                    )
                )
            }

            return simulatorPath
        }

    // endregion
    //region Geofences
    private fun setupTestGeofenceSdk() {
        Logger.v("setupGeofenceSdk")

        // Create instance of GeofenceApi
        geofenceApi = MapstedGeofenceApi.newInstance(this, coreApi)

        // Create single geofence trigger (Poi Vicinity) for Entering POI vicinity
        geofenceApi.geofenceTriggers().addGeofenceTrigger(
            504,
            GeofenceTrigger.Builder(504, "Enter_Urban_Planet_Vicinity")
                .setLocationCriteria(
                    PoiVicinityLocationCriteria.Builder()
                        .addEntityZone(EntityZone(504, 504, 941, 34)) // Urban Planet
                        .setActivationDistanceTh(10.0f)
                        .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_ENTER)
                        .build()
                )
                .build()
        )

        // Create single geofence trigger (Poi Vicinity) for Exiting POI vicinity
        geofenceApi.geofenceTriggers().addGeofenceTrigger(
            504,
            GeofenceTrigger.Builder(504, "Exit_Urban_Planet_Vicinity")
                .setLocationCriteria(
                    PoiVicinityLocationCriteria.Builder()
                        .addEntityZone(EntityZone(504, 504, 941, 34)) // Urban Planet
                        .setActivationDistanceTh(10.0f)
                        .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_EXIT)
                        .build()
                )
                .build()
        )

        // Create a list of geofence triggers
        val geofenceTriggers: MutableList<GeofenceTrigger> = ArrayList<GeofenceTrigger>()
        geofenceTriggers.add(
            GeofenceTrigger.Builder(504, "Enter_SquareOne_Property")
                .setLocationCriteria(
                    PropertyLocationCriteria.Builder(504)
                        .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_ENTER)
                        .build()
                )
                .build()
        )
        geofenceTriggers.add(
            GeofenceTrigger.Builder(504, "Enter_SquareOne_LevelTwo")
                .setLocationCriteria(
                    FloorLocationCriteria.Builder(942) // Level 2
                        .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_ENTER)
                        .build()
                )
                .build()
        )
        geofenceApi.geofenceTriggers().addGeofenceTriggers(504, geofenceTriggers)

        // Add callback listener
        geofenceApi.geofenceTriggers().addListener { propertyId, geofenceId ->

            // Unsubscribe after successfully triggering (so only triggers once)
            geofenceApi.geofenceTriggers().removeGeofenceTrigger(propertyId, geofenceId)
            val geofenceMsd = java.lang.String.format(
                Locale.CANADA,
                "GeofenceTriggered: pId: %d -> geofenceId: %s", propertyId, geofenceId
            )
            Logger.v(geofenceMsd)
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    geofenceMsd,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //endregion
    //region Lifecycle
    override fun onStart() {
        super.onStart()
        mapUiApi.setMapViewVisibility(View.VISIBLE)
        mBinding.lifecycleOwner = this
    }

    override fun onStop() {
        mBinding.lifecycleOwner = null
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapUiApi.onResume()
        mapApi.onResume()
        coreApi.onResume()
    }

    override fun onDestroy() {
        Logger.v("onDestroy")
        mapUiApi.setMapViewVisibility(View.GONE)
        coreApi.onDestroy()
        geofenceApi.onDestroy()
        mapApi.onDestroy()
        mapUiApi.onDestroy()
        super.onDestroy()
    }

    //endregion
    //region Permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!mapUiApi.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    //endregion
    //region Providers
    override fun provideMapstedUiApi(): MapUiApi {
        return mapUiApi
    }

    override fun getSearchCoreSdkCallback(): SearchCoreSdkCallback {
        return object : SearchCoreSdkCallback {
            override fun getCategories(propertyIds: List<Int>): List<Category> {
                return ArrayList()
            }

            override fun openCategory(
                categoryId: String,
                categoryName: String,
                query: String,
                propertyId: Int
            ) {
                Toast.makeText(applicationContext, "Open Category", Toast.LENGTH_SHORT).show()
            }

            override fun openGlobalStorePropertyList(
                poi: Poi,
                text: String,
                query: String,
                analyticsSearchBarId: String
            ) {
                Toast.makeText(
                    applicationContext,
                    "Open Properties With this entity",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getSearchFeedCallback(): SearchFeedCallback? {
        return null
    }

    override fun getSearchAlertCallback(): SearchAlertCallback? {
        return null
    }
}