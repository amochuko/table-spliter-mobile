package com.ochuko.tabsplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ochuko.tabsplit.ui.theme.TabSplitTheme
import androidx.navigation.compose.rememberNavController
import com.ochuko.ui.navigation.tabsplit.AppNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TabSplitTheme {
                val navController = rememberNavController()

                AppNavHost(navController)
            }
        }
    }
}