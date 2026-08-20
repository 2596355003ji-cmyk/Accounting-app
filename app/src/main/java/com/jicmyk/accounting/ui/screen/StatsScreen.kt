package com.jicmyk.accounting.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jicmyk.accounting.domain.Money
import com.jicmyk.accounting.model.EntryDirection
import com.jicmyk.accounting.model.TransactionRecord

@Composable
fun StatsScreen(
    contentPadding: PaddingValues,
    transactions: List<TransactionRecord>,
) {
    val expenses = transactions.filter { it.direction == EntryDirection.EXPENSE }
    val categoryTotals = expenses
        .groupBy { it.category }
        .mapValues { (_, records) -> records.sumOf { it.amountMinor } }
        .toList()
        .sortedByDescending { it.second }
    val maximum = categoryTotals.maxOfOrNull { it.second } ?: 1L

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Text("支出统计", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            text = "累计支出 ${Money.formatYuan(expenses.sumOf { it.amountMinor })}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (categoryTotals.isEmpty()) {
            Text(
                text = "记录支出后，这里会显示分类占比。",
                modifier = Modifier.padding(top = 28.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(
                modifier = Modifier.padding(top = 28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                categoryTotals.forEach { (category, total) ->
                    val fraction = total.toFloat() / maximum.toFloat()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(category, fontWeight = FontWeight.Medium)
                            Text(Money.formatYuan(total))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                        }
                    }
                }
            }
        }
    }
}
