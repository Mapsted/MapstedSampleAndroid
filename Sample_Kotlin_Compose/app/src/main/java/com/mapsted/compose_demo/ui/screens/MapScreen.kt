package com.mapsted.compose_demo.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mapsted.compose_demo.MapActivity

@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navController.navigate("Home")
            }
        }
    )

    LaunchedEffect(Unit) {
        val intent = Intent(context, MapActivity::class.java)
        launcher.launch(intent)
    }
}
