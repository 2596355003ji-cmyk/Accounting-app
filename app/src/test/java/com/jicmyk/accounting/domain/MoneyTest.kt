package com.jicmyk.accounting.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyTest {
    @Test
    fun parsesCommonYuanAmountsIntoFen() {
        assertEquals(1234L, Money.parseYuanToFen("12.34"))
        assertEquals(100L, Money.parseYuanToFen("¥1"))
        assertEquals(123456L, Money.parseYuanToFen("1,234.56"))
    }

    @Test
    fun roundsToTheNearestFen() {
        assertEquals(101L, Money.parseYuanToFen("1.005"))
    }

    @Test
    fun rejectsInvalidOrNonPositiveAmounts() {
        assertNull(Money.parseYuanToFen(""))
        assertNull(Money.parseYuanToFen("abc"))
        assertNull(Money.parseYuanToFen("0"))
        assertNull(Money.parseYuanToFen("-5"))
    }
}
