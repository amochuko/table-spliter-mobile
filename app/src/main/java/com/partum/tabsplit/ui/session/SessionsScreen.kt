package com.partum.tabsplit.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.partum.tabsplit.R
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

    // FAB control
    val listState = rememberLazyListState()
    val isFabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    LaunchedEffect(Unit) {
        sessionViewModel.loadSessions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_to_tablesplit),
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
                    Text(
                        text = stringResource(
                            R.string.list_of_available_session, if (uiState.sessions.size > 1) 's'
                            else ""
                        ),
                        modifier = Modifier
                            .padding(
                                bottom = 12.dp,
                                top = 12.dp
                            ),
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )

                    uiState.sessions.forEach { s ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSessionClick(s.id)
                                }) {
                            Column(
                                modifier = Modifier.padding(8.dp)
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
                        text = stringResource(R.string.no_session_yet), modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            ExtendedFloatingActionButton(
                onClick = { showCreateModal = true },
                expanded = isFabExpanded,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_session)
                    )
                },
                text = {
                    Text(text = stringResource(R.string.add_session))
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
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