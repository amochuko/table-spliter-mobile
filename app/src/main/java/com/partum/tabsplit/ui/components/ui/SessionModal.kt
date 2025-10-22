package com.ochuko.tabsplit.ui.components.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ochuko.tabsplit.data.model.Session
import com.ochuko.tabsplit.ui.session.SessionViewModel
import com.ochuko.tabsplit.utils.parseIsoDate
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun SessionModal(
    showCreateModal: Boolean,
    setShowCreateModal: (Boolean) -> Unit,
    onSessionCreated: (Session) -> Unit,
    sessionViewModel: SessionViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (showCreateModal) {
        AlertDialog(
            onDismissRequest = { setShowCreateModal(false) },
            title = {
                Text(
                    text = "New Session",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Session title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            scope.launch {

                                // call your AppStore createSession
                                val result = sessionViewModel.createSession(title, description)

                                // close + reset state
                                setShowCreateModal(false)
                                title = ""
                                description = ""

                                // navigate to SessionDetails
                                result?.let {
                                    onSessionCreated(
                                        Session(
                                            it.id, it.title, it.description,
                                            it.currency,
                                            it.inviteCode,
                                            it.qrDataUrl,
                                            it.inviteUrl,
                                            it.createdBy,
                                            parseIsoDate(it.createdAt) ?: Date()
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowCreateModal(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}
