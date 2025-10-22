package com.partum.tabsplit.ui.join

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.session.SessionViewModel
import com.partum.tabsplit.utils.parseIsoDate
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun JoinSessionScreen(
    onAuthRequired: (String) -> Unit,
    onJoinSuccess: (Session) -> Unit,
    onJoinFailed: () -> Unit,
    inviteCode: String,
    authViewModel: AuthViewModel = viewModel(),
    sessionViewModel: SessionViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authUiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(inviteCode, authUiState.token) {
        if (inviteCode.isBlank()) return@LaunchedEffect

        scope.launch {
            try {
                if (authUiState.token.isNullOrEmpty()) {
                    // No token -> redirect to auth
                    sessionViewModel.setPendingInviteCode(inviteCode)
                    onAuthRequired(inviteCode)
                    return@launch
                }

                sessionViewModel.joinSessionByInvite(inviteCode)?.let { res ->

                    onJoinSuccess(
                        Session(
                            res.id,
                            res.title,
                            res.description,
                            res.currency,
                            res.inviteCode,
                            res.qrDataUrl,
                            res.inviteUrl,
                            res.createdBy,
                            parseIsoDate(res.createdAt) ?: Date()

                        )
                    )
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
