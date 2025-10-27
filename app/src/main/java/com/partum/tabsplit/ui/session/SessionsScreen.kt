package com.partum.tabsplit.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        sessionViewModel.loadSessions()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 8.dp, vertical = 4.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            val tabs = listOf(
                stringResource(R.string.my_sessions),
                stringResource(R.string.joined_session)
            )

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title, color = MaterialTheme
                                    .colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

                else -> {

                    val sessions = if (selectedTabIndex == 0)
                        uiState.ownedSessions
                    else
                        uiState.joinedSessions

                    if (sessions.isEmpty()) {
                        Text(
                            text = if (selectedTabIndex == 0)
                                stringResource(R.string.you_haven_t_created_any_session_yet)
                            else
                                stringResource(R.string.you_haven_t_joined_any_session_yet),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        LazyColumn(state = listState) {
                            items(sessions) { s ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            onSessionClick(s.id)
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Text(
                                            text = s.title,
                                            style = MaterialTheme.typography.titleMedium
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
                    }

                }
            }
        }

        AnimatedVisibility(
            visible = selectedTabIndex == 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomEnd)
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
                }
            )
        }
    }
}