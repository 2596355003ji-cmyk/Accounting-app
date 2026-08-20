package com.jicmyk.accounting.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jicmyk.accounting.ui.screen.AddTransactionSheet
import com.jicmyk.accounting.ui.screen.HomeScreen
import com.jicmyk.accounting.ui.screen.SettingsScreen
import com.jicmyk.accounting.ui.screen.StatsScreen
import com.jicmyk.accounting.ui.screen.TransactionsScreen

private data class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val destinations = listOf(
    Destination("home", "首页", Icons.Outlined.Home),
    Destination("transactions", "明细", Icons.AutoMirrored.Outlined.ReceiptLong),
    Destination("stats", "统计", Icons.Outlined.BarChart),
    Destination("settings", "设置", Icons.Outlined.Settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountingApp(
    ledgerViewModel: LedgerViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val uiState by ledgerViewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var showAddTransaction by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(),
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute != "settings") {
                FloatingActionButton(onClick = { showAddTransaction = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "记一笔")
                }
            }
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
        ) {
            composable("home") {
                HomeScreen(
                    contentPadding = contentPadding,
                    transactions = uiState.transactions,
                    onAddTransaction = { showAddTransaction = true },
                    onOpenAutomation = { navController.navigate("settings") },
                )
            }
            composable("transactions") {
                TransactionsScreen(
                    contentPadding = contentPadding,
                    transactions = uiState.transactions,
                )
            }
            composable("stats") {
                StatsScreen(
                    contentPadding = contentPadding,
                    transactions = uiState.transactions,
                )
            }
            composable("settings") {
                SettingsScreen(contentPadding = contentPadding)
            }
        }
    }

    if (showAddTransaction) {
        ModalBottomSheet(onDismissRequest = { showAddTransaction = false }) {
            AddTransactionSheet(
                onSave = { amount, direction, category, account, merchant ->
                    ledgerViewModel.addTransaction(
                        amountText = amount,
                        direction = direction,
                        category = category,
                        account = account,
                        merchant = merchant,
                    ).also { saved ->
                        if (saved) showAddTransaction = false
                    }
                },
            )
        }
    }
}
