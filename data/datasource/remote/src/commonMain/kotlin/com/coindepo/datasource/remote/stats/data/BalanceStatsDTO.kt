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

@file:UseSerializers(BigNumSerializer::class)

package com.coindepo.datasource.remote.stats.data

import com.coindepo.datasource.remote.BigNumSerializer
import com.coindepo.domain.entities.stats.tier.UserTier
import korlibs.bignumber.BigNum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class BalanceStatsDTO(
    @SerialName("available_credit_limit") val availableCreditLimit: BigNum? = null,
    @SerialName("balances") val balances: List<BalanceDTO> = emptyList(),
    //@SerialName("bonuses_summary") val bonusesSummary: BonusesSummary? = BonusesSummary(),
    @SerialName("credit_line_limit") val creditLineLimit: BigNum? = null,
    @SerialName("earned_referrals") val earnedReferrals: BigNum? = null,
    @SerialName("total_available_balance") val totalAvailableBalance: BigNum? = null,
    @SerialName("total_balance") val totalBalance: BigNum? = null,
    @SerialName("user_tier") val userTier: UserTierDTO? = null,
    override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

@Serializable
data class BalanceDTO(
    @SerialName("accrued_interest") val accruedInterest: BigNum,
    @SerialName("accrued_interest_currency") val accruedInterestCurrency: BigNum,
    @SerialName("balance") val balance: BigNum,
    @SerialName("balance_available") val balanceAvailable: BigNum,
    @SerialName("balance_available_currency") val balanceAvailableCurrency: BigNum,
    @SerialName("balance_currency") val balanceCurrency: BigNum,
    @SerialName("binance_24h") val binance24h: BigNum,
    @SerialName("binance_24h_currency") val binance24hCurrency: BigNum,
    @SerialName("coin_id") val coinId: Int,
    @SerialName("coin_name") val coinName: String,
    @SerialName("freezed_trx_balance") val freezedTrxBalance: BigNum,
    @SerialName("freezed_trx_balance_currency") val freezedTrxBalanceCurrency: BigNum,
    @SerialName("in_transit") val inTransit: BigNum,
    @SerialName("in_transit_currency") val inTransitCurrency: BigNum,
    @SerialName("interest") val interest: BigNum,
    @SerialName("interest_currency") val interestCurrency: BigNum,
    @SerialName("interest_currency_currency") val interestCurrencyCurrency: BigNum,
    @SerialName("is_token") val isToken: Int,
    @SerialName("new_trx_balance") val newTrxBalance: BigNum,
    @SerialName("new_trx_balance_currency") val newTrxBalanceCurrency: BigNum,
    @SerialName("order") val order: Int,
    @SerialName("pending_trx_balance") val pendingTrxBalance: BigNum,
    @SerialName("pending_trx_balance_currency") val pendingTrxBalanceCurrency: BigNum,
    @SerialName("precision") val precision: Int,
    @SerialName("processing_trx_balance") val processingTrxBalance: BigNum,
    @SerialName("processing_trx_balance_currency") val processingTrxBalanceCurrency: BigNum,
    @SerialName("queued_trx_balance") val queuedTrxBalance: BigNum,
    @SerialName("queued_trx_balance_currency") val queuedTrxBalanceCurrency: BigNum,
    @SerialName("summary_balance") val summaryBalance: BigNum,
    @SerialName("summary_balance_currency") val summaryBalanceCurrency: BigNum,
    @SerialName("ticker") val ticker: String
)

@Serializable
data class UserTierDTO(
    @SerialName("coindepo_percentage") val coindepoPercentage: BigNum? = null,
    @SerialName("tier_id") val tierId: Int? = null
)

val UserTierDTO.asUserTier: UserTier
    get() = UserTier(
        coinDepoTokenPercentage = coindepoPercentage ?: BigNum.ZERO,
        tierId = tierId
    )