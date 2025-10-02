package com.ochuko.tabsplit.ui.components.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ochuko.tabsplit.store.SessionViewModel

@Composable
fun SessionModal(
    navController: NavController,
    showCreateModal: Boolean,
    setShowCreateModal: (Boolean) -> Unit,
    sessionViewModel: SessionViewModel = viewModel(),
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
                            // call your AppStore createSession
                            val result = sessionViewModel.createSession(title, description)

                            // close + reset state
                            setShowCreateModal(false)
                            title = ""
                            description = ""

                            // navigate to SessionDetails
                            result?.let {
                                navController.navigate("sessionDetails/${it.session.id}")
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
