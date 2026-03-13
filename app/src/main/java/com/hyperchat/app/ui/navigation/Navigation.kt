package com.hyperchat.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "首页", Icons.Default.Home)
    data object Chat : Screen("chat?conversationId={conversationId}", "聊天辅助", Icons.Default.Chat)
    data object Screenshot : Screen("screenshot", "截图分析", Icons.Default.Photo)
    data object Review : Screen("review", "聊天复盘", Icons.Default.Refresh)
    data object Profile : Screen("profile", "我的", Icons.Default.Person)
    data object Settings : Screen("settings", "设置", Icons.Default.Settings)
    data object NewConversation : Screen("new_conversation", "新建会话", Icons.Default.Chat)
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

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route?.startsWith(screen.route.substringBefore("?")) == true
                        } == true,
                        onClick = {
                            navController.navigate(
                                if (screen == Screen.Chat) "chat?conversationId=-1"
                                else screen.route
                            ) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onStartChat = { conversationId ->
                        navController.navigate("chat?conversationId=$conversationId")
                    },
                    onAnalyzeScreenshot = {
                        navController.navigate(Screen.Screenshot.route)
                    },
                    onReview = {
                        navController.navigate(Screen.Review.route)
                    }
                )
            }

            composable(
                route = "chat?conversationId={conversationId}",
                arguments = listOf(
                    navArgument("conversationId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getLong("conversationId") ?: -1L
                ChatScreen(
                    conversationId = conversationId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Screenshot.route) {
                ScreenshotScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Review.route) {
                ReviewScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onSettingsClick = { navController.navigate("settings") }
                )
            }

            composable("settings") {
                SettingsScreen(onBack = { navController.popBackStack() })
            }

            composable("new_conversation") {
                NewConversationScreen(
                    onConversationCreated = { conversationId ->
                        navController.navigate("chat?conversationId=$conversationId") {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
        }
    }
}
