package com.wishly.app.navigation

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.wishly.app.di.ViewModelFactory
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

        composable(Screen.Home.route) { backStackEntry ->
            val viewModel: HomeViewModel = viewModel(backStackEntry) {
                HomeViewModel(
                    wishlistRepository = ViewModelFactory.getWishlistRepository()
                )
            }

            val savedStateHandle = backStackEntry.savedStateHandle

            val hasProcessedResult = remember { mutableStateOf(false) }

            if (!hasProcessedResult.value) {
                val newWishlistJson = savedStateHandle.get<String>(NavKeys.KEY_NEW_WISHLIST)

                if (newWishlistJson != null) {
                    Log.d("APP NAV", ">>> Processing new wishlist from navigation result")

                    val newWishlist = NavKeys.argToWishlist(newWishlistJson)
                    viewModel.addWishlistOptimistically(newWishlist)

                    savedStateHandle.remove<String>(NavKeys.KEY_NEW_WISHLIST)
                    hasProcessedResult.value = true

                    Log.d("APP NAV", "<<< Navigation result processed and cleared")
                }
            }

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

        composable(Screen.CreateWishlist.route) {
            CreateWishlistScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() },
                onWishlistCreated = { navController.popBackStack() }
            )
        }

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