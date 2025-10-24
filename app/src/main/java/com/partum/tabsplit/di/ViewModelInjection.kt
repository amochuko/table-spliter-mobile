package com.partum.tabsplit.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelFactory provided!")
}

@Composable
inline fun <reified T : ViewModel> injectedViewModel(): T {
    val factory = LocalViewModelFactory.current

    return viewModel(factory = factory)
}