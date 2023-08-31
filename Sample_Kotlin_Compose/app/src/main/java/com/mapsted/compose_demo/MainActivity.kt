package com.mapsted.compose_demo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.mapsted.positioning.CoreApi
import com.mapsted.positioning.CoreApi.LocationServicesCallback
import com.mapsted.positioning.MapstedCoreApiProvider
import com.mapsted.positioning.SdkError
import com.mapsted.positioning.SdkStatusUpdate
import com.mapsted.positioning.core.map_download.PropertyDownloadManager
import com.mapsted.positioning.core.models.internal.DownloadStatus
import com.mapsted.compose_demo.ui.theme.DemoAppTheme
import com.mapsted.positioning.CoreParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : ComponentActivity(), MapstedCoreApiProvider {

    private var coreApi: CoreApi? = null
    private val isPropertyDownloadComplete = mutableStateOf(false)
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private var tActivityStart = 0L

    private var tStartInitMapsted = 0L
    private var tInitMapstedFinished = 0L

    private var tStartDownload = 0L
    private var tDownloadFinished = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coreApi = (application as DemoApplication).getCoreApi(this)

        // Keep splash screen until property is downloaded
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isPropertyDownloadComplete.value }

        setContent {
            DemoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DemoApp()
                }
            }
        }

        tActivityStart = System.currentTimeMillis()

        Log.d(TAG, "onCreate")
        setupMapstedSdk()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    private fun setupMapstedSdk() {

        tStartInitMapsted = System.currentTimeMillis()

        val params = CoreParams.newBuilder()
            // Add additional custom content here
            .build()

        coreApi?.setup()?.initialize(
            params,
            object : CoreApi.CoreInitCallback {
                override fun onSuccess() {

                    tInitMapstedFinished = System.currentTimeMillis()
                    val dtInitSec = (tInitMapstedFinished - tStartInitMapsted) / 1000.0

                    val msg = String.format(Locale.CANADA, "coreApi: onSuccess. dt: %.1f s", dtInitSec)

                    Log.d(TAG, msg)

                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                    }

                    coreApi?.setup()?.startLocationServices(this@MainActivity,
                        object : LocationServicesCallback {
                            override fun onSuccess() {
                                Log.d(TAG, "coreApi: LocServices: onSuccess")
                            }

                            override fun onFailure(sdkError: SdkError?) {
                                Log.d(TAG, "coreApi: LocServices: onFailure " + sdkError.toString())
                            }
                    })

                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            // Grab first property in licence
                            val propertyInfos = coreApi?.properties()?.infos
                            val numProperties = propertyInfos?.size
                            val propertyId = if (numProperties != null && numProperties > 0)
                                propertyInfos.keys.first()
                            else -1

                            val status = coreApi?.properties()?.getDownloadStatus(propertyId)
                            Log.d(TAG, "pId: $propertyId -> status: $status")

                            withContext(Dispatchers.Main) {
                                if (status?.status != DownloadStatus.DOWNLOADED) {
                                    startPropertyDownload(propertyId)
                                } else {
                                    val alreadyDownloadedMsg = "Property ($propertyId) already downloaded"
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        Toast.makeText(applicationContext, alreadyDownloadedMsg, Toast.LENGTH_LONG).show()
                                    }
                                    Log.d(TAG, alreadyDownloadedMsg)
                                    isPropertyDownloadComplete.value = true
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "Error: ${e.message}")
                            isPropertyDownloadComplete.value = true
                        }
                    }
                }

                override fun onFailure(sdkError: SdkError) {
                    Log.d(TAG, "coreApi: onFailure: $sdkError")
                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "coreApi: onFailure: $sdkError", Toast.LENGTH_LONG).show()
                    }
                    isPropertyDownloadComplete.value = true
                }

                override fun onStatusUpdate(sdkUpdate: SdkStatusUpdate) {
                    Log.d(TAG, "coreApi: onStatusUpdate: $sdkUpdate")
                }
            }
        )
    }

    private fun startPropertyDownload(propertyId: Int) {
        Log.d(TAG, "startPropertyDownload: pId: $propertyId")

        tStartDownload = System.currentTimeMillis()

        coreApi?.properties()?.startDownload(
            propertyId,
            object : PropertyDownloadManager.Listener {
                override fun onComplete(propertyId: Int) {

                    tDownloadFinished = System.currentTimeMillis()

                    val dtDownloadSec = (tStartDownload - tDownloadFinished) / 1000.0

                    val msg = String.format(Locale.CANADA,
                        "startPropertyDownload: onComplete: pId: $propertyId dt: %.1f s",
                        dtDownloadSec)

                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                    }
                    Log.d(TAG, msg)

                    isPropertyDownloadComplete.value = true
                }

                override fun onFail(propertyId: Int, exception: Exception?) {

                    val msg = "startPropertyDownload: onFail: pId: $propertyId -> ${exception?.message}"

                    lifecycleScope.launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                    }
                    Log.d(TAG, msg)

                    isPropertyDownloadComplete.value = true
                }

                override fun onProgress(propertyId: Int, current: Int, total: Int) {
                    Log.d(TAG, "startPropertyDownload: onProgress: pId: $propertyId -> ($current / $total)")
                }
            }
        )
    }

    override fun provideMapstedCoreApi(): CoreApi? {
        return coreApi
    }
}
