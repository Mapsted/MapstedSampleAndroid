package com.mapsted.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.corepositioning.cppObjects.swig.CppRouteResponse;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.MapInitializationDetails;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.CustomMapParams;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MessageType;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.positioning.coreObjects.ISearchable;
import com.mapsted.positioning.coreObjects.RouteRequest;
import com.mapsted.positioning.coreObjects.RoutingRequestCallback;
import com.mapsted.positioning.coreObjects.RoutingResponse;
import com.mapsted.positioning.coreObjects.Waypoint;
import com.mapsted.positioning.coreObjects.WaypointHelper;
import com.mapsted.sample.R;
import com.mapsted.ui.map.routing.preview.RoutePreviewFragment;

import java.util.List;


public class SampleMapActivity extends AppCompatActivity {
    private static final String TAG = SampleMapActivity.class.getSimpleName();

    private static final int PROPERTY_ID = 504; //Square One Mall Property

    private FrameLayout flMapContainer;

    private MapApi mapApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");
        setContentView(R.layout.activity_sample_main);
        flMapContainer = findViewById(R.id.my_map_container);
        mapApi = MapstedMapApi.newInstance(this);
        Params.initialize(this);
        setupMapstedSdk();
    }

    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomMapParams.newBuilder()
                .setBaseMapStyle(BaseMapStyle.GREY)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        MapInitializationDetails mapInitDetail = new MapInitializationDetails(this, flMapContainer);

        mapApi.initializeMapstedMapApi(mapInitDetail, new MapApi.MapInitCallback() {
            @Override
            public void onFailure(SdkError sdkError) {
                //this api (map api) failed to initialize
                Log.w(TAG, "onFailure: " + sdkError);
            }

            @Override
            public void onSuccess() {
                //this api (map api) is ready
                Log.d(TAG, "onSuccess: ");
                mapApi.selectPropertyAndDrawIfNeeded(PROPERTY_ID, new MapApi.DefaultSelectPropertyListener() {
                    @Override
                    public void onPlotted(boolean isSuccess, int propertyId) {
                        super.onPlotted(isSuccess, propertyId);
                        Log.d(TAG, "onPlotted: isSuccess: " + isSuccess + ", propertyId: " + propertyId);
                    }
                });
                showRouting();
            }

            @Override
            public void onCoreInitiated() {
                //core api is ready
                Log.d(TAG, "onCoreInitiated: ");
            }

            @Override
            public void onMessage(MessageType messageType, String s) {
                Log.d(TAG, "onMessage: " + s);
            }
        });
    }

    private void showRouting() {
        ISearchable destinationSearchable;
        Waypoint waypoint = WaypointHelper.from(destinationSearchable);
        RouteRequest routeRequest = new RouteRequest.Builder().addDestinationWaypoint(waypoint).build();
        mapApi.requestRouting(routeRequest, new RoutingRequestCallback() {
            @Override
            public void onSuccess(RoutingResponse routeResponse) {
                showRoutePreview(routeResponse);
            }


            @Override
            public void onError(CppRouteResponse.ErrorType errorType, String error, List<String> alertIds) {

            }
        });
    }

    private void showRoutePreview(RoutingResponse routeResponse) {
        RoutePreviewFragment routePreviewFragment = RoutePreviewFragment.newInstance(routeResponse, new RoutePreviewFragment.Listener() {
            @Override
            public void onRoutePreviewCreated() {

            }

            @Override
            public void onRoutePreviewDestroyed() {

            }
        });
        getSupportFragmentManager().beginTransaction().add(containerId, routePreviewFragment).commit();
    }

    @Override
    protected void onDestroy() {
        mapApi.onDestroy();
        super.onDestroy();
    }
}