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

package com.coindepo.datasource.local.Transactions

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.map
import com.coindepo.datasource.contracts.transactions.LocalTransactionsDataSource
import com.coindepo.datasource.local.database.CoinDepoDataBase
import com.coindepo.datasource.local.database.dao.asTransaction
import com.coindepo.datasource.local.utils.RemoteMediatorConverter
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.transactions.TransactionStatus
import com.coindepo.domain.entities.transactions.TransactionsPager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTransactionsDataSourceImpl(
    private val coinDepoDatabase: CoinDepoDataBase
) : LocalTransactionsDataSource {
    @OptIn(ExperimentalPagingApi::class)
    override fun getTransactionsPager(
        config: PagingConfig,
        remoteMediator: RemoteMediator<Int, Transaction>
    ): Flow<PagingData<Transaction>> = Pager(
        config,
        remoteMediator = RemoteMediatorConverter(remoteMediator) {
            it.asTransaction
        },
        pagingSourceFactory = { coinDepoDatabase.getTransactionsPagingSource() }
    ).flow.map { it.map { it.asTransaction } }


    override suspend fun saveTransactions(
        list: List<Transaction>,
        clean: Boolean
    ) {
        coinDepoDatabase.saveTransactions(list, clean)
    }

    override suspend fun getAllTransactions(): List<Transaction> =
        coinDepoDatabase.getAllTransactions()

    override suspend fun updateTransactionStatus(entryId: String, newStatus: TransactionStatus) {
        coinDepoDatabase.updateTransactionStatus(entryId, newStatus)
    }
}