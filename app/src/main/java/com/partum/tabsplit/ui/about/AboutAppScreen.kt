package com.partum.tabsplit.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.partum.tabsplit.R


@Composable
fun AboutAppScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "TabSplit",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodyMedium
        )

        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.tabsplit_helps_groups_manage_shared_expenses_easily_and_settle_with_zcash) + stringResource(
                R.string.built_with_by_the_partum_team
            ),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.open_source_libraries),
            style = MaterialTheme.typography.titleMedium
        )
        Text(stringResource(R.string.jetpack_compose_retrofit_express_js_postgresql_zcash_sdk))

    }
}
