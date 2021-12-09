package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.mapsted.map.MapApi;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.MapstedSdkController;


public class SampleMapUiWithAButtonActivity extends AppCompatActivity implements MapstedMapUiApiProvider {

    private static final String TAG = SampleMapUiWithAButtonActivity.class.getSimpleName();
    private View rootView;
    private FrameLayout fl_map_content;
    private FrameLayout fl_map_ui_tool;


    private MapUiApi mapUiApi;
    private String myTag;
    private MapApi mapApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        rootView = findViewById(R.id.rootView);
        fl_map_content = findViewById(R.id.my_map_container);
        fl_map_ui_tool = findViewById(R.id.my_map_ui_tool);
        mapUiApi = MapstedSdkController.newInstance(getApplicationContext());
        mapApi = mapUiApi.getMapApi();
        Params.initialize(this);
        setupMapstedSdk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        if (!mapUiApi.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomParams.newBuilder()
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
                .setEnablePropertyListSelection(true)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        mapUiApi.initializeMapstedSDK(this, fl_map_ui_tool, fl_map_content, new MapstedInitCallback() {

            @Override
            public void onCoreInitialized() {
                //core initialized
            }

            @Override
            public void onMapInitialized() {
                //mapInitialized
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "::setupMapstedSdk ::onSuccess");
                String tag = "com.example.view.mybuttontag";
                View inflate = LayoutInflater.from(SampleMapUiWithAButtonActivity.this).inflate(R.layout.sample_button, null, false);
                inflate.findViewById(R.id.btn_hello).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = "Hello! You clicked " + ((Button) v).getText() + ".";
                        /*Log.d(TAG, "onClick: ");*/
                        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
                    }
                });
                myTag = mapUiApi.addViewToMap(tag, inflate);
                Log.d(TAG, "onSuccess: myTag= " + myTag);
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.e(TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage);
            }
        });
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        mapUiApi.removeViewFromMap(myTag);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        mapUiApi.onDestroy();
        super.onDestroy();
    }

    @Override
    public MapUiApi provideMapstedUiApi() {
        return mapUiApi;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mapUiApi.onConfigurationChanged(this, newConfig);
    }
}