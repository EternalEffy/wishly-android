package com.wishly.app.data.api

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wishly.app.dataStore
import com.wishly.app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TokenManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

    init {
        observeToken()
    }

    private fun observeToken() {
        scope.launch {
            context.dataStore.data
                .map { preferences ->
                    preferences[stringPreferencesKey(Constants.ACCESS_TOKEN)]
                }
                .collect { token ->
                    cachedAccessToken = token
                }
        }
    }

    fun getAccessToken(): String? = cachedAccessToken
    fun getRefreshToken(): String? = cachedRefreshToken

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(Constants.ACCESS_TOKEN)] = accessToken
            preferences[stringPreferencesKey(Constants.REFRESH_TOKEN)] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(Constants.ACCESS_TOKEN))
            preferences.remove(stringPreferencesKey(Constants.REFRESH_TOKEN))
        }
    }

    fun isLoggedIn(): Boolean = !cachedAccessToken.isNullOrEmpty()

}
