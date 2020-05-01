package com.mapsted.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import sample.mapsted.com.R;
import com.mapsted.SdkError;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.interfaces.OnSetSelectedPropertyListener;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.ui.map.processing.CustomParams;
import com.mapsted.ui.map.processing.MapstedSdkController;


public class SampleMapWithUiToolsActivity extends AppCompatActivity {
    private static final String TAG = SampleMapWithUiToolsActivity.class.getSimpleName();
    private FrameLayout fl_map_content;
    private FrameLayout fl_map_ui_tool;

    private MapstedSdkController sdkController;


    private int dubaiMallPropertyId = 592;
    private int soukPropertyId = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        fl_map_content = findViewById(R.id.my_map_container);
        fl_map_ui_tool = findViewById(R.id.my_map_ui_tool);
        sdkController = MapstedSdkController.getInstance();
        Params.initialize(this);
        setupMapstedSdk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!MapstedSdkController.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomParams.newBuilder()
                .setBaseMapStyle(BaseMapStyle.DEFAULT)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();


        sdkController.initializeMapstedSDK(this, fl_map_ui_tool, fl_map_content, new MapstedInitCallback() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "::setupMapstedSdk ::onSuccess");

                MapstedMapApi.selectPropertyAndDrawIfNeeded(dubaiMallPropertyId, new OnSetSelectedPropertyListener() {
                    @Override
                    public void onSetSelectedProperty(boolean isSuccessful) {
                        Log.i(TAG, "::SelectedProperty " + (isSuccessful ? "SUCCESSFUL" : "FAILED"));
                    }
                });
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.e(TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage);
            }

        });
    }

    @Override
    protected void onDestroy() {
        sdkController.onDestroy();
        super.onDestroy();
    }
}