package com.jicmyk.accounting.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jicmyk.accounting.model.TransactionRecord

@Composable
fun TransactionsScreen(
    contentPadding: PaddingValues,
    transactions: List<TransactionRecord>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        Text(
            text = "收支明细",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        if (transactions.isEmpty()) {
            Text(
                text = "暂无明细，点击右下角记录第一笔。",
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
                items(transactions, key = { it.id }) { transaction ->
                    TransactionListItem(transaction)
                }
            }
        }
    }
}
