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

package com.coindepo.repository.implementation.transactions

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.coindepo.datasource.contracts.transactions.LocalTransactionsDataSource
import com.coindepo.datasource.contracts.transactions.RemoteTransactionsDataSource
import com.coindepo.datasource.contracts.transactions.TransactionsPage
import com.coindepo.domain.entities.transactions.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class TransactionsRemoteMediator(
    private val userName: String,
    private val accessToken: String,
    private val localTransactionsDataSource: LocalTransactionsDataSource,
    private val remoteTransactionsDataSource: RemoteTransactionsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(1)
): RemoteMediator<Int, Transaction>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Transaction>
    ): MediatorResult = withContext(ioDispatcher) {
        val existingItems = localTransactionsDataSource.getAllTransactions()
        when(loadType) {
            LoadType.REFRESH -> {
                remoteTransactionsDataSource.getTransactions(userName, accessToken, 1, state.config.pageSize)
            }
            LoadType.PREPEND -> {
                Result.success(
                    TransactionsPage(
                        emptyList(),
                        1,
                        state.config.pageSize,
                        0
                    )
                )
            }
            LoadType.APPEND -> {
                val pageNumber = (existingItems.size / state.config.pageSize) + 1
                remoteTransactionsDataSource.getTransactions(userName, accessToken, pageNumber, state.config.pageSize)
            }
        }.fold(
            onSuccess = {
                localTransactionsDataSource.saveTransactions(it.transactions, loadType == LoadType.REFRESH)
                MediatorResult.Success(
                    when(loadType) {
                        LoadType.REFRESH -> false
                        LoadType.PREPEND, LoadType.APPEND -> it.transactions.isEmpty() || (existingItems.size + it.transactions.size) == it.totalTransactions
                    }
                )
            },
            onFailure = {
                MediatorResult.Error(it)
            }
        )
    }
}