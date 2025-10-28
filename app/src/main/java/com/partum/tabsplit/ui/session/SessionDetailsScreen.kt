package com.partum.tabsplit.ui.session

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Payments
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.partum.tabsplit.ui.components.ui.AddExpenseDialog
import com.partum.tabsplit.ui.components.ui.BalancesList
import com.partum.tabsplit.ui.components.ui.SessionQRCode
import com.partum.tabsplit.ui.components.ui.ZcashIntegration
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.partum.tabsplit.R
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.ui.auth.AuthUiState
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.ui.zec.ZecViewModel
import com.partum.tabsplit.utils.calculateBalances
import com.partum.tabsplit.utils.formatDate
import com.partum.tabsplit.utils.formatTime
import com.partum.tabsplit.utils.shortString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    navController: NavController,
    sessionId: String,
    sessionViewModel: SessionViewModel,
    expenseViewModel: ExpenseViewModel,
    participantViewModel: ParticipantViewModel,
    authUiState: AuthUiState,
    zecViewModel: ZecViewModel
) {
    // Collect reactive state from AppStore
    val sessionUiState by sessionViewModel.uiState.collectAsState()
    val participantUiState by participantViewModel.uiState.collectAsState()
    val expenseUiState by expenseViewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()

    // FAB control
    val listState = rememberLazyListState()
    val isFabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    // UI local state
    var showZcash by remember { mutableStateOf(false) }
    var showAddExpense by remember { mutableStateOf(false) }
    var inviteUrl by remember { mutableStateOf<String?>(null) }
    var recipientAddress by remember { mutableStateOf("") }

    // Fetch session details when screen is opened
    var session by remember { mutableStateOf<Session?>(null) }

    LaunchedEffect(sessionId) {
        sessionViewModel.fetchSession(sessionId)

        expenseViewModel.fetchExpenses(sessionId)
//        participantViewModel.fetchSessionParticipants(sessionId, session)
    }

    // Update invite URL and recipient address whenever sessions or user changes
    LaunchedEffect(sessionUiState) {
        session = sessionUiState.sessionWithExpensesAndParticipants?.session
        inviteUrl = session?.inviteUrl
        recipientAddress = session?.owner?.zaddr.orEmpty()
    }

    val title = session?.title ?: stringResource(R.string.session_not_found)
    val description = session?.description ?: ""
    val startDateTime = session?.startDateTime
    val endDateTime = session?.endDateTime

    val formattedStartDate = remember(startDateTime) {
        startDateTime?.let { formatDate(it.toString()) } ?: ""
    }

    val formattedStartTime = remember(startDateTime) {
        startDateTime?.let { formatTime(it.toString()) } ?: ""
    }

    val formattedEndDate = remember(endDateTime) {
        endDateTime?.let { formatDate(it.toString()) } ?: ""
    }

    val formattedEndTime = remember(endDateTime) {
        endDateTime?.let { formatTime(it.toString()) } ?: ""
    }

    val participants = participantUiState.participants[sessionId].orEmpty()
    val expenses = expenseUiState.expenses[sessionId].orEmpty()

    // Compute balances
    val balances = remember(sessionId, participants, expenses) {
        calculateBalances(
            sessionId, participantUiState.participants, expenseUiState.expenses
        )
    }

    val isSessionOwner = session?.owner?.id == authUiState.user?.id
    val currentUserId = authUiState.user?.id
    val currentParticipant = participants.find { it.userId == currentUserId }
    val currentBalance = balances[currentParticipant?.id] ?: 0.0


    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
    ) {

        session.let {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            inviteUrl?.let { SessionQRCode(inviteUrl = it) }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.start_date_time),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$formattedStartDate at $formattedStartTime",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(R.string.end_date_time),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$formattedEndDate at $formattedEndTime",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.participants, participants.size), fontSize = 16.sp)
        }

        // Expenses list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.expenses), fontSize = 16.sp)

            if (expenses.isNotEmpty()) {
                expenses.forEach { e ->
                    val participant = participants.find { it.id == e.payerId }
                    val payerName = if (participant?.email != null) {
                        shortString(
                            participant.email,
                            prefix = 2, suffix = 1
                        )
                    } else e.payerId.take(6)

                    val isHost = participant?.userId == session?.owner?.id
                    val currencySymbol = if (session?.currency == "USD") "$" else "$"
                    val formatterAmount = "$currencySymbol${"%.2f".format(e.amount.toDouble())}"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                e.memo, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier
                                = Modifier.padding(bottom = 4.dp)
                            )

                            val payerLabel = if (isHost) "$payerName (Host)" else payerName

                            Text(
                                stringResource(R.string.paid_by, formatterAmount, payerLabel),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Text(
                    stringResource(R.string.no_expense_yet),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        if (expenses.orEmpty().isNotEmpty()) {
            Column {
                Text(stringResource(R.string.balances), fontSize = 16.sp)
                BalancesList(
                    participants = participants, balances = balances
                )
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            if (expenses.orEmpty().isNotEmpty()) {
                Log.d(
                    "SessionDetailsScreen", "isOwner: ${!isSessionOwner}, balance: " +
                            "${currentBalance}"
                )

                if (!isSessionOwner) {
                    ExtendedFloatingActionButton(
                        onClick = { showZcash = true },
                        expanded = isFabExpanded,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = stringResource(R.string.settle_with_zec)
                            )
                        },
                        text = {
                            Text(text = stringResource(R.string.settle_with_zec))
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    )
                }
            }

            if (session?.owner?.id == authUiState.user?.id) {
                ExtendedFloatingActionButton(
                    onClick = { showAddExpense = true },
                    expanded = isFabExpanded,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_expense)
                        )
                    },
                    text = {
                        Text(text = stringResource(R.string.add_expense))
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    // Modals
    if (showZcash) {
        ZcashIntegration(
            visible = showZcash,
            balances = balances,
            recipientAddress = recipientAddress,
            sessionId = sessionId,
            onClose = { showZcash = false },
            zecViewModel = zecViewModel
        )
    }

    if (showAddExpense) {
        AddExpenseDialog(
            sessionId = sessionId,
            onClose = { showAddExpense = false },
            expensesViewModel = expenseViewModel,
            hasSetZaddr = authUiState.user?.zaddr.isNullOrBlank()
        )

    }
}
