package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.MapstedBaseApplication;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapSelectionChangeListener;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.SdkStatusUpdate;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.positioning.coreObjects.Entity;
import com.mapsted.positioning.coreObjects.EntityZone;
import com.mapsted.positioning.coreObjects.SearchEntity;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.search.SearchCallbacksProvider;


public class SampleMapWithUiToolsActivity extends AppCompatActivity implements MapstedMapUiApiProvider, SearchCallbacksProvider {
    private static final String TAG = SampleMapWithUiToolsActivity.class.getSimpleName();
    private FrameLayout fl_base_map;
    private FrameLayout fl_map_ui;

    private CoreApi coreApi;
    private MapApi mapApi;
    private MapUiApi mapUiApi;

    private int propertyId = 504;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        fl_base_map = findViewById(R.id.fl_base_map);
        fl_map_ui = findViewById(R.id.fl_map_ui);

        coreApi = ((MapstedBaseApplication)getApplication()).getCoreApi(this);
        mapApi = MapstedMapApi.newInstance(this, coreApi);
        mapUiApi = MapstedMapUiApi.newInstance(this, mapApi);

        Params.initialize(this);
        setupMapstedSdk();
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomParams customParams = CustomParams.newBuilder(this, fl_base_map, fl_map_ui)
                .setBaseMapStyle(BaseMapStyle.GREY)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
//                .setEnablePropertyListSelection(true)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        mapUiApi.setup().initialize(customParams, new MapUiApi.MapUiInitCallback() {
            @Override
            public void onCoreInitialized() {
                Log.d(TAG, "onCoreInitialized: ");
            }

            @Override
            public void onMapInitialized() {
                Log.d(TAG, "onMapInitialized: ");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                mapApi.data().selectPropertyAndDrawIfNeeded(propertyId, new MapApi.DefaultSelectPropertyListener() {
                    @Override
                    public void onPlotted(boolean isSuccess, int propertyId) {
                        Log.d(TAG, "onPlotted: propertyId=" + propertyId + " success=" + isSuccess);
                        super.onPlotted(isSuccess, propertyId);
                        selectAnEntityOnMap();
                    }
                });
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.d(TAG, "onFailure: " + sdkError.toString());
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {
                Log.d(TAG, "onStatusUpdate: " + sdkStatusUpdate.toString());
            }
        });
    }

    private void selectAnEntityOnMap() {

        Log.d(TAG, "selectAnEntityOnMap: ");
        Toast.makeText(this, "Selecting Gap store on map", Toast.LENGTH_LONG).show();

        coreApi.properties().findEntityByName("Gap", propertyId, filteredResult -> {
            if (filteredResult.size() > 0) {
                SearchEntity searchEntity = filteredResult.get(0);
                //while it may have multiple entityZones if it spans multiple floors, we will select the first one.
                EntityZone entityZone = searchEntity.getEntityZones().iterator().next();

                coreApi.properties().getEntity(entityZone, entity -> {
                    Log.d(TAG, "selectAnEntityOnMap: " + entity);
                    mapApi.data().addMapSelectionChangeListener(new MapSelectionChangeListener() {
                        @Override
                        public void onPropertySelectionChange(int propertyId, int previousPropertyId) {
                            Log.d(TAG, "onPropertySelectionChange: propertyId " + propertyId + ", previous " + previousPropertyId);
                        }

                        @Override
                        public void onBuildingSelectionChange(int propertyId, int buildingId, int previousBuildingId) {
                            Log.d(TAG, "onBuildingSelectionChange: propertyId " + propertyId + ", buildingId " + buildingId + ", previousBuildingId " + previousBuildingId);
                        }

                        @Override
                        public void onFloorSelectionChange(int propertyId, int buildingId, int floorId) {
                            Log.d(TAG, "onFloorSelectionChange: buildingId " + buildingId + ", floorId " + floorId);
                        }

                        @Override
                        public void onEntitySelectionChange(Entity entityId) {
                            Log.d(TAG, "onEntitySelectionChange: entityId " + entityId);
                        }
                    });

                    mapApi.data().selectEntity(entity);
                });
            }
        });
    }


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

    @Override
    public void onBackPressed() {

        if (mapUiApi != null && mapUiApi.lifecycle().onBackPressed()) {
            return;
        }

        super.onBackPressed();
        Log.i(TAG, "::onBackPressed");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mapUiApi.lifecycle().onConfigurationChanged(this, newConfig);
    }

    @Override
    public void onDestroy() {
        mapApi.lifecycle().onDestroy();
        mapUiApi.lifecycle().onDestroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public SearchCoreSdkCallback getSearchCoreSdkCallback() {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Nullable
    @Override
    public SearchFeedCallback getSearchFeedCallback() {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Nullable
    @Override
    public SearchAlertCallback getSearchAlertCallback() {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show();
        return null;
    }
}