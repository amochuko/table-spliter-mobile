package com.ochuko.tabsplit.ui.components.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochuko.tabsplit.models.Session
import com.ochuko.tabsplit.store.AppStore
import kotlinx.coroutines.launch

@Composable
fun SessionModal(
    showCreateModal: Boolean,
    setShowCreateModal: (Boolean) -> Unit,
    onSessionCreated: (Session) -> Unit,
    appStore: AppStore = viewModel(),
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
                                val result = appStore.createSession(title, description)

                                // close + reset state
                                setShowCreateModal(false)
                                title = ""
                                description = ""

                                // navigate to SessionDetails
                                result?.let {
                                    onSessionCreated(it)
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
