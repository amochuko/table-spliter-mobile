package com.partum.tabsplit.ui.session

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.partum.tabsplit.ui.components.ui.AddExpenseDialog
import com.partum.tabsplit.ui.components.ui.BalancesList
import com.partum.tabsplit.ui.components.ui.SessionQRCode
import com.partum.tabsplit.ui.components.ui.ZcashIntegration
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import com.partum.tabsplit.R
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.utils.calculateBalances
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    navController: NavController,
    sessionId: String,
    sessionViewModel: SessionViewModel,
    expenseViewModel: ExpenseViewModel,
    participantViewModel: ParticipantViewModel
) {
    // Collect reactive state from AppStore
    val sessionUiState by sessionViewModel.uiState.collectAsState()
    val participantUiState by participantViewModel.uiState.collectAsState()
    val expenseUiState by expenseViewModel.uiState.collectAsState()

    val scrollState = rememberScrollState()

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

    val sessionTitle = session?.title ?: stringResource(R.string.session_not_found)
    val participants = participantUiState.participants[sessionId].orEmpty()
    val expenses = expenseUiState.expenses[sessionId].orEmpty()

    // Compute balances
    val balances = remember(sessionId, participants, expenses) {
        calculateBalances(
            sessionId, participantUiState.participants, expenseUiState.expenses
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
    ) {

        inviteUrl?.let { SessionQRCode(inviteUrl = it) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.participants, participants.size), fontSize = 16.sp)
        }

        // Expenses list
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.expenses), fontSize = 16.sp)

            if (expenses.isNotEmpty()) {
                expenses.forEach { e ->
                    val payerName =
                        participants.find { it.id == e.payerId }?.username ?: e.payerId.take(6)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(e.memo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                stringResource(R.string.paid_by, e.amount, payerName),
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
                Button(
                    onClick = { showZcash = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text(stringResource(R.string.settle_with_zec))
                }
            }

            Button(onClick = { showAddExpense = true }) {
                Text(stringResource(R.string.add_expense))
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
            onClose = { showZcash = false })
    }

    if (showAddExpense) {
        AddExpenseDialog(
            sessionId = sessionId,
            onClose = { showAddExpense = false },
            expensesViewModel = expenseViewModel
        )
    }
}
