package com.example.facialrecognition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.facialrecognition.ui.main.MainScreen
import com.example.facialrecognition.ui.permissions.PermissionsScreen
import com.example.facialrecognition.ui.photos.AllPhotosScreen
import com.example.facialrecognition.ui.profile.ProfileScreen
import com.example.facialrecognition.ui.progress.ProgressScreen
import com.example.facialrecognition.ui.search.SearchScreen
import com.example.facialrecognition.ui.theme.FacialPhotoManagementTheme
import com.example.facialrecognition.ui.welcome.WelcomeScreen
import com.example.facialrecognition.worker.ScanWorker
import com.example.facialrecognition.data.local.AppPreferences
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.values.all { it }
        if (isGranted) {
            triggerScan()
            // Navigation will be handled by the UI state observation or callback if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FacialPhotoManagementTheme {
                MainAppContent(
                    onRequestPermissions = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES))
                        } else {
                            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
                        }
                    },
                    onTriggerScan = { triggerScan() }
                )
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
fun MainAppContent(
    onRequestPermissions: () -> Unit,
    onTriggerScan: () -> Unit
) {
    val context = LocalContext.current
    val appPreferences = androidx.compose.runtime.remember { AppPreferences(context) }
    val isOnboarded = androidx.compose.runtime.remember { appPreferences.isOnboarded }
    val startDestination = if (isOnboarded) "main" else "welcome"

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            title = stringResource(R.string.nav_home),
            route = "main",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_gallery),
            route = "photos",
            selectedIcon = Icons.Filled.PhotoAlbum,
            unselectedIcon = Icons.Outlined.PhotoAlbum
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_people),
            route = "people_tab",
            selectedIcon = Icons.Filled.Face,
            unselectedIcon = Icons.Outlined.Face
        ),
        BottomNavItem(
            title = stringResource(R.string.nav_settings),
            route = "profile",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    val showBottomBar = currentRoute in listOf("main", "photos", "people_tab", "profile")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            },
                            label = { Text(text = item.title) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) 
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { 
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = { 
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = { 
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = { 
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
            }
        ) {
            composable("welcome") {
                WelcomeScreen(
                    onStartClick = { navController.navigate("permissions") }
                )
            }
            composable("permissions") {
                PermissionsScreen(
                    onGrantClick = {
                        onRequestPermissions()
                        onTriggerScan()
                        navController.navigate("progress")
                    },
                    onNotNowClick = {
                        appPreferences.isOnboarded = true
                        navController.navigate("main") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }
            composable("progress") {
                ProgressScreen(
                    onBackClick = { navController.navigate("main") },
                    onContinueClick = {
                        appPreferences.isOnboarded = true
                        navController.navigate("main") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }
            composable("main") {
                MainScreen(
                    onPersonClick = { personId ->
                        navController.navigate("detail/$personId")
                    },
                    onAllPhotosClick = {
                        navController.navigate("photos")
                    },
                    onPhotoClick = { photoId ->
                        navController.navigate("viewer/all/0/$photoId")
                    },
                    onSearchClick = {
                        navController.navigate("search")
                    },
                    onSettingsClick = {
                        navController.navigate("profile")
                    }
                )
            }
            composable("photos") {
                AllPhotosScreen(
                    onPhotoClick = { photoId ->
                        navController.navigate("viewer/all/0/$photoId")
                    }
                )
            }
            composable("search") {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onPersonClick = { personId ->
                        navController.navigate("detail/$personId")
                    },
                    showBackButton = true
                )
            }
            composable("people_tab") {
                SearchScreen(
                    onBackClick = { }, // No back action needed for tab
                    onPersonClick = { personId ->
                        navController.navigate("detail/$personId")
                    },
                    showBackButton = false
                )
            }
            composable("profile") {
                ProfileScreen(
                    onRescanClick = {
                        onTriggerScan()
                    }
                )
            }
            composable(
                "detail/{personId}",
                arguments = listOf(androidx.navigation.navArgument("personId") { type = androidx.navigation.NavType.LongType })
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getLong("personId") ?: return@composable
                com.example.facialrecognition.ui.detail.PersonDetailScreen(
                    personId = personId,
                    onBackClick = { navController.popBackStack() },
                    onPhotoClick = { photoId ->
                        navController.navigate("viewer/person/$personId/$photoId")
                    },
                    onPlayClick = {
                        // Start slideshow from the first photo
                        navController.navigate("viewer/person/$personId/0?autoPlay=true")
                    }
                )
            }
            composable(
                "viewer/{source}/{sourceId}/{startPhotoId}?autoPlay={autoPlay}",
                arguments = listOf(
                    androidx.navigation.navArgument("source") { type = androidx.navigation.NavType.StringType },
                    androidx.navigation.navArgument("sourceId") { type = androidx.navigation.NavType.LongType },
                    androidx.navigation.navArgument("startPhotoId") { type = androidx.navigation.NavType.LongType },
                    androidx.navigation.navArgument("autoPlay") { 
                        type = androidx.navigation.NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val source = backStackEntry.arguments?.getString("source") ?: "all"
                val sourceId = backStackEntry.arguments?.getLong("sourceId") ?: 0L
                val startPhotoId = backStackEntry.arguments?.getLong("startPhotoId") ?: 0L
                val autoPlay = backStackEntry.arguments?.getBoolean("autoPlay") ?: false
                
                com.example.facialrecognition.ui.photos.PhotoViewerScreen(
                    source = source,
                    sourceId = sourceId,
                    startPhotoId = startPhotoId,
                    autoPlay = autoPlay,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
