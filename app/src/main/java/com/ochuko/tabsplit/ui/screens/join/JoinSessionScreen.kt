package com.ochuko.tabsplit.ui.screens.join

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.ochuko.tabsplit.store.AppStore
import com.ochuko.tabsplit.store.AuthViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochuko.tabsplit.models.Session
import kotlinx.coroutines.launch

@Composable
fun JoinSessionScreen(
    appStore: AppStore,
    onAuthRequired: (String) -> Unit,
    onJoinSuccess: (Session) -> Unit,
    onJoinFailed: () -> Unit,
    inviteCode: String,
    authViewModel: AuthViewModel = viewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val token by authViewModel.token.collectAsState()

    LaunchedEffect(inviteCode, token) {
        if (inviteCode.isBlank()) return@LaunchedEffect

        scope.launch {
            try {
                if (token.isNullOrEmpty()) {
                    // No token -> redirect to auth
                    appStore.setPendingInviteCode(inviteCode)
                    onAuthRequired(inviteCode)

                    return@launch
                }

                val res = appStore.joinSessionByInvite(inviteCode)
                if (res != null) {
                    appStore.addSession(res)
                    onJoinSuccess(res)
                } else {
                    Toast.makeText(context, "Unable to join session", Toast.LENGTH_SHORT).show()
                    onJoinFailed()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Unable to join session", Toast.LENGTH_SHORT).show()

                onJoinFailed()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(10.dp))
            Text("Joining session...")
        }
    }
}
