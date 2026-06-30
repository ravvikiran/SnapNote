package com.snapnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.snapnote.data.settings.SettingsDataStore
import com.snapnote.ui.screens.DetailScreen
import com.snapnote.ui.screens.HomeScreen
import com.snapnote.ui.screens.ManualScreen
import com.snapnote.ui.screens.OnboardingScreen
import com.snapnote.ui.screens.SettingsScreen
import com.snapnote.ui.screens.SplashScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Manual : Screen("manual")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{noteId}") {
        fun createRoute(noteId: Int) = "detail/$noteId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            // Use LaunchedEffect to avoid blocking the main thread
            var navigated by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                if (!navigated) {
                    // Small delay to let the splash animation play
                    delay(2000)
                    val onboardingDone = settingsDataStore.onboardingCompleted.first()
                    val destination = if (onboardingDone) {
                        Screen.Home.route
                    } else {
                        Screen.Onboarding.route
                    }
                    navigated = true
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }

            SplashScreen(onSplashFinished = {
                // No-op: navigation is handled by LaunchedEffect above
            })
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    scope.launch {
                        settingsDataStore.setOnboardingCompleted(true)
                    }
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { noteId ->
                    navController.navigate(Screen.Detail.createRoute(noteId))
                },
                onNavigateToManual = {
                    navController.navigate(Screen.Manual.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Manual.route) {
            ManualScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
            DetailScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
