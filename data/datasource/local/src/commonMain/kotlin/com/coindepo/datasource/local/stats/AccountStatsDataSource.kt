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

package com.coindepo.datasource.local.stats

import com.coindepo.datasource.contracts.stats.LocalAccountStatsDataSource
import com.coindepo.datasource.local.database.CoinDepoDataBase
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.Coin
import kotlinx.coroutines.flow.Flow

class LocalAccountStatsDataSourceImpl(
    private val coinDepoDataBase: CoinDepoDataBase
) : LocalAccountStatsDataSource {
    override val accountStats: Flow<AccountStats?>
        get() = coinDepoDataBase.accountStatsFlow

    override val accountBalance: Flow<AccountBalance?>
        get() = coinDepoDataBase.accountBalance
    override val borrowBalance: Flow<BorrowBalance?>
        get() = coinDepoDataBase.borrowBalance
    override val coins: Flow<List<Coin>>
        get() = coinDepoDataBase.coins
    override val tokens: Flow<List<Coin>>
        get() = coinDepoDataBase.tokens


    override suspend fun saveAccountStats(accountStats: AccountStats, userId: Int) {
        coinDepoDataBase.saveAccountStats(accountStats, userId)
    }

    override suspend fun getAccountStats(userId: Int): AccountStats? =
        coinDepoDataBase.getAccountStats(userId)
}