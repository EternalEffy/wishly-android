package com.wishly.app.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishly.app.data.model.Wishlist
import com.wishly.app.data.repository.WishlistRepository
import com.wishly.app.util.AppError
import com.wishly.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val wishlists: List<Wishlist> = emptyList(),
    val isLoading: Boolean = false,
    val error: AppError? = null
)

class HomeViewModel(private val wishlistRepository: WishlistRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val TAG = "HomeVM"

    init {
        Log.d(TAG, "ViewModel created, calling loadWishlists()")
        loadWishlists()
    }

    fun loadWishlists() {
        Log.d(TAG, "loadWishlists() START")

        viewModelScope.launch {
            _uiState.update {
                Log.d(TAG, "Updating state: isLoading=true")
                it.copy(isLoading = true, error = null)
            }

            Log.d(TAG, "Calling repository.getMyWishlists()")
            when (val result = wishlistRepository.getMyWishlists()) {
                is Result.Success -> {
                    Log.d(TAG, "SUCCESS: ${result.data.size} wishlists")
                    result.data.forEachIndexed { i, w ->
                        android.util.Log.d(TAG, "  [$i] ${w.id.take(8)}... - ${w.title}")
                    }
                    _uiState.update {
                        Log.d(TAG, "Updating state: wishlists=${result.data.size}, isLoading=false")
                        it.copy(
                            isLoading = false,
                            wishlists = result.data,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    val appError = result.toAppError()
                    Log.e(TAG, "ERROR: ${result.message}")
                    _uiState.update {
                        Log.d(TAG, "Updating state: error=${result.message}")
                        it.copy(
                            isLoading = false,
                            error = appError
                        )
                    }
                    if (appError is AppError.Unauthorized ||
                        appError is AppError.TokenExpired) {
                        Log.e(TAG, "Critical error: ${appError::class.simpleName}")
                        // можно вызвать колбэк для логаута
                    }
                }

                is Result.Loading -> {
                    Log.d(TAG, "LOADING state")
                }
            }
        }
    }

    fun addWishlistOptimistically(newWishlist: Wishlist) {
        Log.d(TAG, ">>> addWishlistOptimistically() called")
        Log.d(TAG, "    New wishlist: ${newWishlist.id.take(8)}... - ${newWishlist.title}")

        try {
            val currentList = _uiState.value.wishlists
            Log.d(TAG, "    Current list size: ${currentList.size}")

            val updatedList = listOf(newWishlist) + currentList
            Log.d(TAG, "    Updated list size: ${updatedList.size}")

            _uiState.update {
                Log.d(TAG, "    Calling _uiState.update with ${updatedList.size} items")
                it.copy(wishlists = updatedList)
            }
            Log.d(
                TAG,
                "<<< addWishlistOptimistically() DONE, new size: ${_uiState.value.wishlists.size}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "EXCEPTION in addWishlistOptimistically: ${e.message}", e)
        }
    }

    fun deleteWishlist(id: String) {
        viewModelScope.launch {
            val currentList = _uiState.value.wishlists
            _uiState.value = _uiState.value.copy(
                wishlists = currentList.filter { it.id != id }
            )

            when (val result = wishlistRepository.deleteWishlist(id)) {
                is Result.Success -> {
                    loadWishlists()
                }

                is Result.Error -> {
                    val appError = result.toAppError()
                    _uiState.value = _uiState.value.copy(
                        wishlists = currentList,
                        error = appError
                    )
                }

                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun retry() {
        Log.d(TAG, "retry() called")
        loadWishlists()
    }

}