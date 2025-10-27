package com.partum.tabsplit.ui.profile

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.partum.tabsplit.ui.auth.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val user = authUiState.user

    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var zaddr by remember { mutableStateOf(user?.zaddr ?: "") }
    var isEditing by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        OutlinedTextField(
            value = username,
            onValueChange = { if (isEditing) username = it },
            label = { Text(stringResource(com.partum.tabsplit.R.string.username)) },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { if (isEditing) email = it },
            label = { Text(stringResource(com.partum.tabsplit.R.string.email)) },
            enabled = false, // usually immutable
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = zaddr,
            onValueChange = { if (isEditing) zaddr = it },
            label = { Text(stringResource(com.partum.tabsplit.R.string.zcash_address)) },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditing) {
                OutlinedButton(
                    onClick = {
                        isEditing = false
                        authViewModel.updateProfile(username, zaddr, email)
                    }
                ) {
                    Text(stringResource(com.partum.tabsplit.R.string.save_changes))
                }
            } else {
                Button(onClick = { isEditing = true }) {
                    Text(stringResource(com.partum.tabsplit.R.string.edit_profile))
                }
            }

            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                onClick = { authViewModel.logout() }
            ) {
                Text(stringResource(com.partum.tabsplit.R.string.log_out))
            }
        }
    }

}
