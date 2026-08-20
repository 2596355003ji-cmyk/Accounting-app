package com.jicmyk.accounting.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jicmyk.accounting.domain.Money
import com.jicmyk.accounting.model.EntryDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onSave: (String, EntryDirection, String, String, String) -> Boolean,
) {
    var amount by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf(EntryDirection.EXPENSE) }
    var category by remember { mutableStateOf("餐饮") }
    var account by remember { mutableStateOf("微信") }
    var merchant by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val categories = if (direction == EntryDirection.EXPENSE) {
        listOf("餐饮", "购物", "交通", "居住", "娱乐", "其他")
    } else {
        listOf("工资", "收款", "理财", "红包", "其他")
    }
    val accounts = listOf("微信", "支付宝", "银行卡", "现金")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
    ) {
        Text("记一笔", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            EntryDirection.entries.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = direction == item,
                    onClick = {
                        direction = item
                        category = if (item == EntryDirection.EXPENSE) "餐饮" else "工资"
                    },
                    shape = SegmentedButtonDefaults.itemShape(index, EntryDirection.entries.size),
                ) {
                    Text(if (item == EntryDirection.EXPENSE) "支出" else "收入")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
                showError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("金额") },
            prefix = { Text("¥") },
            singleLine = true,
            isError = showError,
            supportingText = {
                if (showError) Text("请输入大于 0 的有效金额")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
        )
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("商户或备注（可选）") },
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))
        Text("分类", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { item ->
                FilterChip(
                    selected = category == item,
                    onClick = { category = item },
                    label = { Text(item) },
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("账户", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(accounts) { item ->
                FilterChip(
                    selected = account == item,
                    onClick = { account = item },
                    label = { Text(item) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (Money.parseYuanToFen(amount) == null) {
                    showError = true
                } else if (!onSave(amount, direction, category, account, merchant)) {
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("保存")
        }
    }
}
