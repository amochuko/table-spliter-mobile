@file:kotlin.OptIn(ExperimentalMaterial3Api::class)

package com.ochuko.tabsplit.ui.navigation

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.ochuko.tabsplit.store.AuthStore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.ochuko.tabsplit.ui.components.WalletDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    authStore: AuthStore,
    navController: NavController,
    onPersistZaddr: suspend (String) -> Boolean = { true }
) {

    val authState by authStore.authState.collectAsState()
    val user = authState.user

    var expanded by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("TabSplit") },
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
                        text = { Text("Wallet") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            expanded = false
                            showWalletDialog = true
                        })

                    DropdownMenuItem(
                        text = { Text("Profile") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Profile.route)
                        })

                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        onClick = {
                            expanded = false
                            authStore.logout()

                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        }
    )

    if (showWalletDialog) {
        WalletDialog(
            authStore,
            user,
            onDismiss = { showWalletDialog = false },
            onSave = { zaddr -> onPersistZaddr(zaddr) }
        )
    }
}