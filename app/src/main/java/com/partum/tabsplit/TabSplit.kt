package com.partum.tabsplit

import android.app.Application
import com.partum.tabsplit.di.AppContainer
import com.partum.tabsplit.utils.TokenProvider

class TabSplit : Application() {

    // Holds all app-level dependencies
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        TokenProvider.init(this)
        appContainer = AppContainer(this)

    }
}