package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mapsted.sample.R;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.MapstedSdkController;
import com.mapsted.ui.search.SearchCallbacksProvider;

public class SampleActivityWithFragment extends AppCompatActivity implements MapstedMapUiApiProvider, SearchCallbacksProvider {

    private MapUiApi mapUiApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_with_fragment);
        Fragment fragment = SampleMapUiFragment.newInstance("someparam");
        mapUiApi = MapstedSdkController.newInstance(this);
        String tag = SampleMapUiFragment.class.getName();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!mapUiApi.onRequestPermissionsResult(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Nullable
    @Override
    public SearchCoreSdkCallback getSearchCoreSdkCallback() {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Nullable
    @Override
    public SearchFeedCallback getSearchFeedCallback() {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Nullable
    @Override
    public SearchAlertCallback getSearchAlertCallback() {
        Toast.makeText(this, "Not implemented in sample", Toast.LENGTH_SHORT).show();
        return null;
    }
}