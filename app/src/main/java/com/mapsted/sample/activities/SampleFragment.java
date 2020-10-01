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

import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.SdkError;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;

public class SampleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private MapUiApi sdk;
    private String TAG = SampleFragment.class.getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MapstedMapUiApiProvider  apiProvider = (MapstedMapUiApiProvider)context;
        sdk = apiProvider.provideMapstedUiApi();
    }

    public static SampleFragment newInstance(String param1) {
        SampleFragment fragment = new SampleFragment();
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
        FrameLayout mapContainerView = view.findViewById(R.id.map_container);
        CustomParams.newBuilder()
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setShowPropertyListOnMapLaunch(true)
                .setEnablePropertyListSelection(true)
                .build();
        sdk.initializeMapstedSDK((AppCompatActivity) getActivity(), mapContainerView, mapstedInitCallback);

    }

    private MapstedInitCallback mapstedInitCallback = new MapstedInitCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess: ");
        }

        @Override
        public void onFailure(SdkError sdkError) {
            Log.e(TAG, "onFailure: " + sdkError.errorMessage);
        }

    };

    @Override
    public void onDestroy() {
        sdk.onDestroy();
        super.onDestroy();
    }
}
