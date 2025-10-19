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

package com.coindepo.datasource.remote.stats

import com.coindepo.datasource.contracts.stats.RemoteAccountStatsDataSource
import com.coindepo.datasource.remote.depositplans.asDepositPlan
import com.coindepo.datasource.remote.stats.data.AccountStatsDTO
import com.coindepo.datasource.remote.stats.data.BalanceStatsDTO
import com.coindepo.datasource.remote.stats.data.BorrowStatsDTO
import com.coindepo.datasource.remote.stats.data.SupportedCoinsDTO
import com.coindepo.datasource.remote.stats.data.TokensStatsDTO
import com.coindepo.datasource.remote.stats.data.asAccountType
import com.coindepo.datasource.remote.stats.data.logoUrl
import com.coindepo.datasource.remote.stats.service.CoinsService
import com.coindepo.datasource.remote.stats.service.StatsService
import com.coindepo.datasource.remote.stats.service.getCoinsService
import com.coindepo.datasource.remote.stats.service.getStatsService
import com.coindepo.datasource.remote.utils.asBoolean
import com.coindepo.datasource.remote.utils.handle
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.balance.LoanApr
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.CoinBalance
import com.coindepo.domain.entities.stats.coin.DepositPlan

class RemoteAccountStatsDataSourceImpl internal constructor(
    private val coinService: CoinsService,
    private val statsService: StatsService
) : RemoteAccountStatsDataSource {

    constructor(): this(
        getCoinsService(), getStatsService()
    )

    override suspend fun fetchAccountStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): Result<AccountStats> = try {
        val supportedCoins = coinService.getSupportedCoins(userName, clientToken).handle()
        val supportedTokens = coinService.getSupportedTokens(userName, clientToken).handle()
        val accountStats = statsService.getAccountStats(userName, userId, currency, clientToken).handle()
        val tokenStats = statsService.getTokensStats(userName, currency, clientToken).handle()
        val borrowStats = statsService.getBorrowStats(userName, currency, clientToken).handle()
        val balanceStats = statsService.getBalanceStats(userName, userId, currency, clientToken).handle()

        Result.success(
            getAccountStats(
                supportedCoinsDTO = supportedCoins,
                supportedTokensDTO = supportedTokens,
                accountStatsDTO = accountStats,
                tokensStatsDTO = tokenStats,
                borrowStatsDTO = borrowStats,
                balanceStatsDTO = balanceStats
            )
        )
    } catch (e: CoinDepoException) {
        Result.failure(e)
    }
}

