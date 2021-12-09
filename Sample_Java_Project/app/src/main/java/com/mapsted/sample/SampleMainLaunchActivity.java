package com.mapsted.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.sample.activities.SampleActivityWithFragment;
import com.mapsted.sample.activities.SampleMapActivity;
import com.mapsted.sample.activities.SampleMapUiWithAButtonActivity;
import com.mapsted.sample.activities.SampleMapWithListActivity;
import com.mapsted.sample.activities.SampleMapWithUiToolsActivity;


public class SampleMainLaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_main_launch);
        SampleMainLaunchActivity context = this;
        findViewById(R.id.btn_launch_map_with_tools).setOnClickListener(v -> {
            Intent intent = new Intent(context, SampleMapWithUiToolsActivity.class);
            context.startActivity(intent);
        });


        findViewById(R.id.btn_launch_map_with_button).setOnClickListener(v -> {
            Intent intent = new Intent(context, SampleMapUiWithAButtonActivity.class);
            context.startActivity(intent);
        });

        findViewById(R.id.btn_launch_map_with_list).setOnClickListener(v -> {
            Intent intent = new Intent(context, SampleMapWithListActivity.class);
            context.startActivity(intent);
        });

        findViewById(R.id.btn_launch_activity_with_fragment).setOnClickListener(v -> {
            Intent intent = new Intent(context, SampleActivityWithFragment.class);
            context.startActivity(intent);
        });

        findViewById(R.id.btn_launch_map).setOnClickListener(v -> {
            Intent intent = new Intent(context, SampleMapActivity.class);
            context.startActivity(intent);
        });
    }
}
