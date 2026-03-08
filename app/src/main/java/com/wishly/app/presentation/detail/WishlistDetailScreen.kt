package com.wishly.app.presentation.detail

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WishlistDetailScreen(
    wishlistHash: String,
    onNavigateBack: () -> Unit,
    onNavigateToShare: (String, String) -> Unit,
    viewModel: WishlistDetailViewModel = viewModel()
) { }