private fun getAccountStats(
    supportedCoinsDTO: SupportedCoinsDTO,
    supportedTokensDTO: SupportedCoinsDTO,
    accountStatsDTO: AccountStatsDTO,
    tokensStatsDTO: TokensStatsDTO,
    borrowStatsDTO: BorrowStatsDTO,
    balanceStatsDTO: BalanceStatsDTO
): AccountStats = AccountStats(
    accountBalance = AccountBalance(
        totalAccruedInterestCurrency = accountStatsDTO.totalAccruedInterestCurrency!!,
        totalInterestPaidCurrency = accountStatsDTO.totalInterestPaidCurrency!!,
        totalBalance = accountStatsDTO.totalBalance!!,
        totalAvailableBalance = accountStatsDTO.totalAvailableBalance!!,
        totalTokensBalanceCurrency = accountStatsDTO.totalTokenBalanceCurrency!!,
        totalTokensAccruedInterestCurrency = accountStatsDTO.totalTokenAccruedInterestCurrency!!,
        totalTokensInterestPaidCurrency = accountStatsDTO.totalTokenInterestPaidCurrency!!,
        totalTokensBalanceAvailable = accountStatsDTO.totalTokenBalanceAvailable!!,
    ),
    borrowBalance = BorrowBalance(
        availablePortfolioBalance = borrowStatsDTO.availablePortfolioBalance!!,
        creditLineLimitCurrency = borrowStatsDTO.creditLineLimitCurrency!!,
        currentLoansCurrency = borrowStatsDTO.currentLoansCurrency!!,
        availableCreditLimitCurrency = borrowStatsDTO.availableCreditLimitCurrency!!,
        ltv = borrowStatsDTO.ltv!!,
        apr = borrowStatsDTO.apr!!,
        aprs = borrowStatsDTO.aprs.mapIndexed { index, value ->
            LoanApr(
                value.toString(), when (index) {
                    0 -> 0.0
                    1 -> 20.0
                    3 -> 35.0
                    else -> 50.0
                },
                when (index) {
                    0 -> 20.0
                    1 -> 35.00
                    3 -> 50.0
                    else -> 100.0
                }
            )
        }
    ),
    coins = supportedCoinsDTO.coinList.sortedBy { it.order }.map {
        Coin(
            coinId = it.coinId,
            coinName = it.coinName,
            logoUrl = it.logoUrl,
            production = it.production.asBoolean,
            precision = it.precision,
            limit24h = it.limit24h,
            explorerUrl = it.explorerUrl,
            withdrawLimit = it.withdrawLimit,
            binanceTicker = it.binanceTicker,
            description = it.description,
            descriptionTicker = it.descriptionTicker,
            isStableCoin = it.stableCoin.asBoolean,
            network = it.network,
            depName = it.depName,
            wthName = it.wthName,
            isActive = it.isActive.asBoolean,
            isToken = it.isToken.asBoolean,
            apy = it.apy,
            apr = it.apr,
            fee = it.fee,
            privatePrice = it.privatePrice,
            listingPrice = it.listingPrice,
            privateDiscount = it.privateDiscount,
            coinBalance = balanceStatsDTO.balances.find { balance -> balance.coinId == it.coinId }
                ?.let { balance ->
                    CoinBalance(
                        accruedInterest = balance.accruedInterest,
                        accruedInterestCurrency = balance.accruedInterestCurrency,
                        balance = balance.balance,
                        balanceAvailable = balance.balanceAvailable,
                        balanceAvailableCurrency = balance.balanceAvailableCurrency,
                        balanceCurrency = balance.balanceCurrency,
                        binance24h = balance.binance24h,
                        binance24hCurrency = balance.binance24hCurrency,
                        freezedTrxBalance = balance.freezedTrxBalance,
                        freezedTrxBalanceCurrency = balance.freezedTrxBalanceCurrency,
                        inTransit = balance.inTransit,
                        inTransitCurrency = balance.inTransitCurrency,
                        interest = balance.interest,
                        interestCurrency = balance.interestCurrency,
                        interestCurrencyCurrency = balance.interestCurrencyCurrency,
                        newTrxBalance = balance.newTrxBalance,
                        newTrxBalanceCurrency = balance.newTrxBalanceCurrency,
                        pendingTrxBalance = balance.pendingTrxBalance,
                        pendingTrxBalanceCurrency = balance.pendingTrxBalanceCurrency,
                        precision = balance.precision,
                        processingTrxBalance = balance.processingTrxBalance,
                        processingTrxBalanceCurrency = balance.processingTrxBalanceCurrency,
                        queuedTrxBalance = balance.queuedTrxBalance,
                        queuedTrxBalanceCurrency = balance.queuedTrxBalanceCurrency,
                        summaryBalance = balance.summaryBalance,
                        summaryBalanceCurrency = balance.summaryBalanceCurrency,
                        displayOrder = balance.order
                    )
                },
            depositPlans = accountStatsDTO.plans.filter { plan -> plan.coinId == it.coinId }
                .map { plan ->  plan.asDepositPlan },
            availableLoans = with(borrowStatsDTO.loansByCoin.first { loan -> loan.coinId == it.coinId }) {
                AvailableLoans(available.map { it.toString() }, rate.toString())
            },
            order = it.order
        )
    },
    tokens = supportedTokensDTO.coinList.sortedBy { it.order }.map {
        Coin(
            coinId = it.coinId,
            coinName = it.coinName,
            logoUrl = it.logoUrl,
            production = it.production.asBoolean,
            precision = it.precision,
            limit24h = it.limit24h,
            explorerUrl = it.explorerUrl,
            withdrawLimit = it.withdrawLimit,
            binanceTicker = it.binanceTicker,
            description = it.description,
            descriptionTicker = it.descriptionTicker,
            isStableCoin = it.stableCoin.asBoolean,
            network = it.network,
            depName = it.depName,
            wthName = it.wthName,
            isActive = it.isActive.asBoolean,
            isToken = it.isToken.asBoolean,
            apy = it.apy,
            apr = it.apr,
            fee = it.fee,
            privatePrice = it.privatePrice,
            listingPrice = it.listingPrice,
            privateDiscount = it.privateDiscount,
            coinBalance = balanceStatsDTO.balances.find { balance -> balance.coinId == it.coinId }
                ?.let { balance ->
                    CoinBalance(
                        accruedInterest = balance.accruedInterest,
                        accruedInterestCurrency = balance.accruedInterestCurrency,
                        balance = balance.balance,
                        balanceAvailable = balance.balanceAvailable,
                        balanceAvailableCurrency = balance.balanceAvailableCurrency,
                        balanceCurrency = balance.balanceCurrency,
                        binance24h = balance.binance24h,
                        binance24hCurrency = balance.binance24hCurrency,
                        freezedTrxBalance = balance.freezedTrxBalance,
                        freezedTrxBalanceCurrency = balance.freezedTrxBalanceCurrency,
                        inTransit = balance.inTransit,
                        inTransitCurrency = balance.inTransitCurrency,
                        interest = balance.interest,
                        interestCurrency = balance.interestCurrency,
                        interestCurrencyCurrency = balance.interestCurrencyCurrency,
                        newTrxBalance = balance.newTrxBalance,
                        newTrxBalanceCurrency = balance.newTrxBalanceCurrency,
                        pendingTrxBalance = balance.pendingTrxBalance,
                        pendingTrxBalanceCurrency = balance.pendingTrxBalanceCurrency,
                        precision = balance.precision,
                        processingTrxBalance = balance.processingTrxBalance,
                        processingTrxBalanceCurrency = balance.processingTrxBalanceCurrency,
                        queuedTrxBalance = balance.queuedTrxBalance,
                        queuedTrxBalanceCurrency = balance.queuedTrxBalanceCurrency,
                        summaryBalance = balance.summaryBalance,
                        summaryBalanceCurrency = balance.summaryBalanceCurrency,
                        displayOrder = balance.order
                    )
                },
            depositPlans = tokensStatsDTO.plans.filter { plan -> plan.coinId == it.coinId && plan.visible }
                .map { plan -> DepositPlan(
                    id = "${plan.userAccountId}_${plan.depositPlanId}",
                    userAccountId = plan.userAccountId,
                    depositPlanId = plan.depositPlanId,
                    accountType = plan.accountTypeId.asAccountType,
                    accountName = plan.accountName,
                    walletName = plan.walletName,
                    openDate = plan.openDate,
                    lastPayout = plan.lastPayout,
                    payoutDate = plan.payoutDate,
                    period = plan.period,
                    apr = plan.apr,
                    precision = plan.precision,
                    balance = plan.balance,
                    balanceCurrency = plan.balanceCurrency,
                    inTransit = plan.inTransit,
                    balanceAvailable = plan.balanceAvailable,
                    summaryBalance = plan.summaryBalance,
                    summaryBalanceCurrency = plan.summaryBalanceCurrency,
                    accruedInterest = plan.accruedInterest,
                    accruedInterestCurrency = plan.accruedInterestCurrency,
                    paidInterest = plan.paidInterest,
                    paidInterestCurrency = plan.paidInterestCurrency,
                    canDelete = plan.canDelete.asBoolean,
                    isAllowedToTransfer = plan.isAllowedToTransfer,
                    apy = plan.apy,
                    pendingBonus = plan.pendingBonus,
                    bonusList = plan.bonusList,
                    isVisible = plan.visible
                ) },
            availableLoans = with(borrowStatsDTO.loansByCoin.firstOrNull { loan -> loan.coinId == it.coinId }) {
                AvailableLoans(this?.available?.map { it.toString() } ?: emptyList(), this?.rate?.toString() ?: "")
            },
            order = it.order
        )
    }
)