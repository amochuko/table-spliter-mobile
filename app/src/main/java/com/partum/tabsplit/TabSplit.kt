package com.partum.tabsplit

import android.app.Application
import com.partum.tabsplit.di.AppContainer
import com.partum.tabsplit.utils.AuthSessionManager

class TabSplit : Application() {

    // Holds all app-level dependencies
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        AuthSessionManager.init(this)
        appContainer = AppContainer(this)

    }
}