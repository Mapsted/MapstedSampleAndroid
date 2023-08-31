package com.mapsted.compose_demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mapsted.MapstedBaseApplication

class DemoApplication : MapstedBaseApplication() {

    companion object {
        private val TAG: String = DemoApplication::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d(TAG, "onActivityCreated: " + activity.localClassName)
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                Log.d(TAG, "onActivityDestroyed: " + activity.localClassName)
            }
        })
    }

    override fun onCoreApiCreated() {
        Log.d(TAG, "onCoreApiCreated: ")
    }

    override fun onCoreApiDestroyed() {
        Log.d(TAG, "onCoreApiDestroyed: ")
    }
}
