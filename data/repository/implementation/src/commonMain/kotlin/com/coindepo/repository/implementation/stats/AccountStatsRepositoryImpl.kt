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

package com.coindepo.repository.implementation.stats

import com.coindepo.datasource.contracts.stats.LocalAccountStatsDataSource
import com.coindepo.datasource.contracts.stats.RemoteAccountStatsDataSource
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.repository.contracts.stats.AccountStatsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class AccountStatsRepositoryImpl(
    private val localAccountStatsDataSource: LocalAccountStatsDataSource,
    private val remoteAccountStatsDataSource: RemoteAccountStatsDataSource
) : AccountStatsRepository {

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val accountStats: StateFlow<AccountStats?>
        get() = localAccountStatsDataSource.accountStats.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    override val accountBalance: StateFlow<AccountBalance?>
        get() = localAccountStatsDataSource.accountBalance.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    override val borrowBalance: StateFlow<BorrowBalance?>
        get() = localAccountStatsDataSource.borrowBalance.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    override val coins: StateFlow<List<Coin>>
        get() = localAccountStatsDataSource.coins.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
    override val tokens: StateFlow<List<Coin>>
        get() = localAccountStatsDataSource.tokens.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override suspend fun fetchAccountStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): OperationResult = withContext(Dispatchers.IO) {
        remoteAccountStatsDataSource.fetchAccountStats(userName, userId, currency, clientToken)
            .fold(
                onSuccess = {
                    localAccountStatsDataSource.saveAccountStats(it, userId)
                    OperationResult.Success(it)
                },
                onFailure = {
                    OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
                }
            )
    }
}