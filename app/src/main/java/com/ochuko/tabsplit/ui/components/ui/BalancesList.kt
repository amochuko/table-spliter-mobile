package com.ochuko.tabsplit.ui.components.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ochuko.tabsplit.models.Participant
import com.ochuko.tabsplit.utils.shortString

@Composable
fun BalancesList(
    participants: List<Participant>,
    balances: Map<String, Double>
) {
    if (participants.isEmpty()) {
        Text(
            text = "No participants yet.",
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
    ) {
        participants.forEach { p ->
            val balance = balances[p.id] ?: 0.0
            val isPositive = balance > 0
            val isZero = balance == 0.0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        !p.username.isNullOrEmpty() -> p.username
                        !p.userId.isNullOrEmpty() -> shortString(p.userId)
                        else -> "Unnamed"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                )

                Text(
                    text = if (balance >= 0) "+%.2f".format(balance) else "%.2f".format(balance),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            isPositive -> Color(0xFF2E7D32) // green
                            !isPositive && !isZero -> Color(0xFFC62828) // red
                            else -> Color.Unspecified
                        }
                    )
                )
            }

            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)
        }
    }
}
