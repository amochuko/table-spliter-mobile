package com.ochuko.tabsplit.ui.screens.sessions


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ochuko.tabsplit.ui.components.ui.SessionModal
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import com.ochuko.tabsplit.models.Session
import com.ochuko.tabsplit.store.AppStore


@Composable
fun SessionScreen(
    onRequireAuth: () -> Unit,
    onSessionClick: (String) -> Unit,
    onCreateSession: (Session) -> Unit,
    appStore: AppStore = viewModel()
) {

    // Reactive state
    val sessions by appStore.sessions.collectAsState()
    val token by appStore.token.collectAsState()
    var showCreateModal by remember { mutableStateOf(false) }

    // Redirect to Login if not authenticated
    LaunchedEffect(token) {
        if (token == null) {
            // navigate to login
            onRequireAuth()
        } else {
            appStore.loadSessions()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Welcome to TableSplit!", fontSize = 20.sp)

        if (sessions.isNotEmpty()) {
            sessions.forEach { s ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // navigate to session detail
                    }) {
                    Text(s.title, modifier = Modifier.padding(12.dp))
                }
            }
        } else {
            Text("No sessions yet!", modifier = Modifier.align(Alignment.CenterHorizontally))
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
                }
            )
        }
    }
}
