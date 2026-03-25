package com.hyperchat.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hyperchat.app.ui.screens.*

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen(
        "home",
        "首页",
        Icons.Filled.Home,
        Icons.Outlined.Home
    )
    data object Chat : Screen(
        "chat/{conversationId}",
        "聊天",
        Icons.Filled.Chat,
        Icons.Outlined.Chat
    ) {
        fun createRoute(conversationId: Long = -1L) = "chat/$conversationId"
    }
    data object Screenshot : Screen(
        "screenshot",
        "截图",
        Icons.Filled.PhotoCamera,
        Icons.Outlined.PhotoCamera
    )
    data object Review : Screen(
        "review",
        "复盘",
        Icons.Filled.Analytics,
        Icons.Outlined.Analytics
    )
    data object Profile : Screen(
        "profile",
        "情商库",
        Icons.Filled.AutoStories,
        Icons.Outlined.AutoStories
    )
    data object Settings : Screen(
        "settings",
        "设置",
        Icons.Filled.Settings,
        Icons.Outlined.Settings
    )
    data object NewConversation : Screen(
        "new_conversation",
        "新建会话",
        Icons.Filled.Add,
        Icons.Outlined.Add
    )
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Chat,
    Screen.Screenshot,
    Screen.Review,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HyperChatNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if we should show bottom bar
    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true ||
        currentDestination?.route?.startsWith(screen.route.substringBefore("/")) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route ||
                            (screen == Screen.Chat && it.route?.startsWith("chat/") == true)
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                val route = when (screen) {
                                    Screen.Chat -> Screen.Chat.createRoute(-1L)
                                    else -> screen.route
                                }

                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onStartChat = { conversationId ->
                        navController.navigate(Screen.Chat.createRoute(conversationId))
                    },
                    onAnalyzeScreenshot = {
                        navController.navigate(Screen.Screenshot.route)
                    },
                    onReview = {
                        navController.navigate(Screen.Review.route)
                    }
                )
            }

            // Chat Screen
            composable(
                route = Screen.Chat.route,
                arguments = listOf(
                    navArgument("conversationId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L

                if (conversationId == -1L) {
                    // Show new conversation screen first
                    NewConversationScreen(
                        onConversationCreated = { newConversationId ->
                            navController.navigate(Screen.Chat.createRoute(newConversationId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                } else {
                    ChatScreen(
                        conversationId = conversationId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // Screenshot Screen
            composable(Screen.Screenshot.route) {
                ScreenshotScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Review Screen
            composable(Screen.Review.route) {
                ReviewScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Profile/EQ Library Screen
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            // Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
