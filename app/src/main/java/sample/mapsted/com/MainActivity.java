package sample.mapsted.com;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mapsted.map.MapstedMapApi;
import com.mapsted.positioning.positioning.MapstedPositioningApi;
import com.mapsted.ui.map.MapstedMapActivity;
import com.mapsted.ui.map.processing.MapstedSdkController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Optional: Pre-download property data
        MapstedMapApi.setPrefetchMapData(true);

        // Intent to start Mapsted All-in-one
        Intent intent = new Intent(MainActivity.this, MapstedMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        MapstedSdkController.getInstance().unBindForegroundService();
        System.exit(1);
        super.onDestroy();
    }
}
