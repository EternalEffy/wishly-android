package com.wishly.app.presentation.home

import android.util.Log
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wishly.app.data.model.Wishlist
import com.wishly.app.di.ViewModelFactory
import com.wishly.app.ui.components.ErrorSnackbar
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val TAG = "HomeScreen"

    val viewModel: HomeViewModel = viewModel {
        HomeViewModel(
            wishlistRepository = ViewModelFactory.getWishlistRepository()
        )
    }
    Log.d(TAG, "HomeScreen @Composable called")

    val uiState by viewModel.uiState.collectAsState()
    Log.d(
        TAG,
        "uiState collected: isLoading=${uiState.isLoading}, wishlists.size=${uiState.wishlists.size}, error=${uiState.error}"
    )

    val hasProcessedResult = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Log.d(TAG, "Column rendering START")
        //header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Wishlists",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onLogout,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Log.d(TAG, "Header rendered")

        Spacer(modifier = Modifier.height(16.dp))

        //create wishlist btn
        Button(
            onClick = onNavigateToCreate,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Create New Wishlist")
        }
        Log.d(TAG, "Create button rendered")

        Spacer(modifier = Modifier.height(16.dp))

        //error msg
        if (uiState.error != null) {
            Log.d(TAG, "Rendering error card: ${uiState.error}")
            ErrorSnackbar(
                error = uiState.error,
                onDismiss = {
                    Log.d(TAG, "ErrorSnackbar dismissed")
                    viewModel.clearError()
                },
                onRetry = {
                    Log.d(TAG, "ErrorSnackbar retry clicked")
                    viewModel.retry()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        //loading
        if (uiState.isLoading) {
            Log.d(TAG, "Rendering: LOADING state")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.wishlists.isEmpty()) {
            Log.d(TAG, "Rendering: EMPTY state")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No wishlists yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Create your first wishlist!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Log.d(TAG, "Rendering: LIST state with ${uiState.wishlists.size} items")
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Log.d(TAG, "LazyColumn created")

                items(
                    items = uiState.wishlists,
                    key = { it.id }
                ) { wishlist ->
                    Log.d(TAG, "Rendering item: ${wishlist.id.take(8)}... - ${wishlist.title}")
                    WishlistCard(
                        wishlist = wishlist,
                        onClick = { onNavigateToDetail(wishlist.id) },
                        onDelete = { viewModel.deleteWishlist(wishlist.id) }
                    )
                    Log.d(TAG, "Item rendered successfully")
                }
                Log.d(TAG, "Column rendering END")
            }
        }
    }

}

@Composable
fun WishlistCard(
    wishlist: Wishlist,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val itemCount = wishlist.items?.size ?: 0

    var showDeleteConfirm by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = wishlist.title,
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete wishlist",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (wishlist.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wishlist.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📦 $itemCount items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "🔓 ${wishlist.privacy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (wishlist.eventDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📅 ${formatDate(wishlist.eventDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Delete ${wishlist.title}?") },
                    text = { Text("This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDelete()
                                showDeleteConfirm = false
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}