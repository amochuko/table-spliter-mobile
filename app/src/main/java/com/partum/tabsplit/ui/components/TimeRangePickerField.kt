package com.partum.tabsplit.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.partum.tabsplit.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangePickerField(
    label: String,
    startTime: LocalTime?,
    endTime: LocalTime?,
    onRangeSelected: (start: LocalTime, end: LocalTime) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val displayText = remember(startTime, endTime) {
        when {
            startTime != null && endTime != null -> "${startTime.format(timeFormatter)} → ${
                endTime.format(
                    timeFormatter
                )
            }"

            startTime != null -> "${startTime.format(timeFormatter)} → (select end)"
            else -> ""
        }
    }

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = stringResource(R.string.select_time_range)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )

    if (showDialog) {
        var tempStart by remember { mutableStateOf(startTime ?: LocalTime.now()) }
        var tempEnd by remember { mutableStateOf(endTime ?: tempStart.plusHours(1)) }

        var selectingStart by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    if (tempEnd.isAfter(tempStart) || tempEnd == tempStart) {
                        onRangeSelected(tempStart, tempEnd)
                        showDialog = false
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        stringResource(
                            R.string
                                .cancel
                        )
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (selectingStart) stringResource(R.string.select_start_time) else stringResource(
                            R.string.select_end_time
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // actual time picker
                    val state = rememberTimePickerState(
                        initialHour = if (selectingStart) tempStart.hour else tempEnd.hour,
                        initialMinute = if (selectingStart) tempStart.minute else tempEnd.minute
                    )

                    TimePicker(state = state)

                    Spacer(Modifier.height(12.dp))

                    Button(onClick = {
                        if (selectingStart) {
                            tempStart = LocalTime.of(state.hour, state.minute)
                            selectingStart = false
                        } else {
                            tempEnd = LocalTime.of(state.hour, state.minute)
                        }
                    }) {
                        Text(
                            if (selectingStart) stringResource(R.string.next_end_time) else stringResource(
                                R.string.done
                            )
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Selected: ${tempStart.format(timeFormatter)} – ${
                            tempEnd.format(
                                timeFormatter
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}
