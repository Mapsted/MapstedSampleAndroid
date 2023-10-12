package com.mapsted.sample;

import android.util.Log;

import com.mapsted.MapstedBaseApplication;

/**
 * Extending MapstedBaseApplication allows for getting access to shared coreApi instances
 * This allows coreApi to be shared across multiple Activities or Fragments
 * which provides major performance improvements
 */
public class SampleApplication extends MapstedBaseApplication {
    @Override
    protected void onCoreApiCreated() {
        Log.d("SampleApplication", "onCoreApiCreated");
    }

    @Override
    protected void onCoreApiDestroyed() {
        Log.d("SampleApplication", "onCoreApiDestroyed");
    }
}
