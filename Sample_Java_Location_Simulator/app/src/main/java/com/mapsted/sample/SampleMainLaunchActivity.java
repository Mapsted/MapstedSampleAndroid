package com.mapsted.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SampleMainLaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_main_launch);
        SampleMainLaunchActivity context = this;

        findViewById(R.id.btn_launch_loc_simulator).setOnClickListener(v -> {
            Intent intent = new Intent(context, LocationSimulatorRoutingAndGeofenceActivity.class);
            context.startActivity(intent);
        });
    }
}
