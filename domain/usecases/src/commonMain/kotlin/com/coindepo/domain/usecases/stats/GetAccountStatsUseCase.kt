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

package com.coindepo.domain.usecases.stats

import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.usecases.LoggedInUseCase
import com.coindepo.domain.usecases.login.GetUserSessionUseCase
import com.coindepo.domain.usecases.login.LogOutUseCase
import com.coindepo.repository.contracts.stats.AccountStatsRepository
import kotlinx.coroutines.flow.StateFlow

class GetAccountStatsUseCase(
    private val accountStatsRepository: AccountStatsRepository,
    getUserSessionUseCase: GetUserSessionUseCase,
    logOutUseCase: LogOutUseCase
): LoggedInUseCase(getUserSessionUseCase, logOutUseCase) {
    val accountStats: StateFlow<AccountStats?> by lazy { accountStatsRepository.accountStats }

    val accountBalance: StateFlow<AccountBalance?> by lazy { accountStatsRepository.accountBalance }
    val borrowBalance: StateFlow<BorrowBalance?> by lazy { accountStatsRepository.borrowBalance }
    val coins: StateFlow<List<Coin>> by lazy { accountStatsRepository.coins }
    val tokens: StateFlow<List<Coin>> by lazy { accountStatsRepository.tokens }

    suspend fun fetchAccountStats(
        currency: Currency,
    ): OperationResult = executeWithUserSession { email, userName, userId, clientToken ->
        accountStatsRepository.fetchAccountStats(userName, userId, currency, clientToken)
    }
}