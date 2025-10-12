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

package com.coindepo.datasource.remote.transactions

import com.coindepo.datasource.contracts.transactions.RemoteTransactionsDataSource
import com.coindepo.datasource.contracts.transactions.TransactionsPage
import com.coindepo.datasource.remote.transactions.data.asTransaction
import com.coindepo.datasource.remote.transactions.service.TransactionsService
import com.coindepo.datasource.remote.transactions.service.getTransactionsService
import com.coindepo.datasource.remote.utils.asResult

class RemoteTransactionsDataSourceImpl internal constructor(
    private val transactionsService: TransactionsService
) : RemoteTransactionsDataSource {

    constructor() : this(getTransactionsService())

    override suspend fun getTransactions(
        userName: String,
        clientToken: String,
        page: Int,
        pageSize: Int
    ): Result<TransactionsPage> = transactionsService.getTransactions(
        userName, clientToken, page, pageSize
    ).asResult {
        TransactionsPage(
            transactions.map { it.asTransaction },
            page,
            pageSize,
            totalTrx
        )
    }

    override suspend fun cancelTransaction(
        userName: String,
        clientToken: String,
        transactionId: Int
    ): Result<Unit> = transactionsService.cancelTransaction(userName, clientToken, transactionId).asResult {}
}