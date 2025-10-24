package com.partum.tabsplit.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.partum.tabsplit.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerField(
    label: String,
    startDate: Date?,
    endDate: Date?,
    onRangeSelected: (start: Date, end: Date) -> Unit
) {
    val context = LocalContext.current
    var showRangePicker by remember { mutableStateOf(false) }

    // Display label text
    val displayText = remember(startDate, endDate) {
        when {
            startDate != null && endDate != null -> {
                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                "${fmt.format(startDate)} → ${fmt.format(endDate)}"
            }

            startDate != null -> {
                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                "${fmt.format(startDate)} → (select end)"
            }

            else -> ""
        }
    }

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showRangePicker = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.pick_date_range)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )

    if (showRangePicker) {
        // Initialize state with existing selection if available
        val state = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate?.time,
            initialSelectedEndDateMillis = endDate?.time,
            initialDisplayedMonthMillis = startDate?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                Button(onClick = {
                    val s = state.selectedStartDateMillis
                    val e = state.selectedEndDateMillis

                    if (s != null && e != null && e >= s) {
                        onRangeSelected(Date(s), Date(e))
                        showRangePicker = false
                    } else {
                        // show error feedback if somehow invalid
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_select_a_valid_start_and_end_date),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    Text(context.getString(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) {
                    Text(
                        context.getString(
                            R
                                .string.cancel
                        )
                    )
                }
            }
        ) {
            DateRangePicker(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                title = {
                    Text(
                        text = stringResource(R.string.select_dates),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .padding(start = 24.dp, top = 12.dp, bottom = 12.dp)
                    )
                },
                headline = {
                    // ✅ Custom headline implementation
                    val fmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
                    val start =
                        state.selectedStartDateMillis?.let { fmt.format(Date(it)) } ?: context
                            .getString(R.string.start_date)
                    val end =
                        state.selectedEndDateMillis?.let { fmt.format(Date(it)) } ?: context
                            .getString(R.string.end_date)

                    Text(
                        text = "$start  –  $end",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.padding(start = 24.dp, bottom = 4.dp, end = 24.dp)
                    )
                },
                colors = DatePickerDefaults.colors()
            )
        }
    }
}
