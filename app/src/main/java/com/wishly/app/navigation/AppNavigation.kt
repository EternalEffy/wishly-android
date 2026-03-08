package com.wishly.app.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.wishly.app.presentation.auth.*
import com.wishly.app.presentation.home.*
import com.wishly.app.presentation.create.*
import com.wishly.app.presentation.detail.*
import com.wishly.app.presentation.share.*


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object CreateWishlist : Screen("create_wishlist")
    object WishlistDetail : Screen("wishlist_detail/{hash}") {
        fun createRoute(hash: String) = "wishlist_detail/$hash"
    }
    object Share : Screen("share/{hash}/{title}") {
        fun createRoute(hash: String, title: String) = "share/$hash/$title"
    }
}

@Composable
fun AppNavigation(
    isLoggedIn: Boolean = false,
    onAuthStateChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isLoggedIn = isLoggedIn
            )
        }

        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    onAuthStateChanged(true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Register
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    onAuthStateChanged(true)
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateWishlist.route)
                },
                onNavigateToDetail = { hash ->
                    navController.navigate(Screen.WishlistDetail.createRoute(hash))
                },
                onLogout = {
                    onAuthStateChanged(false)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Create Wishlist
        composable(Screen.CreateWishlist.route) {
            CreateWishlistScreen(
                onNavigateBack = { navController.popBackStack() },
                onWishlistCreated = { navController.popBackStack() }
            )
        }

        // Wishlist Detail
        composable(Screen.WishlistDetail.route) { backStackEntry ->
            val hash = backStackEntry.arguments?.getString("hash") ?: return@composable
            WishlistDetailScreen(
                wishlistHash = hash,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToShare = { hash, title ->
                    navController.navigate(Screen.Share.createRoute(hash, title))
                }
            )
        }

        // Share
        composable(Screen.Share.route) { backStackEntry ->
            val hash = backStackEntry.arguments?.getString("hash") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            ShareScreen(
                wishlistHash = hash,
                wishlistTitle = title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}