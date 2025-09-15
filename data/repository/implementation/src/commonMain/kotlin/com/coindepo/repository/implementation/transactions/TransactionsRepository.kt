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
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.coindepo.datasource.contracts.transactions.LocalTransactionsDataSource
import com.coindepo.datasource.contracts.transactions.RemoteTransactionsDataSource
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.repository.contracts.transactions.TransactionsRepository
import kotlinx.coroutines.flow.Flow

class TransactionsRepositoryImpl(
    private val localTransactionsDataSource: LocalTransactionsDataSource,
    private val remoteTransactionsDataSource: RemoteTransactionsDataSource
): TransactionsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getTransactionsListPaged(
        userName: String,
        clientToken: String,
        pageSize: Int
    ): Flow<PagingData<Transaction>> = localTransactionsDataSource.getTransactionsPager(
        config = PagingConfig(pageSize),
        remoteMediator = TransactionsRemoteMediator(userName, clientToken, localTransactionsDataSource, remoteTransactionsDataSource)
    )
}