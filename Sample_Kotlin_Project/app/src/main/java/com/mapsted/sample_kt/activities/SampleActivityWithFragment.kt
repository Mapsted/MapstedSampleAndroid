package com.mapsted.sample_kt.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mapsted.sample_kt.R
import com.mapsted.sample_kt.databinding.ActivitySampleWithFragmentBinding
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider
import com.mapsted.ui.MapstedSdkController

class SampleActivityWithFragment : AppCompatActivity(), MapstedMapUiApiProvider {

    private lateinit var mapUiApi: MapUiApi
    private lateinit var mBinding: ActivitySampleWithFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_with_fragment)
        mapUiApi = MapstedSdkController.newInstance(this)
        val fragment: Fragment = SampleFragment.newInstance("someparam")
        val tag = SampleFragment::class.java.name
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, tag)
            .commit()

    }

    override fun provideMapstedUiApi(): MapUiApi {
        return mapUiApi
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (!mapUiApi.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        ) super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mapUiApi.onConfigurationChanged(this, newConfig)
    }
}