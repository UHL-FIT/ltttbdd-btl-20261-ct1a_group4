package com.example.studysync_btl

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studysync_btl.navigation.NavigationItems
import com.example.studysync_btl.navigation.StudySyncNavGraph
import com.example.studysync_btl.navigation.StudySyncRoutes

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysync_btl.viewmodel.StudySyncViewModel
import com.example.studysync_btl.ui.theme.StudySync_BTLTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "LifecycleDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate START - before super")  // THÊM DÒNG NÀY
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        try {   // THÊM TRY-CATCH ĐỂ BẮT LỖI
            setContent {
                StudySync_BTLTheme {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                                NavigationItems.bottomNavItems.forEach { item ->
                                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        selected = isSelected,
                                        onClick = {
                                            val isHome = item.route == StudySyncRoutes.TRANG_CHU

                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = !isHome
                                                }
                                                launchSingleTop = true
                                                restoreState = !isHome
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        StudySyncNavGraph(
                            navController = navController,
                            paddingValues = innerPadding
                        )
                    }
                }
            }
            Log.d(TAG, "setContent completed successfully")  // THÊM DÒNG NÀY
        } catch (e: Exception) {
            Log.e(TAG, "Error in setContent: ${e.message}", e)  // THÊM DÒNG NÀY
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
    }
}