package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.mapsted.MapstedBaseApplication;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.SdkStatusUpdate;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.search.SearchCallbacksProvider;


public class SampleMapUiWithAButtonActivity extends AppCompatActivity
        implements MapstedMapUiApiProvider , SearchCallbacksProvider {
    private static final String TAG = SampleMapUiWithAButtonActivity.class.getSimpleName();

    private View rootView;
    private FrameLayout fl_base_map;
    private FrameLayout fl_map_ui;

    private CoreApi coreApi;
    private MapApi mapApi;
    private MapUiApi mapUiApi;
    private String myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        rootView = findViewById(R.id.rootView);
        fl_base_map = findViewById(R.id.fl_base_map);
        fl_map_ui = findViewById(R.id.fl_map_ui);

        coreApi = ((MapstedBaseApplication) getApplication()).getCoreApi(this);
        mapApi = MapstedMapApi.newInstance(this, coreApi);
        mapUiApi = MapstedMapUiApi.newInstance(this, mapApi);

        Params.initialize(this);
        setupMapstedSdk();
    }

    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomParams customParams = CustomParams.newBuilder(this, fl_base_map, fl_map_ui)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
                .setEnablePropertyListSelection(true)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        mapUiApi.setup().initialize(customParams, new MapUiApi.MapUiInitCallback() {
            @Override
            public void onCoreInitialized() {

            }

            @Override
            public void onMapInitialized() {

            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "::setupMapstedSdk ::onSuccess");
                String tag = "com.example.view.mybuttontag";
                View inflate = LayoutInflater.from(SampleMapUiWithAButtonActivity.this).inflate(R.layout.sample_button, null, false);
                inflate.findViewById(R.id.btn_hello).setOnClickListener(v -> {
                    String message = "Hello! You clicked " + ((Button) v).getText() + ".";
                    /*Log.d(TAG, "onClick: ");*/
                    Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
                });

                myTag = mapApi.mapView().customView().addViewToMapFragment(tag, inflate);
                Log.d(TAG, "onSuccess: myTag= " + myTag);
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.e(TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.toString());
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {
                Log.d(TAG, "sdkStatusUpdate: " + sdkStatusUpdate.toString());
            }
        });
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        mapApi.mapView().customView().removeViewFromMap(myTag);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mapUiApi.lifecycle().onDestroy();
        mapApi.lifecycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        if(mapUiApi != null && mapUiApi.lifecycle().onBackPressed()) {
            return;
        }

        super.onDestroy();
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mapUiApi.lifecycle().onConfigurationChanged(this, newConfig);
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