package com.wishly.app.presentation.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wishly.app.di.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWishlistScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onWishlistCreated: () -> Unit
) {
    val viewModel: CreateWishlistViewModel = viewModel {
        CreateWishlistViewModel(
            wishlistRepository = ViewModelFactory.getWishlistRepository()
        ).apply {
            this.navController = navController
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onWishlistCreated()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onNavigateBack) {
                Text("Cancel")
            }

            Text(
                text = "Create Wishlist",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.width(60.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // title
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Title *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // description
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Description (optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // privacy
        Text(
            text = "Privacy",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = uiState.privacy == "PUBLIC",
                onClick = { viewModel.onPrivacyChange("PUBLIC") },
                label = { Text("Public") },
                enabled = !uiState.isLoading
            )
            FilterChip(
                selected = uiState.privacy == "PRIVATE",
                onClick = { viewModel.onPrivacyChange("PRIVATE") },
                label = { Text("Private") },
                enabled = !uiState.isLoading
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // event date
        var showDatePicker by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = uiState.eventDate,
            onValueChange = { },
            label = { Text("Event Date (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                }
            }
        )

        val todayMillis = System.currentTimeMillis()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = todayMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayMillis
                }
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Date(millis)
                                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val dateString = format.format(date)
                                viewModel.onEventDateChange(dateString)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // error
        if (uiState.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("✕")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // create wishlist btn
        Button(
            onClick = { viewModel.createWishlist() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Creating...")
                }
            } else {
                Text("Create Wishlist", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

}