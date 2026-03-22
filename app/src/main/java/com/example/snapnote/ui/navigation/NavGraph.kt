package com.example.snapnote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.snapnote.ui.screens.DetailScreen
import com.example.snapnote.ui.screens.HomeScreen
import com.example.snapnote.ui.screens.ManualScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Manual : Screen("manual")
    object Detail : Screen("detail/{noteId}") {
        fun createRoute(noteId: Int) = "detail/$noteId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { noteId ->
                    navController.navigate(Screen.Detail.createRoute(noteId))
                },
                onNavigateToManual = {
                    navController.navigate(Screen.Manual.route)
                }
            )
        }
        composable(Screen.Manual.route) {
            ManualScreen(
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
