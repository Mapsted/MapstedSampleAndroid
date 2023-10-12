package com.mapsted.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.mapsted.MapstedBaseApplication;
import com.mapsted.corepositioning.cppObjects.swig.LatLng;
import com.mapsted.corepositioning.cppObjects.swig.MercatorZone;
import com.mapsted.geofence.GeofenceApi;
import com.mapsted.geofence.MapstedGeofenceApi;
import com.mapsted.geofence.triggers.FloorLocationCriteria;
import com.mapsted.geofence.triggers.GeofenceTrigger;
import com.mapsted.geofence.triggers.ILocationCriteria;
import com.mapsted.geofence.triggers.PoiVicinityLocationCriteria;
import com.mapsted.geofence.triggers.PropertyLocationCriteria;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.views.CustomMapParams;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.CoreParams;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.SdkStatusUpdate;
import com.mapsted.positioning.core.network.property_metadata.model.Category;
import com.mapsted.positioning.core.utils.Logger;
import com.mapsted.positioning.core.utils.calcs.MapCalc;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.positioning.coreObjects.EntityZone;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.databinding.MapstedMapActivityBinding;
import com.mapsted.ui.map.MapViewModel;
import com.mapsted.ui.search.Poi;
import com.mapsted.ui.search.SearchCallbacksProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * This activity uses Mapsted's location simulator feature
 * to demonstrate the blue dot positioning/routing/turn-by-turn navigation experience.
 * This will also demonstrate the Geofence SDK feature
 */
