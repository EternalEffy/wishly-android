package com.wishly.app

import android.app.Application
import com.wishly.app.di.DependencyContainer

class WishlyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        DependencyContainer.init(this)
    }

    companion object {
        lateinit var instance: WishlyApplication
            private set
    }
}