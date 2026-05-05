package com.androidforge.streakwise.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androidforge.streakwise.presentation.ui.about.AboutScreen
import com.androidforge.streakwise.presentation.ui.addedit.AddEditHabitScreen
import com.androidforge.streakwise.presentation.ui.habitdetail.HabitDetailScreen
import com.androidforge.streakwise.presentation.ui.habitsdashboard.HabitsDashboardScreen
import com.androidforge.streakwise.presentation.ui.onboarding.OnboardingScreen
import com.androidforge.streakwise.presentation.ui.privacy.PrivacyPolicyScreen
import com.androidforge.streakwise.presentation.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object HabitsDashboard : Screen("habits_dashboard")
    data object AddEditHabit : Screen("add_edit_habit?habitId={habitId}") {
        fun createRoute(habitId: Long? = null) = "add_edit_habit?habitId=${habitId ?: -1L}"
    }
    data object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: Long) = "habit_detail/$habitId"
    }
    data object Settings : Screen("settings")
    data object About : Screen("about")
    data object PrivacyPolicy : Screen("privacy_policy")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(400))
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.popBackStack() // Remove onboarding from back stack
                    navController.navigate(Screen.HabitsDashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.HabitsDashboard.route) {
            HabitsDashboardScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAddEditHabit = { habitId -> navController.navigate(Screen.AddEditHabit.createRoute(habitId)) },
                onNavigateToHabitDetail = { habitId -> navController.navigate(Screen.HabitDetail.createRoute(habitId)) }
            )
        }
        composable(
            route = Screen.AddEditHabit.route,
            arguments = listOf(navArgument("habitId") {
                type = NavType.LongType
                defaultValue = -1L // Indicates a new habit
                nullable = true
            })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId")
            AddEditHabitScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(navArgument("habitId") {
                type = NavType.LongType
                nullable = false
            })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: -1L
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditHabit = { id -> navController.navigate(Screen.AddEditHabit.createRoute(id)) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}