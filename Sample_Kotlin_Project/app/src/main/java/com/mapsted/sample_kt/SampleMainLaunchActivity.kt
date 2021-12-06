package com.mapsted.sample_kt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mapsted.sample_kt.activities.SampleActivityWithFragment
import com.mapsted.sample_kt.activities.SampleMapWithAButtonActivity
import com.mapsted.sample_kt.activities.SampleMapWithUiToolsActivity
import com.mapsted.sample_kt.databinding.ActivitySampleMainLaunchBinding

class SampleMainLaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySampleMainLaunchBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sample_main_launch)
        val context = this

        binding.btnLaunchMapWithTools.setOnClickListener {
            val intent = Intent(context, SampleMapWithUiToolsActivity::class.java)
            startActivity(intent)
        }

        binding.btnLaunchMapWithButton.setOnClickListener {
            val intent = Intent(context, SampleMapWithAButtonActivity::class.java)
            startActivity(intent)
        }

        binding.btnLaunchActivityWithFragment.setOnClickListener {
            val intent = Intent(context, SampleActivityWithFragment::class.java)
            startActivity(intent)
        }
    }
}