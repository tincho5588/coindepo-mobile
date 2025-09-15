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

package com.coindepo.datasource.contracts.stats

import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.Coin
import kotlinx.coroutines.flow.Flow

interface RemoteAccountStatsDataSource {
    suspend fun fetchAccountStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): Result<AccountStats>
}

interface LocalAccountStatsDataSource {
    val accountStats: Flow<AccountStats?>

    val accountBalance: Flow<AccountBalance?>
    val borrowBalance: Flow<BorrowBalance?>
    val coins: Flow<List<Coin>>
    val tokens: Flow<List<Coin>>

    suspend fun saveAccountStats(accountStats: AccountStats, userId: Int)

    suspend fun getAccountStats(userId: Int): AccountStats?
}