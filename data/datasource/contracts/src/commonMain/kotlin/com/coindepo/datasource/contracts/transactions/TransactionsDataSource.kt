/*
 *     CoinDepo Unofficial Mobile App for Android and iOS
 *     Copyright (C) 2025  Martin Leon Bouchet
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.coindepo.datasource.contracts.transactions

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.transactions.TransactionStatus
import kotlinx.coroutines.flow.Flow

interface RemoteTransactionsDataSource {
    suspend fun getTransactions(
        userName: String,
        clientToken: String,
        page: Int,
        pageSize: Int
    ): Result<TransactionsPage>

    suspend fun cancelTransaction(
        userName: String,
        clientToken: String,
        transactionId: Int
    ): Result<Unit>
}

data class TransactionsPage(
    val transactions: List<Transaction>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalTransactions: Int
)

interface LocalTransactionsDataSource {
    @OptIn(ExperimentalPagingApi::class)
    fun getTransactionsPager(config: PagingConfig, remoteMediator: RemoteMediator<Int, Transaction>): Flow<PagingData<Transaction>>

    suspend fun saveTransactions(list: List<Transaction>, clean: Boolean = true)

    suspend fun getAllTransactions(): List<Transaction>

    suspend fun updateTransactionStatus(transactionId: Int, newStatus: TransactionStatus)
}