package com.ochuko.tabsplit.ui.components.ui


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochuko.tabsplit.ui.viewmodel.ZcashViewModel

@Composable
fun ZcashAddressSettings(vm: ZcashViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    var newZAddr by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            state.currentZAddr != null && !state.editMode -> {
                // Show current z-addr
                Text(
                    text = "Current ZAddress:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE1E0E0), RoundedCornerShape(6.dp))
                        .background(Color(0xFFFAFAFA))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = shortString(state.currentZAddr),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF6EA9E8),
                            modifier = Modifier.clickable { vm.toggleEditMode(true) }
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red,
                            modifier = Modifier.clickable {
                                vm.deleteZAddr()
                                Toast.makeText(context, "Deleted ZAddr", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            else -> {
                // Edit or Add mode
                Text(
                    text = if (state.editMode) "Edit Zcash Address" else "Set Your Zcash Address",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newZAddr,
                    onValueChange = { newZAddr = it },
                    placeholder = { Text("Enter your z-addr or u1-addr") },
                    modifier = Modifier.fillMaxWidth()
                )

                state.error?.let {
                    Text(text = it, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        vm.saveZAddr(newZAddr)
                        newZAddr = ""
                    }) {
                        Text("Save")
                    }
                    if (state.editMode) {
                        OutlinedButton(onClick = { vm.toggleEditMode(false) }) {
                            Text("Cancel", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

fun shortString(addr: String, prefix: Int = 8, suffix: Int = 6): String {
    return if (addr.length > prefix + suffix) {
        "${addr.take(prefix)}...${addr.takeLast(suffix)}"
    } else addr
}
