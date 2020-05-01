package com.mapsted.sample.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import sample.mapsted.com.R;
import com.mapsted.SdkError;
import com.mapsted.corepositioning.cppObjects.swig.Category;
import com.mapsted.map.MapstedMapApi;
import com.mapsted.map.models.interfaces.OnSetSelectedPropertyListener;
import com.mapsted.map.models.layers.BaseMapStyle;
import com.mapsted.map.views.MapPanType;
import com.mapsted.map.views.MapstedMapRange;
import com.mapsted.positioning.MapstedInitCallback;
import com.mapsted.positioning.core.utils.common.Params;
import com.mapsted.sample.MyCategoryUtils;
import com.mapsted.ui.map.processing.CustomParams;
import com.mapsted.ui.map.processing.MapstedSdkController;
import com.mapsted.ui_components.list.MapstedListAdapter;
import com.mapsted.ui_components.list.MapstedListView;

import java.util.List;


public class SampleMapWithListActivity extends AppCompatActivity {
    private static final String TAG = SampleMapWithListActivity.class.getSimpleName();
    private FrameLayout fl_map_content;

    private MapstedSdkController sdkController = MapstedSdkController.getInstance();
    private View rootView;
    private Context context;

    private int dubaiMallPropertyId = 592;
    private int soukPropertyId = 600;
    private String myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "::onCreate");

        context = this.getApplicationContext();

        setContentView(R.layout.activity_sample_main);
        rootView = findViewById(R.id.rootView);
        fl_map_content = findViewById(R.id.my_map_container);
        setupMapstedSdk();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!MapstedSdkController.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void setupMapstedSdk() {
        Log.i(TAG, "::setupMapstedSdk");

        // TODO: This needs to be in the app itself not in app template...
        CustomParams.newBuilder()
                .setBaseMapStyle(BaseMapStyle.DEFAULT)
                .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
                .setMapZoomRange(new MapstedMapRange(6.0f, 24.0f))
                .build();

        sdkController.initializeMapstedSDK(this, fl_map_content, fl_map_content, new MapstedInitCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "::setupMapstedSdk ::onSuccess");

                MapstedMapApi.selectPropertyAndDrawIfNeeded(dubaiMallPropertyId, new OnSetSelectedPropertyListener() {
                    @Override
                    public void onSetSelectedProperty(boolean isSuccessful) {
                        Log.i(TAG, "::SelectedProperty " + (isSuccessful ? "SUCCESSFUL" : "FAILED"));
                    }
                });

                putListViewComponentOnTheMapstedMap();
            }

            @Override
            public void onFailure(SdkError sdkError) {
                Log.e(TAG, "::setupMapstedSdk ::onFailure message=" + sdkError.errorMessage);
            }

        });

    }

    @Override
    protected void onStop() {
        sdkController.removeViewFromMap(myTag);
        super.onStop();
    }

    void putListViewComponentOnTheMapstedMap() {
        // Create custom layout
        View inflate = LayoutInflater.from(this).inflate(R.layout.sample_layout, null, false);
        MapstedListView mapstedListView = (MapstedListView) inflate.findViewById(R.id.mylist);

        // Set Data
        List<Category> items = MyCategoryUtils.createCategoryList();

        // Create adapter and set layout manager
        MapstedListAdapter adapter = new MapstedListAdapter(items, listener);
        mapstedListView.setListViewAdapter(adapter);
        String tag = "com.example.view.mytag";
        myTag = sdkController.addViewToMap(tag, inflate);
        sdkController.setMapViewVisibility(View.VISIBLE);
    }

    private MapstedListAdapter.Listener<Category> listener = new MapstedListAdapter.Listener<Category>() {
        @Override
        public void onItemClicked(int position, View view, Category item) {
            String message = "item clicked position=" + position + ", item=" + item.getName();
            /*Log.d(TAG, message);*/
            Snackbar.make(rootView, message, BaseTransientBottomBar.LENGTH_SHORT).show();
        }

        @Override
        public MapstedListAdapter.MapstedListViewHolder getMapstedListViewHolder() {

            View itemLayout = LayoutInflater.from(context).inflate(R.layout.sample_category_item_layout, null, false);

            return new MapstedListAdapter.MapstedListViewHolder<Category>(itemLayout) {
                @Override
                public void bind(Category item) {
                    if (item == null) return;

                    // Set item width (Max 5)
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int categoryItemWidth = (int) (metrics.widthPixels) / 5;
                    this.itemView.setLayoutParams(new RecyclerView.LayoutParams(categoryItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView textView = this.itemView.findViewById(R.id.tv_parent_category);
                    textView.setText(item.getName());

                    ImageView imageView = this.itemView.findViewById(R.id.iv_parent_category);
                    setParentCategory(item, imageView);
                    //Glide.with(getApplicationContext()).load(item.getImageGuid()).into(imageView);

                    Log.d(TAG, "Guid: " + Params.imageUrl + item.getImageGuid());

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
        String imageUrl = Params.imageUrl + parentCategory.getImageGuid();
        Log.d(TAG, "setParentCategory: imageUrl=" + imageUrl);
        Glide.with(ivCategoryImage)
                .load(imageUrl)
                .placeholder(com.mapsted.apptemplate.R.drawable.ic_category_shopping_active)
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
        sdkController.onDestroy();
        super.onDestroy();
    }
}