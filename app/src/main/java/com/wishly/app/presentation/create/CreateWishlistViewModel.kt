package com.wishly.app.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishly.app.data.model.CreateWishlistRequest
import com.wishly.app.data.repository.WishlistRepository
import com.wishly.app.navigation.NavKeys
import com.wishly.app.util.DateFormatter
import com.wishly.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CreateWishlistUiState(
    val title: String = "",
    val description: String = "",
    val privacy: String = "PUBLIC",
    val eventDate: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class CreateWishlistViewModel(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(CreateWishlistUiState())
    val uiState: StateFlow<CreateWishlistUiState> = _uiState.asStateFlow()


    var navController: androidx.navigation.NavController? = null
    var onWishlistCreated: (() -> Unit)? = null
    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title, error = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onPrivacyChange(privacy: String) {
        _uiState.value = _uiState.value.copy(privacy = privacy)
    }

    fun onEventDateChange(eventDate: String) {
        _uiState.value = _uiState.value.copy(eventDate = eventDate)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun createWishlist() {
        val title = _uiState.value.title
        val eventDate = _uiState.value.eventDate

        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Title is required")
            return
        }

        if (eventDate.isNotBlank()) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                inputFormat.isLenient = false

                val selectedDate = inputFormat.parse(eventDate)
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                if (selectedDate != null && selectedDate.before(today)) {
                    _uiState.update {
                        it.copy(error = "Event date cannot be in past")
                    }
                    return
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Uncorrected format date")
                }
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val eventDateForApi = _uiState.value.eventDate
                .takeIf { it.isNotBlank() }
                ?.let { DateFormatter.toLocalDateTime(it) }


            val request = CreateWishlistRequest(
                title = title,
                description = _uiState.value.description.takeIf { it.isNotBlank() },
                privacy = _uiState.value.privacy,
                eventDate = eventDateForApi
            )

            when (val result = wishlistRepository.createWishlist(request)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }

                    result.data?.let { newWishlist ->
                        val json = NavKeys.wishlistToArg(newWishlist)
                        navController?.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(NavKeys.KEY_NEW_WISHLIST, json)
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Result.Loading -> {}
            }

        }
    }

}