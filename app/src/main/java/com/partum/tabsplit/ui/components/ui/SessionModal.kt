package com.partum.tabsplit.ui.components.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.partum.tabsplit.R
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.ui.components.DateRangePickerField
import com.partum.tabsplit.ui.components.TimeRangePickerField
import com.partum.tabsplit.ui.session.SessionViewModel
import com.partum.tabsplit.utils.parseIsoDate
import kotlinx.coroutines.launch
import java.util.Date
import java.time.LocalTime

@Composable
fun SessionModal(
    showCreateModal: Boolean,
    setShowCreateModal: (Boolean) -> Unit,
    onSessionCreated: (Session) -> Unit,
    sessionViewModel: SessionViewModel
) {
    if (!showCreateModal) return

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    // Validation state
    var titleError by remember { mutableStateOf<String?>(null) }
    var descError by remember { mutableStateOf<String?>(null) }
    var startDateError by remember { mutableStateOf<String?>(null) }
    var endDateError by remember { mutableStateOf<String?>(null) }
    var startTimeError by remember { mutableStateOf<String?>(null) }
    var endTimeError by remember { mutableStateOf<String?>(null) }


    AlertDialog(
        onDismissRequest = { setShowCreateModal(false) },
        title = {
            Text(
                text = stringResource(R.string.new_session),
                style = MaterialTheme.typography.titleLarge
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = titleError != null
                )
                if (titleError != null) {
                    Text(
                        text = titleError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(bottom = 12.dp),
                    maxLines = 3,
                    isError = descError != null
                )
                if (descError != null) {
                    Text(
                        text = descError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                DateRangePickerField(
                    label = stringResource(R.string.start_and_end_date),
                    startDate = startDate,
                    endDate = endDate
                ) { s, e ->
                    startDate = s
                    endDate = e
                }

                TimeRangePickerField(
                    label = stringResource(R.string.session_time),
                    startTime = startTime,
                    endTime = endTime,
                    onRangeSelected = { s, e ->
                        startTime = s
                        endTime = e
                    }
                )

            }
        }, confirmButton = {
            TextButton(
                onClick = {

                    // Reset errors
                    titleError = null
                    descError = null
                    startDateError = null
                    endDateError = null
                    startTimeError = null
                    endTimeError = null

                    var hasError = false

                    if (title.isBlank()) {
                        titleError = context.getString(R.string.title_is_required)
                        hasError = true
                    }

                    if (description.isBlank()) {
                        descError = context.getString(R.string.description_is_required)
                        hasError = true
                    }

                    if (startDate == null) {
                        startDateError = context.getString(R.string.start_date_is_required)
                        hasError = true
                    }

                    if (endDate == null) {
                        endDateError = context.getString(R.string.end_date_is_required)
                        hasError = true
                    }

                    if (startTime == null) {
                        startTimeError = context.getString(R.string.start_time_required)
                        hasError = true
                    }

                    if (endTime == null) {
                        endTimeError = context.getString(R.string.end_time_required)
                        hasError = true
                    }

                    if (hasError) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_fix_the_highlighted_errors),
                            Toast.LENGTH_SHORT
                        ).show()

                        return@TextButton
                    }

                    scope.launch {
                        try {

                            // call your AppStore createSession
                            val result = sessionViewModel.createSession(
                                title,
                                description,
                                startDate!!,
                                endDate!!,
                                startTime!!,
                                endTime!!
                            )

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
                                        parseIsoDate(it.createdAt.toString()) ?: Date(),
                                        it.startDateTime,
                                        it.endDateTime
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.d("SessionModal", e.message.toString())

                            Toast.makeText(
                                context,
                                "Failed to create session.",
                                Toast.LENGTH_SHORT
                            ).show()
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
