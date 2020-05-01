package com.mapsted.sample.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import sample.mapsted.com.R;

import com.mapsted.sample.MyCategory;
import com.mapsted.sample.MyCategoryUtils;
import com.mapsted.ui.map.processing.MapstedSdkController;
import com.mapsted.ui_components.list.MapstedListAdapter;
import com.mapsted.ui_components.list.MapstedListView;

import java.util.List;


public class SampleListActivity extends AppCompatActivity {
    private static final String TAG = SampleListActivity.class.getSimpleName();
    private FrameLayout fl_map_content;

    private MapstedSdkController sdkController = MapstedSdkController.getInstance();
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        rootView = findViewById(R.id.rootView);

        MapstedListView mapstedListView = findViewById(R.id.mylist);
        List<MyCategory> items = MyCategoryUtils.createSomeItems();

        MapstedListAdapter adapter = new MapstedListAdapter(items, listener);
        mapstedListView.setListViewAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        sdkController.onDestroy();
        super.onDestroy();
    }

    private MapstedListAdapter.Listener<MyCategory> listener = new MapstedListAdapter.Listener<MyCategory>() {
        @Override
        public void onItemClicked(int position, View view, MyCategory item) {
            String message = "item clicked position=" + position + ", item=" + item.title;
            Log.d(TAG, message);
            Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_SHORT).show();
        }

        @Override
        public MapstedListAdapter.MapstedListViewHolder<MyCategory> getMapstedListViewHolder() {

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


}