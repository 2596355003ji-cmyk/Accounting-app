package com.jicmyk.accounting.model

import java.time.LocalDateTime

enum class EntryDirection {
    EXPENSE,
    INCOME,
}

enum class TransactionSource {
    MANUAL,
    NOTIFICATION,
}

data class TransactionRecord(
    val id: Long,
    val amountMinor: Long,
    val direction: EntryDirection,
    val category: String,
    val account: String,
    val merchant: String,
    val occurredAt: LocalDateTime,
    val source: TransactionSource,
)
