package com.example.facialrecognition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.facialrecognition.ui.main.MainScreen
import com.example.facialrecognition.ui.photos.AllPhotosScreen
import com.example.facialrecognition.ui.progress.ProgressScreen
import com.example.facialrecognition.ui.theme.FacialPhotoManagementTheme
import com.example.facialrecognition.worker.ScanWorker
import androidx.work.Constraints

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Trigger scan after permission granted
        triggerScan()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request Permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
        }

        // Trigger initial scan
        triggerScan()

        setContent {
            FacialPhotoManagementTheme {
                MainAppContent()
            }
        }
    }

    private fun triggerScan() {
        val constraints = Constraints.Builder()
            .build()
            
        val scanRequest = OneTimeWorkRequestBuilder<ScanWorker>()
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(applicationContext).enqueue(scanRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val navController = rememberNavController()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            route = "main",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            title = "Photos",
            route = "photos",
            selectedIcon = Icons.Filled.Face,
            unselectedIcon = Icons.Outlined.Face
        ),
        BottomNavItem(
            title = "Progress",
            route = "progress",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            navController.navigate(item.route) {
                                popUpTo("main") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(text = item.title) },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) 
                                    item.selectedIcon 
                                else 
                                    item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("main") {
                MainScreen(onPersonClick = { personId ->
                    navController.navigate("detail/$personId")
                })
            }
            composable("photos") {
                AllPhotosScreen()
            }
            composable("progress") {
                ProgressScreen()
            }
            composable(
                "detail/{personId}",
                arguments = listOf(androidx.navigation.navArgument("personId") { type = androidx.navigation.NavType.LongType })
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getLong("personId") ?: return@composable
                com.example.facialrecognition.ui.detail.PersonDetailScreen(
                    personId = personId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
