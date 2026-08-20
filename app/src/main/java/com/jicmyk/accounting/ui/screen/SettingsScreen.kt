package com.jicmyk.accounting.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        Text("设置", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "自动记账将在账本底座完成后接入。",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(modifier = Modifier.padding(top = 20.dp)) {
            ListItem(
                headlineContent = { Text("通知自动记账") },
                supportingContent = { Text("未开启 · 计划支持微信和支付宝") },
                leadingContent = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null) },
            )
        }
        Card(modifier = Modifier.padding(top = 12.dp)) {
            ListItem(
                headlineContent = { Text("数据与隐私") },
                supportingContent = { Text("账单仅保存在本机，不上传支付信息") },
                leadingContent = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            )
        }
        Card(modifier = Modifier.padding(top = 12.dp)) {
            ListItem(
                headlineContent = { Text("关于自动账本") },
                supportingContent = { Text("原型版本 0.1.0-dev") },
                leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) },
            )
        }
    }
}
