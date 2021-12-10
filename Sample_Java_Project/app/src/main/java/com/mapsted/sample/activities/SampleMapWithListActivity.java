package com.mapsted.sample.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.MessageType;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.core.network.property_metadata.model.Category;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.MapstedSdkController;
import com.mapsted.ui_components.list.MapstedListAdapter;


public class SampleMapWithListActivity extends AppCompatActivity implements MapstedMapUiApiProvider {
    private static final String TAG = SampleMapWithListActivity.class.getSimpleName();
    private FrameLayout fl_map_content;
    private FrameLayout fl_map_ui_tool;

    private MapUiApi mapUiApi;
    private View rootView;
    private Context context;

    private String myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");

        context = this.getApplicationContext();
        mapUiApi = MapstedSdkController.newInstance(context);

        setContentView(R.layout.activity_sample_main);
        rootView = findViewById(R.id.rootView);
        fl_map_content = findViewById(R.id.my_map_container);
        fl_map_ui_tool = findViewById(R.id.my_map_ui_tool);
        setupMapstedSdk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!mapUiApi.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        // TODO: This needs to be in the app itself not in app template...
        CustomParams.newBuilder()
                .setBaseMapStyle(BaseMapStyle.DARK)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        mapUiApi.initializeMapstedSDK(this, fl_map_ui_tool, fl_map_content, new MapstedInitCallback() {
            @Override
            public void onCoreInitialized() {
                Log.d(TAG, "onCoreInitialized: ");
            }

            @Override
            public void onMapInitialized() {
                Log.d(TAG, "onMapInitialized: ");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.d(TAG, "onFailure: "+sdkError);
            }

            @Override
            public void onMessage(MessageType messageType, String s) {
                Log.d(TAG, "onMessage: " + s);
            }

        });

    }

    @Override
    protected void onStop() {
        mapUiApi.removeViewFromMap(myTag);
        super.onStop();
    }

//    void putListViewComponentOnTheMapstedMap() {
//        // Create custom layout
//        View inflate = LayoutInflater.from(this).inflate(R.layout.sample_layout, null, false);
//        MapstedListView mapstedListView = (MapstedListView) inflate.findViewById(R.id.mylist);
//
//        // Set Data
//        List<Category> items = MyCategoryUtils.createCategoryList(mapUiApi);
//
//        // Create adapter and set layout manager
//        MapstedListAdapter<Category> adapter = new MapstedListAdapter(items, listener);
//        mapstedListView.setListViewAdapter(adapter);
//        String tag = "com.example.view.mylistviewtag";
//        myTag = mapUiApi.addViewToMap(tag, inflate);
//    }

    private MapstedListAdapter.Listener<Category> listener = new MapstedListAdapter.Listener<Category>() {
        @Override
        public void onItemClicked(int position, View view, Category item) {
            String message = "item clicked position=" + position + ", item=" + item.getName();
            /*Log.d(TAG, message);*/
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public MapstedListAdapter.MapstedListViewHolder<Category> getMapstedListViewHolder(ViewGroup parent, int viewType) {
            View itemLayout = LayoutInflater.from(context).inflate(R.layout.sample_category_item_layout, null, false);

            return new MapstedListAdapter.MapstedListViewHolder<Category>(itemLayout) {
                @Override
                public void bind(Category item) {
                    if (item == null) return;
                    TextView textView = this.itemView.findViewById(R.id.tv_parent_category);
                    textView.setText(item.getName());

                    ImageView imageView = this.itemView.findViewById(R.id.iv_parent_category);
                    setParentCategory(item, imageView);
                    //Glide.with(getApplicationContext()).load(item.getImageGuid()).into(imageView);


                }
            };
        }
    };

    public void setParentCategory(Category parentCategory, ImageView ivCategoryImage) {
        CustomViewTarget<ImageView, Drawable> target = new CustomViewTarget<ImageView, Drawable>(ivCategoryImage) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {

            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                fetchColorFromResource(resource);
                ivCategoryImage.setImageDrawable(resource);
            }
        };
        String imageUrl = "http://www.example.com/" + parentCategory.getImageGuid();
        Log.d(TAG, "setParentCategory: imageUrl=" + imageUrl);
        Glide.with(ivCategoryImage)
                .load(imageUrl)
                .placeholder(R.drawable.ic_category_shopping_active)
                .transform(new MultiTransformation<>(new RoundedCorners(8), new FitCenter()))
                .into(target);
    }

    private void fetchColorFromResource(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Palette palette = Palette.from(bitmap).generate();
            Palette.Swatch vibrantSwatch = palette.getDominantSwatch();
            if (vibrantSwatch != null) {
                int rgbInt = vibrantSwatch.getRgb();
                int red = Color.red(rgbInt);
                int green = Color.green(rgbInt);
                int blue = Color.blue(rgbInt);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mapUiApi.onDestroy();
        super.onDestroy();
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
}