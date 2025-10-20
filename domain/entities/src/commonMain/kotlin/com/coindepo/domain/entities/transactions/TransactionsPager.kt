package com.coindepo.domain.entities.transactions

import androidx.paging.PagingData
import com.coindepo.domain.entities.stats.coin.AccountType
import com.coindepo.domain.entities.stats.coin.Coin
import kotlinx.coroutines.flow.Flow

data class TransactionsPager(
    val transactions: Flow<PagingData<Transaction>>,
    val filtersSetter: TransactionsFilterSetter
)

fun interface TransactionsFilterSetter {
    fun setFilters(newFilters: TransactionsFilters)
}

data class TransactionsFilters(
    val dateRange: TransactionsDateRange? = null,
    val selectedAsset: Coin? = null,
    val accountType: AccountType? = null,
    val transactionType: TransactionType? = null,
    val transactionStatus: TransactionStatus? = null,
)

data class TransactionsDateRange(
    val startDateMillis: Long,
    val endDateMillis: Long
)

val TransactionsFilters.hasFilters: Boolean
    get() = dateRange != null || selectedAsset != null || accountType != null || transactionType != null || transactionStatus != null