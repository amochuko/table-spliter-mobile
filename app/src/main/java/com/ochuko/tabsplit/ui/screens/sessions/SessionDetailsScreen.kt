package com.ochuko.tabsplit.ui.screens.sessions

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
import com.ochuko.tabsplit.store.AppStore
import com.ochuko.tabsplit.ui.components.ui.AddExpenseDialog
import com.ochuko.tabsplit.ui.components.ui.BalancesList
import com.ochuko.tabsplit.ui.components.ui.SessionQRCode
import com.ochuko.tabsplit.ui.components.ui.ZcashIntegration
import androidx.compose.material3.*
import com.ochuko.tabsplit.data.api.SessionWithOwner
import com.ochuko.tabsplit.models.Session
import com.ochuko.tabsplit.utils.calculateBalances
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    navController: NavController,
    sessionId: String,
    appStore: AppStore
) {
    // Collect reactive state from AppStore
    val sessions by appStore.sessions.collectAsState()
    val user by appStore.user.collectAsState()
    val participants by appStore.participants.collectAsState()
    val expenses by appStore.expenses.collectAsState()

    var session by remember { mutableStateOf<SessionWithOwner?>(null) }

    // UI local state
    var showZcash by remember { mutableStateOf(false) }
    var showAddExpense by remember { mutableStateOf(false) }
    var inviteUrl by remember { mutableStateOf<String?>(null) }
    var recipientAddress by remember { mutableStateOf("") }

    // Fetch session details when screen is opened
    LaunchedEffect(sessionId) {
        val result = appStore.fetchSession(sessionId)
        session = result?.session
    }

    // Update invite URL and recipient address whenever sessions or user changes
    LaunchedEffect(sessions, session?.owner?.zaddr) {
        recipientAddress = session?.owner?.zaddr.orEmpty()

        sessions.find { it.id == sessionId }?.let { s ->
            inviteUrl = s.inviteUrl
        }
    }

    val sessionTitle = sessions.find { it.id == sessionId }?.title ?: "Session not found"
    val scrollState = rememberScrollState()

    // Compute balances
    val balances = remember(sessionId, participants, expenses) {
        calculateBalances(sessionId, participants, expenses)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(sessionTitle.capitalize(Locale.ENGLISH)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            inviteUrl?.let { SessionQRCode(inviteUrl = it) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Participants: ${participants[sessionId]?.size ?: 0}", fontSize = 16.sp)
            }

            // Expenses list
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Expenses", fontSize = 16.sp)
                val sessionExpenses = expenses[sessionId].orEmpty()
                if (sessionExpenses.isNotEmpty()) {
                    sessionExpenses.forEach { e ->
                        val payerName =
                            participants[sessionId]?.find { it.id == e.payerId }?.username
                                ?: e.payerId.take(6)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(e.memo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${e.amount} • paid by $payerName", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Text(
                        "No expense yet!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (expenses[sessionId].orEmpty().isNotEmpty()) {
                Column {
                    Text("Balances", fontSize = 16.sp)
                    BalancesList(
                        participants = participants[sessionId].orEmpty(),
                        balances = balances
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                if (expenses[sessionId].orEmpty().isNotEmpty()) {
                    Button(
                        onClick = { showZcash = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) {
                        Text("Settle with ZEC")
                    }
                }

                Button(onClick = { showAddExpense = true }) {
                    Text("Add Expense")
                }
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
            onClose = { showZcash = false }
        )
    }

    if (showAddExpense) {
        AddExpenseDialog(
            sessionId = sessionId,
            onClose = { showAddExpense = false }
        )
    }
}
