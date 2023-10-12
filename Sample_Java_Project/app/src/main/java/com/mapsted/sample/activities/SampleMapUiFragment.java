package com.mapsted.sample.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mapsted.map.MapApi;
import com.mapsted.map.views.MapPanType;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.SdkStatusUpdate;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;

public class SampleMapUiFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private CoreApi coreApi;
    private MapApi mapApi;
    private MapUiApi mapUiApi;
    private String TAG = SampleMapUiFragment.class.getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof MapstedMapUiApiProvider apiProvider)) {
            throw new IllegalStateException(requireActivity().getClass() + " must implement " + MapstedMapUiApiProvider.class);
        }

        coreApi = apiProvider.provideMapstedCoreApi();
        mapApi = apiProvider.provideMapstedMapApi();
        mapUiApi = apiProvider.provideMapstedUiApi();
    }

    public static SampleMapUiFragment newInstance(String param1) {
        SampleMapUiFragment fragment = new SampleMapUiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ensure that Activity implements AppCompactActivity
        AppCompatActivity activity = (AppCompatActivity)requireActivity();

        FrameLayout fl_base_map = view.findViewById(R.id.fl_base_map);
        FrameLayout fl_map_ui = view.findViewById(R.id.fl_map_ui);

        CustomParams customParams = CustomParams.newBuilder(activity, fl_base_map, fl_map_ui)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
                .setEnablePropertyListSelection(true)
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
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.e(TAG, "onFailure: " + sdkError.toString());
            }

            @Override
            public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {
                Log.d(TAG, "sdkStatusUpdate: " + sdkStatusUpdate.toString());
            }
        });
    }
}
