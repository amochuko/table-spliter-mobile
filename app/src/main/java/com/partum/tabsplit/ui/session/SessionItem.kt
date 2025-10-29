package com.partum.tabsplit.ui.session

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.data.model.SessionStatus

@Composable
fun SessionItem(
    session: Session,
    isOwner: Boolean,
    onLeave: (Session) -> Unit,
    onDelete: (Session) -> Unit,
    onClick: (String) -> Unit,
) {
    val context = LocalContext.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { // Swiped left
                    if (isOwner) {
                        if (session.status == SessionStatus.ACTIVE) {
                            Toast.makeText(
                                context,
                                "You cannot delete an active session.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            onDelete(session)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Only the session owner can delete it.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }

                SwipeToDismissBoxValue.StartToEnd -> {// Swiped right
                    if (!isOwner) {
                        if (session.status == SessionStatus.ACTIVE) {
                            Toast.makeText(
                                context,
                                "You cannot leave an active session",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            onLeave(session)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Owners cannot leave their own session.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    true
                }

                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    // Leave
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Leave",
                            tint = Color.White
                        )
                    }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    // Delete
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .padding(horizontal = 24.dp),

                        ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }
                }

                else -> {}
            }
        },
        content = { SessionCard(session = session, onClick = onClick) }
    )
}