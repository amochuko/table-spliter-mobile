package com.partum.tabsplit.ui.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.partum.tabsplit.ui.components.ui.SessionModal
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.partum.tabsplit.data.model.Session

@Composable
fun SessionsScreen(
    onSessionClick: (String) -> Unit,
    onCreateSession: (Session) -> Unit,
    sessionViewModel: SessionViewModel,
) {

    // Reactive state
    val uiState by sessionViewModel.uiState.collectAsState()
    var showCreateModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sessionViewModel.loadSessions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = "Welcome to TableSplit!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )

            when {
                uiState.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }

                uiState.sessions.isNotEmpty() -> {
                    uiState.sessions.forEach { s ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSessionClick(s.id)
                                }) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = s.title, style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = s.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.7f
                                    )
                                )
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = "No session yet!", modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateModal = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Session"
            )
        }

        if (showCreateModal) {
            SessionModal(
                showCreateModal = showCreateModal,
                setShowCreateModal = { showCreateModal = it },
                sessionViewModel = sessionViewModel,
                onSessionCreated = { newSession ->
                    showCreateModal = false
                    onCreateSession(newSession)

                    onSessionClick(newSession.id)
                    sessionViewModel.loadSessions()
                })
        }
    }
}