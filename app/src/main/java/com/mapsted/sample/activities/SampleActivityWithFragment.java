package com.mapsted.sample.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mapsted.ui.map.processing.MapstedSdkController;

import sample.mapsted.com.R;

public class SampleActivityWithFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_with_fragment);
        Fragment fragment = SampleFragment.newInstance("someparam");
        String tag = SampleFragment.class.getName();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!MapstedSdkController.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
