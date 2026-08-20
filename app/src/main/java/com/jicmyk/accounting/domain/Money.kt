package com.jicmyk.accounting.domain

import java.math.RoundingMode
import java.util.Locale

object Money {
    fun parseYuanToFen(value: String): Long? {
        val normalized = value
            .trim()
            .replace("，", "")
            .replace(",", "")
            .replace("￥", "")
            .replace("¥", "")

        val amount = normalized.toBigDecimalOrNull() ?: return null
        if (amount.signum() <= 0) return null

        return runCatching {
            amount
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact()
        }.getOrNull()
    }

    fun formatYuan(amountMinor: Long): String =
        String.format(Locale.CHINA, "¥%,.2f", amountMinor / 100.0)
}
