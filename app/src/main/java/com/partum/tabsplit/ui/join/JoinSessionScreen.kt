package com.partum.tabsplit.ui.join

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.partum.tabsplit.R
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.session.SessionViewModel
import kotlinx.coroutines.launch

@Composable
fun JoinSessionScreen(
    onAuthRequired: (String) -> Unit,
    onJoinSuccess: (Session) -> Unit,
    onJoinFailed: () -> Unit,
    inviteCode: String,
    authViewModel: AuthViewModel = viewModel(),
    sessionViewModel: SessionViewModel
) {
    val scope = rememberCoroutineScope()

    val authUiState by authViewModel.uiState.collectAsState()
    val sessionUiState by sessionViewModel.uiState.collectAsState()

    LaunchedEffect(inviteCode, authUiState.token, sessionUiState.error) {
        if (inviteCode.isBlank()) return@LaunchedEffect

        scope.launch {
            try {
                if (authUiState.token.isNullOrEmpty()) {
                    // No token -> redirect to auth
                    sessionViewModel.setPendingInviteCode(inviteCode)
                    onAuthRequired(inviteCode)

                    return@launch
                }

                sessionViewModel.joinSessionByInvite(inviteCode)
            } catch (e: Exception) {
                e.printStackTrace()
                onJoinFailed()
            }
        }
    }

    when {
        sessionUiState.hasJoinedSession -> {
            sessionUiState.session?.let { s -> onJoinSuccess(s) }
        }

        sessionUiState.error != null -> {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(60.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = sessionUiState.error ?: stringResource(R.string.unable_to_join_session),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onJoinFailed()
                        }, shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = stringResource(R.string.go_back))
                    }
                }
            }
        }

        else -> {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(10.dp))

                    val loadingText = sessionUiState.session?.title?.let {
                        stringResource(R.string.joining, it)
                    } ?: stringResource(R.string.joining_session)

                    Text(text = loadingText)
                }
            }
        }
    }
}
