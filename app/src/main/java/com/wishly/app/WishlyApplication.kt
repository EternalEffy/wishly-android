package com.wishly.app

import android.app.Application
import android.util.Log
import com.wishly.app.di.DependencyContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking

class WishlyApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        Log.d("WishlyApp", "=== APPLICATION START ===")
        DependencyContainer.init(this)
        Log.d("WishlyApp", "DependencyContainer initialized")

        runBlocking(Dispatchers.IO) {
            Log.d("WishlyApp", "About to load tokens from DataStore")
            DependencyContainer.getTokenManager().loadFromDataStore()
            Log.d("WishlyApp", "Tokens loaded from DataStore")
        }
        Log.d("WishlyApp", "=== APPLICATION START COMPLETE ===")
    }

    override fun onTerminate() {
        super.onTerminate()
        DependencyContainer.getTokenManager().cleanup()
        applicationScope.cancel()
    }

    companion object {
        lateinit var instance: WishlyApplication
            private set
    }
}