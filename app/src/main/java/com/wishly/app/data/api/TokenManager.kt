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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

class TokenManager(private val context: Context) {

    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun getAccessToken(): String? = cachedAccessToken

    fun getRefreshToken(): String? = cachedRefreshToken

    fun isLoggedIn(): Boolean = !cachedAccessToken.isNullOrEmpty()

    fun saveTokens(accessToken: String, refreshToken: String) {
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken

        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(Constants.ACCESS_TOKEN)] = accessToken
                preferences[stringPreferencesKey(Constants.REFRESH_TOKEN)] = refreshToken
            }
        }
    }

    fun clearTokens() {
        cachedAccessToken = null
        cachedRefreshToken = null

        scope.launch {
            context.dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(Constants.ACCESS_TOKEN))
                preferences.remove(stringPreferencesKey(Constants.REFRESH_TOKEN))
            }
        }
    }

    suspend fun loadTokens() {
        context.dataStore.data.first().let { preferences ->
            cachedAccessToken = preferences[stringPreferencesKey(Constants.ACCESS_TOKEN)]
            cachedRefreshToken = preferences[stringPreferencesKey(Constants.REFRESH_TOKEN)]
        }
    }

    fun cleanup() {
        scope.cancel()
    }
}
