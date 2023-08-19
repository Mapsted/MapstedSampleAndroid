package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.mapsted.MapstedBaseApplication;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.positioning.CoreApi;
import com.mapsted.sample.R;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.search.SearchCallbacksProvider;

public class SampleActivityWithFragment extends AppCompatActivity implements MapstedMapUiApiProvider, SearchCallbacksProvider {

    private CoreApi coreApi;
    private MapApi mapApi;
    private MapUiApi mapUiApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_with_fragment);

        // Create Mapsted Api instances
        coreApi = ((MapstedBaseApplication)getApplication()).getCoreApi(this);
        mapApi = MapstedMapApi.newInstance(this, coreApi);
        mapUiApi = MapstedMapUiApi.newInstance(this, mapApi);

        Fragment fragment = SampleMapUiFragment.newInstance("someparam");
        String tag = SampleMapUiFragment.class.getName();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!mapUiApi.lifecycle().onRequestPermissionsResult(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public CoreApi provideMapstedCoreApi() {
        return coreApi;
    }
    @Override
    public MapApi provideMapstedMapApi() {
        return mapApi;
    }
    @Override
    public MapUiApi provideMapstedUiApi() {
        return mapUiApi;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mapUiApi.lifecycle().onConfigurationChanged(this, newConfig);
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