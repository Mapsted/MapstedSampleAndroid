package com.mapsted.sample.activities;

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

import sample.mapsted.com.R;
import com.mapsted.SdkError;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.ui.map.processing.MapstedSdkController;

public class SampleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";


    private String mParam1;
    private String mParam2;
    private String TAG = SampleFragment.class.getSimpleName();


    public SampleFragment() {
        // Required empty public constructor
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
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

        MapstedSdkController.getInstance().initializeMapstedSDK((AppCompatActivity) getActivity(), mapContainerView, mapstedInitCallback);

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
        MapstedSdkController.getInstance().onDestroy();
        super.onDestroy();
    }
}
