package com.partum.tabsplit

import android.app.Application
import com.partum.tabsplit.di.AppContainer

class TabSplit : Application() {

    // Holds all app-level dependencies
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this )
    }
}