public class LocationSimulatorRoutingAndGeofenceActivity extends AppCompatActivity
        implements MapstedMapUiApiProvider, SearchCallbacksProvider
{
    private MapstedMapActivityBinding mBinding;

    private CoreApi coreApi;
    private MapApi mapApi;
    private MapUiApi mapUiApi;
    private GeofenceApi geofenceApi;

    private MapViewModel mapViewModel;

    private final int propertyId = 504;

    private enum SimulatorPath {
        LevelOne_ToFido,
        LevelOne_LevelTwo_ToFootLocker,
    }

    // Change enum if different path is desired
    private final SimulatorPath simulatorPath = SimulatorPath.LevelOne_LevelTwo_ToFootLocker;

    // Change value to adjust speed of blue dot (e.g., 1.0 is normal walking pace, 4.0 can reduce weight time while developing)
    private final float simulatorWalkSpeedModifier = 2.0F;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Params.initialize(this);

        mBinding = DataBindingUtil.setContentView(this, com.mapsted.ui.R.layout.mapsted_map_activity);
        mBinding.progressBar.setVisibility(View.VISIBLE);

        // Initialize objects
        coreApi = ((MapstedBaseApplication)getApplication()).getCoreApi(this);
        mapApi = MapstedMapApi.newInstance(this, coreApi);
        mapUiApi = MapstedMapUiApi.newInstance(this, mapApi);

        initializeMapstedSdk(success -> {
            Logger.v("initializeMapstedSdk: %s", success ? "Success" : "Failed");
            mBinding.progressBar.setVisibility(View.GONE);
            setupTestGeofenceSdk();

            // Delay a bit and then notify where the user can request a route to
            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                String destination = null;

                switch (simulatorPath) {
                    case LevelOne_ToFido:
                        destination = "Fido";
                        break;
                    case LevelOne_LevelTwo_ToFootLocker:
                        destination = "Foot Locker";
                        break;
                }

                if (destination != null) {
                    Toast.makeText(LocationSimulatorRoutingAndGeofenceActivity.this,
                            "To test routing, please request a route to: " + destination, Toast.LENGTH_LONG).show();
                }
            }, 5000);
        });
    }

    // region SDK Initialization

    /**
     * Initialize all Mapsted SDK's
     */
    public void initializeMapstedSdk(Consumer<Boolean> onComplete) {
        Logger.d("initializeMapstedSdk: ");

        CustomParams params = CustomParams.newBuilder(this, mBinding.flBaseMap, mBinding.flMapUi)
                .build();

        List<MercatorZone> path = getLocationSimulatorPath(simulatorPath);

        if (path != null && !path.isEmpty()) {
            CoreParams.SimulatorPath paramsPath = new CoreParams.SimulatorPath();
            paramsPath.path = path;
            paramsPath.walkSpeedModifier = simulatorWalkSpeedModifier;
            params.setSimulatorPath(paramsPath);
        }


        initializeCoreSdk(coreSuccess -> {
            Logger.v("coreSuccess: %s", coreSuccess ? "Success" : "Failure");
            if (coreSuccess) {
                initializeMapSdk(mapSuccess -> {
                    Logger.v("mapSuccess: %s", mapSuccess ? "Success" : "Failure");

                    if (mapSuccess) {
                        initializeMapUiSdk(mapUiSuccess -> {
                            Logger.v("mapUiSuccess: %s", mapUiSuccess ? "Success" : "Failure");
                            onComplete.accept(mapUiSuccess);
                        });
                    }
                    else {
                        onComplete.accept(false);
                    }

                });
            }
            else {
                onComplete.accept(false);
            }
        });
    }

    /**
     * Initialize Core SDK
     */
    private void initializeCoreSdk(Consumer<Boolean> onComplete) {

        CoreParams.SimulatorPath paramsPath = new CoreParams.SimulatorPath();

        paramsPath.path =getLocationSimulatorPath(simulatorPath); // For example, from above
        paramsPath.walkSpeedModifier = simulatorWalkSpeedModifier;

        CoreParams coreParams = CoreParams.newBuilder()
                .setSimulatorPath(paramsPath)
                .build();

        coreApi.setup().initialize(coreParams, new CoreApi.CoreInitCallback() {
            @Override
            public void onSuccess() {
                Logger.v("CoreSdk: Initialize: onSuccess");
                onComplete.accept(true);
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Logger.v("CoreSdk: Initialize: onFailure");
                onComplete.accept(false);
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {

            }
        });

        coreApi.locations().addPositionChangeListener(position ->
                Logger.w("PositionChange: (%d, %d, %d) -> (%.1f, %.1f)",
                        position.getPropertyId(), position.getBuildingId(), position.getFloorId(),
                        position.getX(), position.getY()));
    }

    /**
     * Initialize Map SDK
     */
    private void initializeMapSdk(Consumer<Boolean> onComplete) {
        Logger.d("initializeMapUiSdk: ");

        FrameLayout flBaseMap = mBinding.flBaseMap;

        CustomMapParams customMapParams = CustomMapParams.newBuilder(this, mBinding.flBaseMap)
                .build();

        mapApi.setup().initialize(customMapParams, new MapApi.MapInitCallback() {
            @Override
            public void onFailure(SdkError sdkError) {
                Logger.v("MapSdk: Initialize: onFailure");
                onComplete.accept(false);
            }

            @Override
            public void onSuccess() {
                Logger.v("MapSdk: Initialize: onSuccess");
                onComplete.accept(true);
            }

            @Override
            public void onCoreInitiated() {
                Logger.v("MapSdk: onCoreInitiated");
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {

            }
        });
    }

    /**
     * Initialize Map UI SDK
     */
    private void initializeMapUiSdk(Consumer<Boolean> onComplete) {
        Logger.d("initializeMapUiSdk: ");

        ViewModelProvider provider = new ViewModelProvider(LocationSimulatorRoutingAndGeofenceActivity.this,
                MapViewModel.createProvider(getApplication(), mapUiApi));

        mapViewModel = provider.get(MapViewModel.class);

        CustomParams customParams = CustomParams.newBuilder(this).build();
        customParams.setMapUiContainerView(mBinding.flMapUi);

        mapUiApi.setup().initialize(customParams, new MapUiApi.MapUiInitCallback() {
            @Override
            public void onCoreInitialized() {
                Logger.d("initializeMapUiSdk: onCoreInitialized");
            }

            @Override
            public void onMapInitialized() {
                Logger.d("initializeMapUiSdk: onMapInitialized");
            }

            @Override
            public void onSuccess() {
                Logger.v("Initialize: onSuccess");

                mapViewModel.init();
                mapViewModel.setPropertyId(propertyId);
                mapViewModel.init();
                onComplete.accept(true);

                mapViewModel.getIsUserPositionAvailable().observe(LocationSimulatorRoutingAndGeofenceActivity.this, result -> {
                    Logger.d("onChange: %s", result);
                    if (result) {
                        mapUiApi.customUi().setMapViewVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(SdkError sdkError) {
                mapViewModel.sdkInitFailure(sdkError);
                onComplete.accept(false);
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {

            }
        });

    }

    //endregion

    //region Simulator Paths

    /** Generates location simulator path based on provided enum scenario */
    private List<MercatorZone> getLocationSimulatorPath(SimulatorPath simulatorPath) {
        switch (simulatorPath) {
            case LevelOne_ToFido:
                return getLocationSimulatorPath_LevelOne_ToFido();
            case LevelOne_LevelTwo_ToFootLocker:
            default:
                return getLocationSimulatorPath_LevelOne_LevelTwo_ToFootLocker();
        }
    }

    /**
     * Generates location simulator path along level one
     */
    private List<MercatorZone> getLocationSimulatorPath_LevelOne_ToFido() {

        LatLng[] levelOneLatLngs = new LatLng[] {

                new LatLng(43.59270591410157,-79.64468396358342),
                new LatLng(43.59275554576186,-79.64405625196909),
                new LatLng(43.59293620465817,-79.64386985725366),
                new LatLng(43.59302554127498,-79.64237869953212),
                new LatLng(43.59312480402684,-79.64235677074178),
                new LatLng(43.5931704648377,-79.64221149250811),
                new LatLng(43.593152597568235,-79.6420333210891),
                new LatLng(43.59316116651411,-79.64169529771698),
                new LatLng(43.593177048529554,-79.64133621378046),
                new LatLng(43.59336167665214,-79.64134443707688),
                new LatLng(43.59344307166634,-79.6413142849905),
        };

        List<MercatorZone> simulatorPath = new ArrayList<>();
        Arrays.stream(levelOneLatLngs).forEach(latLng -> simulatorPath.add(new MercatorZone(504, 504, 941, MapCalc.toMercator(latLng))));

        return simulatorPath;
    }

    /**
     * Generates location simulator path along level one and level two to Foot Locker
     */
    private List<MercatorZone> getLocationSimulatorPath_LevelOne_LevelTwo_ToFootLocker() {

        LatLng[] levelOneLatLngs = new LatLng[] {
                new LatLng(43.59270506141888,-79.64467918464558),
                new LatLng(43.59274299108304,-79.64407554834054),
                new LatLng(43.59284979290362,-79.6439460008228),
                new LatLng(43.59286875769334,-79.64355460194005),
                new LatLng(43.59291567056778,-79.64341265093671),
                new LatLng(43.59290777113583,-79.64330951843408),
        };

        LatLng[] levelTwoLatLngs = new LatLng[] {
                new LatLng(43.59290777113583,-79.64330951843408),
                new LatLng(43.59291305771916,-79.64321462730396),
                new LatLng(43.59354343449601,-79.64326847002535),
                new LatLng(43.59363085147197,-79.64315247246824),
                new LatLng(43.59367739812217,-79.6420787112964),
                new LatLng(43.59363085147197,-79.64207400869265),
        };

        List<MercatorZone> simulatorPath = new ArrayList<>();
        Arrays.stream(levelOneLatLngs).forEach(latLng -> simulatorPath.add(new MercatorZone(504, 504, 941, MapCalc.toMercator(latLng))));
        Arrays.stream(levelTwoLatLngs).forEach(latLng -> simulatorPath.add(new MercatorZone(504, 504, 942, MapCalc.toMercator(latLng))));

        return simulatorPath;
    }

    // endregion

    //region Geofences

    private void setupTestGeofenceSdk() {
        Logger.v("setupGeofenceSdk");

        // Create instance of GeofenceApi
        geofenceApi = MapstedGeofenceApi.newInstance(this, coreApi);

        // Create single geofence trigger (Poi Vicinity) for Entering POI vicinity
        geofenceApi.geofenceTriggers().addGeofenceTrigger(504,
                new GeofenceTrigger.Builder(504, "Enter_Urban_Planet_Vicinity")
                        .setLocationCriteria(new PoiVicinityLocationCriteria.Builder()
                                .addEntityZone(new EntityZone(504, 504, 941, 34)) // Urban Planet
                                .setActivationDistanceTh(10.0F)
                                .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_ENTER)
                                .build())
                        .build());

        // Create single geofence trigger (Poi Vicinity) for Exiting POI vicinity
        geofenceApi.geofenceTriggers().addGeofenceTrigger(504,
                new GeofenceTrigger.Builder(504, "Exit_Urban_Planet_Vicinity")
                        .setLocationCriteria(new PoiVicinityLocationCriteria.Builder()
                                .addEntityZone(new EntityZone(504, 504, 941, 34)) // Urban Planet
                                .setActivationDistanceTh(10.0F)
                                .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_EXIT)
                                .build())
                        .build());

        // Create a list of geofence triggers
        List<GeofenceTrigger> geofenceTriggers = new ArrayList<>();

        geofenceTriggers.add(new GeofenceTrigger.Builder(504, "Enter_SquareOne_Property")
                .setLocationCriteria(new PropertyLocationCriteria.Builder(504)
                        .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_ENTER)
                        .build())
                .build());

        geofenceTriggers.add(new GeofenceTrigger.Builder(504, "Enter_SquareOne_LevelTwo")
                .setLocationCriteria(new FloorLocationCriteria.Builder(942) // Level 2
                        .setTriggerDirection(ILocationCriteria.LocationTriggerDirection.ON_ENTER)
                        .build())
                .build());

        geofenceApi.geofenceTriggers().addGeofenceTriggers(504, geofenceTriggers);

        // Add callback listener
        geofenceApi.geofenceTriggers().addListener((propertyId, geofenceId) -> {

            // Unsubscribe after successfully triggering (so only triggers once)
            boolean unSubscribeSuccess = geofenceApi.geofenceTriggers().removeGeofenceTrigger(propertyId, geofenceId);

            String geofenceMsd = String.format(Locale.CANADA,
                    "GeofenceTriggered: pId: %d -> geofenceId: %s", propertyId, geofenceId);

            Logger.v(geofenceMsd);

            runOnUiThread(() -> Toast.makeText(getApplicationContext(), geofenceMsd, Toast.LENGTH_LONG).show());
        });
    }

    //endregion

    //region Lifecycle

    @Override
    public void onStart() {
        super.onStart();
        mapUiApi.customUi().setMapViewVisibility(View.VISIBLE);
        mBinding.setLifecycleOwner(this);
    }

    @Override
    public void onStop() {
        mBinding.setLifecycleOwner(null);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (coreApi != null) {
            coreApi.lifecycle().onResume();
        }

        if (mapApi != null) {
            mapApi.lifecycle().onResume();
        }

        if (mapUiApi != null) {
            mapUiApi.lifecycle().onResume();
        }
    }

    @Override
    public void onPause() {
        if (coreApi != null) {
            coreApi.lifecycle().onPause();
        }

        if (mapApi != null) {
            mapApi.lifecycle().onPause();
        }

        if (mapUiApi != null) {
            mapUiApi.lifecycle().onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Logger.v("onDestroy");
        mapUiApi.customUi().setMapViewVisibility(View.GONE);

        if (coreApi != null) {
            coreApi.lifecycle().onDestroy();
        }

        if (geofenceApi != null) {
            geofenceApi.onDestroy();
        }

        if (mapApi != null) {
            mapApi.lifecycle().onDestroy();
        }

        if (mapUiApi != null) {
            mapUiApi.lifecycle().onDestroy();
        }

        super.onDestroy();
    }

    //endregion

    //region Permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions,
                                           @Nullable int[] grantResults) {
        if (!mapUiApi.lifecycle().onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //endregion

    //region Providers

    @Override
    public CoreApi provideMapstedCoreApi() {
        return coreApi;
    }
    @Override
    public MapApi provideMapstedMapApi() {
        return mapApi;
    }
    @Override
    public MapUiApi provideMapstedUiApi() {
        return mapUiApi;
    }

    @NonNull
    @Override
    public SearchCoreSdkCallback getSearchCoreSdkCallback() {
        return new SearchCoreSdkCallback() {
            @Override
            public List<Category> getCategories(List<Integer> propertyIds) {
                return new ArrayList<>();
            }

            @Override
            public void openCategory(String categoryId, String categoryName, String query, int propertyId) {
                Toast.makeText(getApplicationContext(), "Open Category", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void openGlobalStorePropertyList(Poi poi, String text, String query, String analyticsSearchBarId) {
                Toast.makeText(getApplicationContext(), "Open Properties With this entity", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Nullable
    @Override
    public SearchFeedCallback getSearchFeedCallback() {
        return null;
    }

    @Nullable
    @Override
    public SearchAlertCallback getSearchAlertCallback() {
        return null;
    }

    //endregion

}
