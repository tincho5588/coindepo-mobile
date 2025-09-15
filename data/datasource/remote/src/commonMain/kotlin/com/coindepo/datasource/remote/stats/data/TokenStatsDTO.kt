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
import korlibs.bignumber.BigNum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class TokensStatsDTO(
    @SerialName("total_accrued_interest_currency") val totalAccruedInterestCurrency: BigNum? = null,
    @SerialName("total_interest_paid_currency") val totalInterestPaidCurrency: BigNum? = null,
    @SerialName("balances") val balances: List<TokenBalancesDTO> = emptyList(),
    @SerialName("total_balance") val totalBalance: BigNum? = null,
    @SerialName("total_t_balance_currency") val totalTBalanceCurrency: BigNum? = null,
    @SerialName("total_t_accrued_interest_currency") val totalTAccruedInterestCurrency: BigNum? = null,
    @SerialName("total_t_interest_paid_currency") val totalTInterestPaidCurrency: BigNum? = null,
    @SerialName("total_t_balance_available") val totalTBalanceAvailable: BigNum? = null,
    @SerialName("plans") val plans: List<TokenPlansDTO> = emptyList(),
    @SerialName("currency") val currency: String? = null,
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("status") override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

@Serializable
data class TokenBalancesDTO(
    @SerialName("coin_id") val coinId: Int,
    @SerialName("balance") val balance: BigNum,
    @SerialName("balance_available") val balanceAvailable: BigNum,
    @SerialName("balance_available_currency") val balanceAvailableCurrency: BigNum,
    @SerialName("balance_currency") val balanceCurrency: BigNum,
    @SerialName("paid_interest_currency") val paidInterestCurrency: BigNum,
    @SerialName("accrued_interest_currency") val accruedInterestCurrency: BigNum
)

@Serializable
data class TokenPlansDTO(
    @SerialName("coin_id") val coinId: Int,
    @SerialName("coin_name") val coinName: String,
    @SerialName("desc_ticker") val descTicker: String,
    @SerialName("desc") val desc: String,
    @SerialName("binance_ticker") val binanceTicker: String,
    @SerialName("user_account_id") val userAccountId: Int,
    @SerialName("deposit_plan_id") val depositPlanId: Int,
    @SerialName("account_type_id") val accountTypeId: Int,
    @SerialName("visible") val visible: Boolean,
    @SerialName("account_name") val accountName: String,
    @SerialName("wallet_name") val walletName: String,
    @SerialName("open_date") val openDate: String,
    @SerialName("last_payout") val lastPayout: String,
    @SerialName("payout_date") val payoutDate: String,
    @SerialName("period") val period: String,
    @SerialName("apr") val apr: BigNum,
    @SerialName("precision") val precision: Int,
    @SerialName("balance") val balance: BigNum,
    @SerialName("balance_currency") val balanceCurrency: BigNum,
    @SerialName("in_transit") val inTransit: BigNum,
    @SerialName("balance_available") val balanceAvailable: BigNum,
    @SerialName("summary_balance") val summaryBalance: BigNum,
    @SerialName("summary_balance_currency") val summaryBalanceCurrency: BigNum,
    @SerialName("accrued_interest") val accruedInterest: BigNum,
    @SerialName("accrued_interest_currency") val accruedInterestCurrency: BigNum,
    @SerialName("paid_interest") val paidInterest: BigNum,
    @SerialName("paid_interest_currency") val paidInterestCurrency: BigNum,
    @SerialName("can_delete") val canDelete: Int,
    @SerialName("is_allowed_to_transfer") val isAllowedToTransfer: Boolean,
    @SerialName("logo_name") val logoName: String,
    @SerialName("apy") val apy: BigNum,
    @SerialName("pending_bonus") val pendingBonus: Boolean,
    @SerialName("bonus_list") val bonusList: String,
    @SerialName("listing_price") val listingPrice: BigNum,
    @SerialName("private_price") val privatePrice: BigNum,
    @SerialName("private_discount") val privateDiscount: BigNum,
    @SerialName("fee") val fee: BigNum
)