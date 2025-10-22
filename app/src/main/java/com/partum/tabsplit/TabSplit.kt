package com.ochuko.tabsplit

import android.app.Application
import com.ochuko.tabsplit.di.AppContainer

class TabSplit : Application() {

    // Holds all app-level dependencies
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this )
    }
}