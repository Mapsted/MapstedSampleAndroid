package com.mapsted.sample_kt.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mapsted.map.views.MapPanType
import com.mapsted.positioning.MapstedInitCallback
import com.mapsted.positioning.MessageType
import com.mapsted.positioning.SdkError
import com.mapsted.sample_kt.databinding.FragmentSampleBinding
import com.mapsted.ui.CustomParams
import com.mapsted.ui.MapUiApi
import com.mapsted.ui.MapstedMapUiApiProvider

class SampleFragment : Fragment() {

    private var sdk: MapUiApi? = null
    private val TAG = SampleFragment::class.java.simpleName
    private lateinit var mBinding: FragmentSampleBinding

    companion object {

        private val ARG_PARAM1 = "param1"

        fun newInstance(param1: String?): SampleFragment {
            val fragment = SampleFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val apiProvider = context as MapstedMapUiApiProvider
        sdk = apiProvider.provideMapstedUiApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSampleBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CustomParams.newBuilder()
            .setMapPanType(MapPanType.RESTRICT_TO_SELECTED_PROPERTY)
            .setShowPropertyListOnMapLaunch(true)
            .setEnablePropertyListSelection(true)
            .build()

        sdk?.initializeMapstedSDK(
            activity as AppCompatActivity?,
            mBinding.mapContainer,
            mapstedInitCallback
        )

    }

    private val mapstedInitCallback: MapstedInitCallback = object : MapstedInitCallback {
        override fun onCoreInitialized() {
            Log.d(TAG, "onCoreInitialized: ")
        }

        override fun onMapInitialized() {
            Log.d(TAG, "onMapInitialized: ")
        }

        override fun onSuccess() {
            Log.d(TAG, "onSuccess: ")
        }

        override fun onFailure(sdkError: SdkError) {
            Log.e(TAG, "onFailure: " + sdkError.errorMessage)
        }

        override fun onMessage(messageType: MessageType?, s: String?) {
            Log.d(TAG,"onMessage: s")
        }
    }


    override fun onDestroy() {
        sdk!!.onDestroy()
        super.onDestroy()
    }
}