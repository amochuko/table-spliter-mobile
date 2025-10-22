package com.partum.tabsplit.ui.components.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.gson.annotations.SerializedName
import com.partum.tabsplit.R
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.ui.session.SessionViewModel
import com.partum.tabsplit.utils.parseIsoDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SessionModal(
    showCreateModal: Boolean,
    setShowCreateModal: (Boolean) -> Unit,
    onSessionCreated: (Session) -> Unit,
    sessionViewModel: SessionViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    // Date picker
    val startCalendar = remember { Calendar.getInstance() }
    val endCalendar = remember { Calendar.getInstance() }


    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->

            startCalendar.set(year, month, day)
            startDate = startCalendar.time
        },
        startCalendar.get(Calendar.YEAR),
        startCalendar.get(Calendar.MONTH),
        startCalendar.get(Calendar.DAY_OF_MONTH)
    )
    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->

            endCalendar.set(year, month, day)
            endDate = endCalendar.time
        },
        endCalendar.get(Calendar.YEAR),
        endCalendar.get(Calendar.MONTH),
        endCalendar.get(Calendar.DAY_OF_MONTH)
    )



    if (showCreateModal) {
        AlertDialog(onDismissRequest = { setShowCreateModal(false) }, title = {
            Text(
                text = "New Session", style = MaterialTheme.typography.titleLarge
            )
        }, text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.session_title)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(startDate),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.start_date)) },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { startDatePickerDialog.show() })

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDate),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.end_date)) },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { endDatePickerDialog.show() })
            }
        }, confirmButton = {
            TextButton(
                onClick = {

                    when {
                        title.isBlank() -> {
                            Toast.makeText(context,
                                context.getString(R.string.title_is_required), Toast.LENGTH_SHORT).show()

                            return@TextButton
                        }

                        description.isBlank() -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.description_is_required),
                                Toast.LENGTH_SHORT
                            ).show()

                            return@TextButton
                        }

                        startDate == null -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.start_date_is_required),
                                Toast.LENGTH_SHORT
                            ).show()

                            return@TextButton
                        }

                        endDate == null -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.end_date_is_required),
                                Toast.LENGTH_SHORT
                            ).show()

                            return@TextButton
                        }

                        else -> {
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
                                            it.id,
                                            it.title,
                                            it.description,
                                            it.currency,
                                            it.inviteCode,
                                            it.qrDataUrl,
                                            it.inviteUrl,
                                            it.createdBy ?: "",
                                            parseIsoDate(it.createdAt) ?: Date(),
                                            it.startDate,
                                            it.endDate
                                        )
                                    )
                                }
                            }

                        }
                    }
               
                }) {
                Text(stringResource(R.string.create))
            }
        }, dismissButton = {
            TextButton(onClick = { setShowCreateModal(false) }) {
                Text(stringResource(R.string.cancel))
            }
        })
    }
}
