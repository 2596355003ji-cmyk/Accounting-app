package com.jicmyk.accounting.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jicmyk.accounting.domain.Money
import com.jicmyk.accounting.model.EntryDirection
import com.jicmyk.accounting.model.TransactionRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    contentPadding: PaddingValues,
    transactions: List<TransactionRecord>,
    onAddTransaction: () -> Unit,
    onOpenAutomation: () -> Unit,
) {
    val today = LocalDate.now()
    val monthly = transactions.filter {
        it.occurredAt.year == today.year && it.occurredAt.monthValue == today.monthValue
    }
    val income = monthly.filter { it.direction == EntryDirection.INCOME }.sumOf { it.amountMinor }
    val expense = monthly.filter { it.direction == EntryDirection.EXPENSE }.sumOf { it.amountMinor }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "自动账本",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "${today.monthValue} 月收支",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("本月结余", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = Money.formatYuan(income - expense),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SummaryValue("收入", Money.formatYuan(income))
                    SummaryValue("支出", Money.formatYuan(expense))
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Card(onClick = onOpenAutomation) {
            ListItem(
                headlineContent = { Text("开启自动记账") },
                supportingContent = { Text("下一阶段将接入微信、支付宝通知识别") },
                leadingContent = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null) },
                trailingContent = { Icon(Icons.Outlined.ChevronRight, contentDescription = null) },
            )
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("最近明细", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            OutlinedButton(onClick = onAddTransaction) { Text("记一笔") }
        }
        Spacer(Modifier.height(8.dp))

        if (transactions.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("还没有账单", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "先手动记录第一笔，自动识别功能随后接入。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onAddTransaction) { Text("记录第一笔") }
                }
            }
        } else {
            transactions.take(5).forEach { transaction ->
                TransactionListItem(transaction)
            }
        }
    }
}

@Composable
private fun SummaryValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TransactionListItem(transaction: TransactionRecord) {
    val sign = if (transaction.direction == EntryDirection.EXPENSE) "-" else "+"
    ListItem(
        headlineContent = { Text(transaction.merchant) },
        supportingContent = {
            Text(
                "${transaction.category} · ${transaction.account} · " +
                    transaction.occurredAt.format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
            )
        },
        trailingContent = {
            Text(
                text = sign + Money.formatYuan(transaction.amountMinor),
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.direction == EntryDirection.EXPENSE) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
        },
    )
}
