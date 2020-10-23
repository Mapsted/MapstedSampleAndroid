package com.mapsted.sample.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mapsted.sample.MyCategory;
import com.mapsted.sample.MyCategoryUtils;
import com.mapsted.sample.R;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.MapstedSdkController;
import com.mapsted.ui_components.list.MapstedListAdapter;
import com.mapsted.ui_components.list.MapstedListView;

import java.util.List;


public class SampleListActivity extends AppCompatActivity implements MapstedMapUiApiProvider {
    private static final String TAG = SampleListActivity.class.getSimpleName();
    private FrameLayout fl_map_content;

    private MapUiApi mapUiApi;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapUiApi = MapstedSdkController.newInstance(this);
        setContentView(R.layout.activity_sample_list);

        rootView = findViewById(R.id.rootView);

        MapstedListView mapstedListView = findViewById(R.id.mylist);
        List<MyCategory> items = MyCategoryUtils.createSomeItems();

        MapstedListAdapter adapter = new MapstedListAdapter(items, listener);
        mapstedListView.setListViewAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        mapUiApi.onDestroy();
        super.onDestroy();
    }

    private MapstedListAdapter.Listener<MyCategory> listener = new MapstedListAdapter.Listener<MyCategory>() {
        @Override
        public void onItemClicked(int position, View view, MyCategory item) {
            String message = "item clicked position=" + position + ", item=" + item.title;
            Log.d(TAG, message);
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public MapstedListAdapter.MapstedListViewHolder<MyCategory> getMapstedListViewHolder(ViewGroup parent, int viewType) {
            View itemLayout = LayoutInflater.from(SampleListActivity.this).inflate(R.layout.sample_category_item_layout, null, false);

            return new MapstedListAdapter.MapstedListViewHolder<MyCategory>(itemLayout) {
                @Override
                public void bind(MyCategory item) {
                    TextView textView = this.itemView.findViewById(R.id.tv_parent_category);
                    textView.setText(item.title);

                    ImageView imageView = this.itemView.findViewById(R.id.iv_parent_category);
                    Glide.with(getApplicationContext()).load(item.imageUrl).into(imageView);
                }
            };
        }
    };

    @Override
    public MapUiApi provideMapstedUiApi() {
        return mapUiApi;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mapUiApi.onConfigurationChanged(this, newConfig);
    }
}