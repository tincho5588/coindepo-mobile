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

package com.coindepo.datasource.remote.depositplans

import com.coindepo.datasource.contracts.depositplans.RemoteDepositPlansDataSource
import com.coindepo.datasource.remote.depositplans.service.DepositPlansService
import com.coindepo.datasource.remote.depositplans.service.getDepositPlansService
import com.coindepo.datasource.remote.stats.data.DepositPlanDetailsDTO
import com.coindepo.datasource.remote.stats.service.StatsService
import com.coindepo.datasource.remote.stats.service.getStatsService
import com.coindepo.datasource.remote.utils.asBoolean
import com.coindepo.datasource.remote.utils.asResult
import com.coindepo.datasource.remote.utils.handle
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.coin.DepositPlan
import korlibs.bignumber.BigNum

class RemoteDepositPlansDataSourceImpl internal constructor(
    private val statsService: StatsService,
    private val depositPlansService: DepositPlansService
): RemoteDepositPlansDataSource {

    constructor() : this(
        getStatsService(),
        getDepositPlansService()
    )

    override suspend fun updateDepositPlans(
        currentAccountStats: AccountStats,
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ) = try {
        val accountStats = statsService.getAccountStats(userName, userId, currency, clientToken).handle()

        Result.success(
            currentAccountStats.copy(
                coins = currentAccountStats.coins.map {
                    it.copy(
                        depositPlans = accountStats.plans.filter { plan -> plan.coinId == it.coinId }
                            .map { plan ->  plan.asDepositPlan }
                    )
                }
            )
        )
    } catch (
        e: CoinDepoException
    ) {
        Result.failure(e)
    }

    override suspend fun setDepositPlanVisibility(
        visibile: Boolean,
        walletName: String,
        depositPlanId: Int,
        userName: String,
        clientToken: String
    ): Result<Unit> = depositPlansService.setDepositPlanVisibility(visibile, walletName, depositPlanId, userName, clientToken).asResult { }

    override suspend fun transferBetweenPlans(
        fromAccountId: Int,
        toAccountId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): Result<Unit> = depositPlansService.transferBetweenPlans(fromAccountId, toAccountId, coinId, amount, userName, userId, clientToken).asResult { }

    override suspend fun openDepositPlan(
        depositPlanId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): Result<Unit> = depositPlansService.openDepositPlan(depositPlanId, coinId, amount, userName, userId, clientToken).asResult { }
}

val DepositPlanDetailsDTO.asDepositPlan: DepositPlan
    get() = DepositPlan(
        id = "${userAccountId}_$depositPlanId",
        userAccountId = userAccountId,
        depositPlanId = depositPlanId,
        accountTypeId = accountTypeId,
        accountName = accountName,
        walletName = walletName,
        openDate = openDate,
        lastPayout = lastPayout,
        payoutDate = payoutDate,
        period = period,
        apr = apr,
        precision = precision,
        balance = balance,
        balanceCurrency = balanceCurrency,
        inTransit = inTransit,
        balanceAvailable = balanceAvailable,
        summaryBalance = summaryBalance,
        summaryBalanceCurrency = summaryBalanceCurrency,
        accruedInterest = accruedInterest,
        accruedInterestCurrency = accruedInterestCurrency,
        paidInterest = paidInterest,
        paidInterestCurrency = paidInterestCurrency,
        canDelete = canDelete.asBoolean,
        isAllowedToTransfer = isAllowedToTransfer,
        apy = apy,
        pendingBonus = pendingBonus,
        bonusList = bonusList,
        isVisible = visible
    )