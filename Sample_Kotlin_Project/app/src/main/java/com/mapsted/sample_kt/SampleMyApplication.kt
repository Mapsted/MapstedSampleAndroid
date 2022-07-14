package com.mapsted.sample_kt

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.mapsted.positioning.CoreApi
import com.mapsted.positioning.MapstedCoreApi

class SampleMyApplication : Application() {

    private val TAG: String = "SampleMyApplication"
    lateinit var coreApi: CoreApi

    override fun onCreate() {
        super.onCreate()
        coreApi = MapstedCoreApi.newInstance(applicationContext);
        registerActivityCounter();
    }

    private fun registerActivityCounter() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            val activities = HashSet<String>()

            /**
             * Called when the Activity calls [super.onCreate()][Activity.onCreate].
             */
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activities.add(activity.localClassName);
                Log.d(TAG, "onActivityCreated: ${activity.localClassName} size ${activities.size}")
            }

            /**
             * Called when the Activity calls [super.onStart()][Activity.onStart].
             */
            override fun onActivityStarted(activity: Activity) {
                Log.d(TAG, "onActivityStarted: ${activity.localClassName}")
            }

            /**
             * Called when the Activity calls [super.onResume()][Activity.onResume].
             */
            override fun onActivityResumed(activity: Activity) {
                Log.d(TAG, "onActivityResumed: ${activity.localClassName}")
            }

            /**
             * Called when the Activity calls [super.onPause()][Activity.onPause].
             */
            override fun onActivityPaused(activity: Activity) {
                Log.d(TAG, "onActivityPaused: ${activity.localClassName}")
            }

            /**
             * Called when the Activity calls [super.onStop()][Activity.onStop].
             */
            override fun onActivityStopped(activity: Activity) {
                Log.d(TAG, "onActivityStopped: ${activity.localClassName}")
            }

            /**
             * Called when the Activity calls
             * [super.onSaveInstanceState()][Activity.onSaveInstanceState].
             */
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d(TAG, "onActivitySaveInstanceState: ${activity.localClassName}")
            }

            /**
             * Called when the Activity calls [super.onDestroy()][Activity.onDestroy].
             */
            override fun onActivityDestroyed(activity: Activity) {
               activities.remove(activity.localClassName)
                Log.d(TAG, "onActivityDestroyed = ${activity.localClassName}   size ${activities.size}")
                if (activities.isEmpty()) {
                    Log.d(TAG, "onActivityDestroyed coreApi.onDestroy")
                    coreApi.onDestroy()
                }
            }
        })
    }
}