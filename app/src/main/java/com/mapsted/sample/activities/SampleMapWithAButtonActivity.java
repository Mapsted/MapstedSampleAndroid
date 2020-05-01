package com.mapsted.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mapsted.SdkError;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.interfaces.OnSetSelectedPropertyListener;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.sample.R;
import com.mapsted.ui.map.processing.CustomParams;
import com.mapsted.ui.map.processing.MapstedSdkController;

public class SampleMapWithAButtonActivity extends AppCompatActivity {

    private static final String TAG = SampleMapWithAButtonActivity.class.getSimpleName();
    private View rootView;
    private FrameLayout fl_map_content;
    private FrameLayout fl_map_ui_tool;


    private MapstedSdkController sdkController;

    private int dubaiMallPropertyId = 592;
    private int soukPropertyId = 600;
    private String myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        rootView = findViewById(R.id.rootView);
        fl_map_content = findViewById(R.id.my_map_container);
        fl_map_ui_tool = findViewById(R.id.my_map_ui_tool);
        sdkController = MapstedSdkController.getInstance();
        Params.initialize(this);
        setupMapstedSdk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: ");
        if (!MapstedSdkController.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        // TODO: This needs to be in the app itself not in app template...
        CustomParams.newBuilder()
                .setBaseMapStyle(BaseMapStyle.DARK)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        sdkController.initializeMapstedSDK(this, fl_map_ui_tool, fl_map_content, new MapstedInitCallback() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "::setupMapstedSdk ::onSuccess");
                String tag = "com.example.view.mybuttontag";
                View inflate = LayoutInflater.from(SampleMapWithAButtonActivity.this).inflate(R.layout.sample_button, null, false);
                inflate.findViewById(R.id.btn_hello).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = "Hello! You clicked " + ((Button) v).getText() + ".";
                        /*Log.d(TAG, "onClick: ");*/
                        Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });
                myTag = sdkController.addViewToMap(tag, inflate);
                Log.d(TAG, "onSuccess: myTag= " + myTag);

                MapstedMapApi.selectPropertyAndDrawIfNeeded(dubaiMallPropertyId, new OnSetSelectedPropertyListener() {
                    @Override
                    public void onSetSelectedProperty(boolean isSuccessful) {
                        Log.i(TAG, "::selectPropertyAndDrawIfNeeded onSetSelectedProperty " + (isSuccessful ? "SUCCESSFUL" : "FAILED"));
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
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        sdkController.removeViewFromMap(myTag);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        sdkController.onDestroy();
        super.onDestroy();
    }
}