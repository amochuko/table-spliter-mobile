package com.partum.tabsplit.ui.navigation

import android.graphics.drawable.Icon
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.partum.tabsplit.ui.auth.AuthViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.partum.tabsplit.R
import com.partum.tabsplit.ui.components.WalletDialog

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    authViewModel: AuthViewModel,
    navController: NavController,
    onPersistZaddr: suspend (String) -> Boolean = { true },
    canNavigateBack: Boolean,
    title: String
) {

    val authUiState by authViewModel.uiState.collectAsState()
    val user = authUiState.user

    var expanded by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {

                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

            } else null
        },
        actions = {

            Box {
                IconButton(onClick = { expanded = true }) {
                    // Show badge if zaddr not set
                    BadgedBox(
                        badge = {
                            if (user != null && user.zaddr.isNullOrEmpty()) {
                                Badge(
                                    modifier = Modifier.offset(
                                        x = (-2).dp, y =
                                            2.dp
                                    ), containerColor = Color.Red
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.wallet)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        onClick = {
                            expanded = false
                            showWalletDialog = true
                        })

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.profile)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ) },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Profile.route)
                        })

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.logout)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ) },
                        onClick = {
                            expanded = false
                            authViewModel.logout()

                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    )

    if (showWalletDialog) {
        WalletDialog(
            authViewModel,
            user,
            onDismiss = { showWalletDialog = false },
            onSave = { zaddr -> onPersistZaddr(zaddr) }
        )
    }
}