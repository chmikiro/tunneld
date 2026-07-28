package com.tunneld.ipdiali.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tunneld.ipdiali.feature.background.ui.RunInBackgroundRoute
import com.tunneld.ipdiali.shared.core.navigation.forwardBackwardComposable
import com.tunneld.ipdiali.shared.feature.notifications.ui.NotificationsRoute
import com.tunneld.ipdiali.shared.feature.settings.ui.SettingsRoute

@Composable
internal fun AppNavHost(
    onExportCsv: (csvContent: String) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Main.name,
        modifier = modifier,
    ) {
        forwardBackwardComposable(Route.Main.name) {
            MainScreen(
                onSettings = {
                    navController.navigate(Route.Settings.name) { launchSingleTop = true }
                },
                onExportCsv = onExportCsv,
            )
        }
        forwardBackwardComposable(Route.Settings.name) {
            SettingsRoute(
                onBack = { navController.popBackStack(Route.Settings.name, inclusive = true) },
                onRunInBackground = {
                    navController.navigate(Route.Background.name) { launchSingleTop = true }
                },
                onNotifications = {
                    navController.navigate(Route.Notifications.name) { launchSingleTop = true }
                },
                modifier = Modifier,
            )
        }
        forwardBackwardComposable(Route.Background.name) {
            RunInBackgroundRoute(
                onBack = { navController.popBackStack(Route.Background.name, inclusive = true) }
            )
        }
        forwardBackwardComposable(Route.Notifications.name) {
            NotificationsRoute(
                onBack = { navController.popBackStack(Route.Notifications.name, inclusive = true) }
            )
        }
    }
}

private enum class Route {
    Main,
    Settings,
    Background,
    Notifications,
}
