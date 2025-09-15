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

package com.coindepo.datasource.contracts.depositplans

import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import korlibs.bignumber.BigNum

interface RemoteDepositPlansDataSource {
    suspend fun updateDepositPlans(
        currentAccountStats: AccountStats,
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): Result<AccountStats>

    suspend fun setDepositPlanVisibility(
        visibile: Boolean,
        walletName: String,
        depositPlanId: Int,
        userName: String,
        clientToken: String
    ): Result<Unit>

    suspend fun transferBetweenPlans(
        fromAccountId: Int,
        toAccountId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): Result<Unit>

    suspend fun openDepositPlan(
        depositPlanId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): Result<Unit>
}