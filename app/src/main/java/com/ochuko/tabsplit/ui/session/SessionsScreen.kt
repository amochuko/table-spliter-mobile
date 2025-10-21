package com.ochuko.tabsplit.ui.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochuko.tabsplit.ui.components.ui.SessionModal
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import com.ochuko.tabsplit.data.model.Session

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {

        Text(
            "Welcome to TableSplit!", fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp)
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
                    text = "No session yet!",
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }


    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = { showCreateModal = true }) {
        Text("Add Session")
    }

    if (showCreateModal) {
        SessionModal(
            showCreateModal = showCreateModal,
            setShowCreateModal = { showCreateModal = it },
            onSessionCreated = { newSession ->
                showCreateModal = false
                onCreateSession(newSession)

                onSessionClick(newSession.id)
                sessionViewModel.loadSessions()
            })
    }
}
