package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.map.MapApi;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.core.utils.Logger;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.MapstedSdkController;


public class SampleMapWithUiToolsActivity extends AppCompatActivity implements MapstedMapUiApiProvider {
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
                .setBaseMapStyle(BaseMapStyle.DEFAULT)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
                .setEnablePropertyListSelection(true)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();


        sdk.initializeMapstedSDK(this, fl_map_ui_tool, fl_map_content, new MapstedInitCallback() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "::setupMapstedSdk ::onSuccess");
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.e(TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage);
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
        return sdk;
    }

    @Override
    public void onBackPressed() {
        Logger.d("onBackPressed: ");
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sdk.onConfigurationChanged(this, newConfig);
    }
}