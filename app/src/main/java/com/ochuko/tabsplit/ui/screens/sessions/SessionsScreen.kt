package com.ochuko.tabsplit.ui.screens.sessions

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
import com.ochuko.tabsplit.models.Session
import com.ochuko.tabsplit.store.AppStore


@Composable
fun SessionsScreen(
    onSessionClick: (String) -> Unit,
    onCreateSession: (Session) -> Unit,
    appStore: AppStore
) {

    // Reactive state
    val sessions by appStore.sessions.collectAsState()
    var showCreateModal by remember { mutableStateOf(false) }

    // Redirect to Login if not authenticated
    LaunchedEffect(Unit) {
        appStore.loadSessions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("Welcome to TableSplit!", fontSize = 20.sp)

        if (sessions.isNotEmpty() && sessions.size > 0) {
            sessions.forEach { s ->
                Card(
                    modifier = Modifier
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
