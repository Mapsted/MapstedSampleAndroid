package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.corepositioning.cppObjects.swig.EntityZone;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapSelectionChangeListener;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.MessageType;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.positioning.coreObjects.SearchEntity;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.MapstedSdkController;
import com.mapsted.ui.search.SearchCallbacksProvider;


public class SampleMapWithUiToolsActivity extends AppCompatActivity implements MapstedMapUiApiProvider, SearchCallbacksProvider {
    private static final String TAG = SampleMapWithUiToolsActivity.class.getSimpleName();
    private FrameLayout fl_map_content;
    private FrameLayout fl_map_ui_tool;

    private MapUiApi sdk;
    private MapApi mapApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        fl_map_content = findViewById(R.id.my_map_container);
        fl_map_ui_tool = findViewById(R.id.my_map_ui_tool);
        sdk = MapstedSdkController.newInstance(getApplicationContext());
        mapApi = sdk.getMapApi();
        Params.initialize(this);
        setupMapstedSdk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!sdk.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomParams.newBuilder()
                .setBaseMapStyle(BaseMapStyle.GREY)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
//                .setEnablePropertyListSelection(true)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        sdk.initializeMapstedSDK(this, fl_map_ui_tool, fl_map_content, new MapstedInitCallback() {

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
                int propertyId = 504;
                mapApi.selectPropertyAndDrawIfNeeded(propertyId, new MapApi.DefaultSelectPropertyListener() {
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
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onMessage(MessageType messageType, String s) {
                Log.d(TAG, "onMessage: " + s);
            }
        });
    }

    private void selectAnEntityOnMap() {
        Log.d(TAG, "selectAnEntityOnMap: ");
        CoreApi coreApi = sdk.getMapApi().getCoreApi();
        Integer propertyId = mapApi.getSelectedPropertyId();
        Toast.makeText(this, "Selecting Gap store on map", Toast.LENGTH_LONG).show();

        coreApi.propertyManager().findEntityByName("Gap", propertyId, filteredResult -> {
            if (filteredResult.size() > 0) {
                SearchEntity searchEntity = filteredResult.get(0);
                //while it may have multiple entityZones if it spans multiple floors, we will select the first one.
                EntityZone entityZone = searchEntity.getEntityZones().get(0);
                coreApi.propertyManager().getEntity(entityZone, entity -> {
                    Log.d(TAG, "selectAnEntityOnMap: " + entity);
                    mapApi.addMapSelectionChangeListener(new MapSelectionChangeListener() {
                        @Override
                        public void onPropertySelectionChange(int propertyId, int previousPropertyId) {
                            Log.d(TAG, "onPropertySelectionChange: propertyId " + propertyId + ", previous " + previousPropertyId);
                        }

                        @Override
                        public void onBuildingSelectionChange(int propertyId, int buildingId, int previousBuildingId) {
                            Log.d(TAG, "onBuildingSelectionChange: propertyId " + propertyId + ", buildingId " + buildingId + ", previousBuildingId " + previousBuildingId);
                        }

                        @Override
                        public void onFloorSelectionChange(int buildingId, int floorId) {
                            Log.d(TAG, "onFloorSelectionChange: buildingId " + buildingId + ", floorId " + floorId);
                        }

                        @Override
                        public void onEntitySelectionChange(int entityId) {
                            Log.d(TAG, "onEntitySelectionChange: entityId " + entityId);
                        }
                    });
                    mapApi.selectEntity(entity);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        sdk.onDestroy();
        super.onDestroy();
    }

    @Override
    public MapUiApi provideMapstedUiApi() {
        if (sdk == null)
            sdk = MapstedSdkController.newInstance(getApplicationContext());
        return sdk;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(TAG, "::onBackPressed");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sdk.onConfigurationChanged(this, newConfig);
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