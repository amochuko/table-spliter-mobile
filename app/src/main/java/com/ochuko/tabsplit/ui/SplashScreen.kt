package com.ochuko.tabsplit.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ochuko.tabsplit.viewModels.AppStore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.ochuko.tabsplit.R
import com.ochuko.tabsplit.ui.navigation.Screen

@Composable
fun SplashScreen(navController: NavController, appStore: AppStore) {
    val token by appStore.token.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    // scale animation for logo
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoScale"
    )

    // Trigger animation
    LaunchedEffect(Unit) {
        visible = true
        delay(1500)

        if (!hasNavigated) {
            hasNavigated = true

            if (token.isNullOrEmpty()) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.Sessions.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }

    // Fade + scale combined
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        AnimatedVisibility(
            visible,
            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(),
            exit = fadeOut(animationSpec = tween(700)),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // App logo
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale)
                )
            }
        }
    }
}