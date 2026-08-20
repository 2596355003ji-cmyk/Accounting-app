package com.jicmyk.accounting.ui

import androidx.lifecycle.ViewModel
import com.jicmyk.accounting.domain.Money
import com.jicmyk.accounting.model.EntryDirection
import com.jicmyk.accounting.model.TransactionRecord
import com.jicmyk.accounting.model.TransactionSource
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LedgerUiState(
    val transactions: List<TransactionRecord> = emptyList(),
)

class LedgerViewModel : ViewModel() {
    private val idSequence = AtomicLong(1)
    private val _uiState = MutableStateFlow(LedgerUiState())
    val uiState = _uiState.asStateFlow()

    fun addTransaction(
        amountText: String,
        direction: EntryDirection,
        category: String,
        account: String,
        merchant: String,
    ): Boolean {
        val amountMinor = Money.parseYuanToFen(amountText) ?: return false
        val transaction = TransactionRecord(
            id = idSequence.getAndIncrement(),
            amountMinor = amountMinor,
            direction = direction,
            category = category,
            account = account,
            merchant = merchant.trim().ifEmpty { category },
            occurredAt = LocalDateTime.now(),
            source = TransactionSource.MANUAL,
        )
        _uiState.update { state ->
            state.copy(transactions = listOf(transaction) + state.transactions)
        }
        return true
    }
}
