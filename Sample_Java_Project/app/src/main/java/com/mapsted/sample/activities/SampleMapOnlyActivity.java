package com.mapsted.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.MapstedBaseApplication;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.CustomMapParams;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.SdkStatusUpdate;
import com.mapsted.sample.R;

public class SampleMapOnlyActivity extends AppCompatActivity {
    private static final String TAG = SampleMapOnlyActivity.class.getSimpleName();

    private static final int PROPERTY_ID = 504; //Square One Mall Property

    private FrameLayout flMapContainer;

    private CoreApi coreApi;
    private MapApi mapApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_map_only);
        flMapContainer = findViewById(R.id.map_container);

        coreApi = ((MapstedBaseApplication)getApplication()).getCoreApi(this);
        mapApi = MapstedMapApi.newInstance(this, coreApi);

        setupMapstedSdk();
    }

    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomMapParams params = CustomMapParams.newBuilder(this, flMapContainer)
                .setBaseMapStyle(BaseMapStyle.GREY)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        mapApi.setup().initialize(params, new MapApi.MapInitCallback() {
            @Override
            public void onCoreInitiated() {
                //core api is ready
                Log.d(TAG, "onCoreInitiated: ");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                mapApi.data().selectPropertyAndDrawIfNeeded(PROPERTY_ID, new MapApi.DefaultSelectPropertyListener() {
                    @Override
                    public void onPlotted(boolean isSuccess, int propertyId) {
                        super.onPlotted(isSuccess, propertyId);
                        Log.d(TAG, "onPlotted: isSuccess: " + isSuccess + ", propertyId: " + propertyId);
                    }
                });
            }
            @Override
            public void onFailure(SdkError sdkError) {
                //this api (map api) failed to initialize
                Log.w(TAG, "onFailure: " + sdkError);
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {

            }
        });
    }

    @Override
    protected void onDestroy() {

        if (mapApi != null) {
            mapApi.lifecycle().onDestroy();
        }

        super.onDestroy();
    }
}