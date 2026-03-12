package com.wishly.app

import android.app.Application
import coil.request.Disposable
import com.wishly.app.di.DependencyContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        DependencyContainer.init(this)

        CoroutineScope(Dispatchers.IO).launch {
            DependencyContainer.getTokenManager().loadTokens()
        }
    }

    companion object {
        lateinit var instance: WishlyApplication
            private set
    }
}