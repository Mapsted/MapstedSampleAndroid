package com.mapsted.compose_demo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mapsted.compose_demo.navigation.DemoAppNavigation
import com.mapsted.compose_demo.ui.screens.HomeScreen
import com.mapsted.compose_demo.ui.screens.MapScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp() {
    Log.d("DemoApp", "DemoApp")
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            DemoTopAppBar()
        },
        bottomBar = {
            if (navController.currentBackStackEntry?.destination?.route != "Splash") {
                DemoAppNavigation(navController)
            } else {
                null
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            DemoNavHost(navController)
        }
    }
}

@Composable
fun DemoNavHost(navController: NavHostController ) {
    Log.d("DemoNavHost", "DemoNavHost")
    NavHost(
        navController = navController,
        startDestination = "Home",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("Home") { HomeScreen() }
        composable("Map") { MapScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Demo App Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(200.dp)
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}

@Composable
@Preview
fun DemoAppPreview() {
    DemoApp()
}