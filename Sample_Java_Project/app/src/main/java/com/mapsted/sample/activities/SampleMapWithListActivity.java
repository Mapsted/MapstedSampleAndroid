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
import android.widget.Toast;

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
import com.mapsted.MapstedBaseApplication;
import com.mapsted.map.MapApi;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.SdkError;
import com.mapsted.positioning.SdkStatusUpdate;
import com.mapsted.positioning.core.network.property_metadata.model.Category;
import com.mapsted.sample.R;
import com.mapsted.ui.CustomParams;
import com.mapsted.ui.MapUiApi;
import com.mapsted.ui.MapstedMapUiApi;
import com.mapsted.ui.MapstedMapUiApiProvider;
import com.mapsted.ui.search.SearchCallbacksProvider;
import com.mapsted.ui_components.list.MapstedListAdapter;
import com.mapsted.ui_components.list.MapstedListView;

import java.util.List;
import java.util.function.Consumer;

public class SampleMapWithListActivity extends AppCompatActivity
        implements MapstedMapUiApiProvider, SearchCallbacksProvider {
    private static final String TAG = SampleMapWithListActivity.class.getSimpleName();
    private FrameLayout fl_base_map;
    private FrameLayout fl_map_ui;

    private CoreApi coreApi;
    private MapApi mapApi;
    private MapUiApi mapUiApi;
    private View rootView;
    private Context context;

    private String myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");

        context = this.getApplicationContext();

        coreApi = ((MapstedBaseApplication)getApplication()).getCoreApi(this);
        mapApi = MapstedMapApi.newInstance(this, coreApi);
        mapUiApi = MapstedMapUiApi.newInstance(this, mapApi);

        setContentView(R.layout.activity_sample_main);
        rootView = findViewById(R.id.rootView);

        fl_base_map = findViewById(R.id.fl_base_map);
        fl_map_ui = findViewById(R.id.fl_map_ui);

        setupMapstedSdk();
    }

    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        CustomParams customParams = CustomParams.newBuilder(this, fl_base_map, fl_map_ui)
                .setBaseMapStyle(BaseMapStyle.DARK)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        mapUiApi.setup().initialize(customParams, new MapUiApi.MapUiInitCallback() {
                    @Override
                    public void onCoreInitialized() {
                        Log.d(TAG, "onCoreInitialized: ");
                    }

                    @Override
                    public void onMapInitialized() {
                        Log.d(TAG, "onMapInitialized: ");
                        putListViewComponentOnTheMapstedMap();
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onFailure(SdkError sdkError) {
                        Log.d(TAG, "onFailure: sdkError: " + sdkError.toString());
                    }

                    @Override
                    public void onStatusUpdate(SdkStatusUpdate sdkStatusUpdate) {
                        Log.d(TAG, "onStatusUpdate: " + sdkStatusUpdate.toString());
                    }
                });

    }

    @Override
    protected void onStop() {
        mapApi.mapView().customView().removeViewFromMap(myTag);
        super.onStop();
    }

    void putListViewComponentOnTheMapstedMap() {

        int propertyId = 504;

        mapApi.data().selectPropertyAndDrawIfNeeded(propertyId, new MapApi.DefaultSelectPropertyListener() {
            @Override
            public void onPlotted(boolean isSuccess, int propertyId) {

                if (isSuccess) {
                    // Create custom layout
                    View inflate = LayoutInflater.from(context).inflate(R.layout.sample_layout, null, false);
                    MapstedListView mapstedListView = inflate.findViewById(R.id.mylist);

                    // Set Data
                    coreApi.properties().getCategories(propertyId, categoriesResult -> {
                        List<Category> categories = categoriesResult.getRootCategories();

                        // Create adapter and set layout manager
                        MapstedListAdapter<Category> adapter = new MapstedListAdapter<>(categories, listener);
                        mapstedListView.setListViewAdapter(adapter);
                        String tag = "com.example.view.mylistviewtag";

                        myTag = mapApi.mapView().customView().addViewToMapFragment(tag, inflate);
                    });
                }

                super.onPlotted(isSuccess, propertyId);
            }
        });
    }

    private final MapstedListAdapter.Listener<Category> listener = new MapstedListAdapter.Listener<>() {
        @Override
        public void onItemClicked(int position, View view, Category item) {
            String message = "item clicked position=" + position + ", item=" + item.getName();
            /*Log.d(TAG, message);*/
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public MapstedListAdapter.MapstedListViewHolder<Category> getMapstedListViewHolder(ViewGroup parent, int viewType) {
            View itemLayout = LayoutInflater.from(context).inflate(R.layout.sample_category_item_layout, null, false);

            return new MapstedListAdapter.MapstedListViewHolder<>(itemLayout) {
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
        CustomViewTarget<ImageView, Drawable> target = new CustomViewTarget<>(ivCategoryImage) {
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

        String imageUrl = parentCategory.getImageUrl();
        Log.d(TAG, "setParentCategory: imageUrl=" + imageUrl);
        Glide.with(ivCategoryImage)
                .load(imageUrl)
                .placeholder(R.drawable.ic_category_shopping_active)
                .transform(new MultiTransformation<>(new RoundedCorners(8), new FitCenter()))
                .into(target);
    }

    private void fetchColorFromResource(Drawable drawable) {
        if (drawable instanceof BitmapDrawable bitmapDrawable) {
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
        mapUiApi.lifecycle().onDestroy();
        mapApi.lifecycle().onDestroy();
        super.onDestroy();
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