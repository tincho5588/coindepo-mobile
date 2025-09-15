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
data class AccountStatsDTO(
    @SerialName("total_accrued_interest_currency") var totalAccruedInterestCurrency: BigNum? = null,
    @SerialName("total_interest_paid_currency") var totalInterestPaidCurrency: BigNum? = null,
    @SerialName("balances") var balances: List<CoinBalanceDTO> = emptyList(),
    @SerialName("total_balance") var totalBalance: BigNum? = null,
    @SerialName("total_t_balance_currency") var totalTokenBalanceCurrency: BigNum? = null,
    @SerialName("total_t_accrued_interest_currency") var totalTokenAccruedInterestCurrency: BigNum? = null,
    @SerialName("total_t_interest_paid_currency") var totalTokenInterestPaidCurrency: BigNum? = null,
    @SerialName("total_t_balance_available") var totalTokenBalanceAvailable: BigNum? = null,
    @SerialName("total_available_balance") var totalAvailableBalance: BigNum? = null,
    @SerialName("plans") var plans: List<DepositPlanDetailsDTO> = emptyList(),
    @SerialName("currency") var currency: String? = null,
    @SerialName("status") override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

@Serializable
data class CoinBalanceDTO(
    @SerialName("coin_id") var coinId: Int,
    @SerialName("balance") var balance: BigNum,
    @SerialName("balance_available") var balanceAvailable: BigNum,
    @SerialName("balance_available_currency") var balanceAvailableCurrency: BigNum,
    @SerialName("balance_currency") var balanceCurrency: BigNum,
    @SerialName("paid_interest_currency") var paidInterestCurrency: BigNum,
    @SerialName("accrued_interest_currency") var accruedInterestCurrency: BigNum
)

@Serializable
data class DepositPlanDetailsDTO(
    @SerialName("coin_id") var coinId: Int,
    @SerialName("coin_name") var coinName: String,
    @SerialName("desc_ticker") var descTicker: String,
    @SerialName("desc") var desc: String,
    @SerialName("binance_ticker") var binanceTicker: String,
    @SerialName("user_account_id") var userAccountId: Int,
    @SerialName("deposit_plan_id") var depositPlanId: Int,
    @SerialName("account_type_id") var accountTypeId: Int,
    @SerialName("visible") var visible: Boolean,
    @SerialName("account_name") var accountName: String,
    @SerialName("wallet_name") var walletName: String,
    @SerialName("open_date") var openDate: String,
    @SerialName("last_payout") var lastPayout: String,
    @SerialName("payout_date") var payoutDate: String,
    @SerialName("period") var period: String,
    @SerialName("apr") var apr: BigNum,
    @SerialName("precision") var precision: Int,
    @SerialName("balance") var balance: BigNum,
    @SerialName("balance_currency") var balanceCurrency: BigNum,
    @SerialName("in_transit") var inTransit: BigNum,
    @SerialName("balance_available") var balanceAvailable: BigNum,
    @SerialName("summary_balance") var summaryBalance: BigNum,
    @SerialName("summary_balance_currency") var summaryBalanceCurrency: BigNum,
    @SerialName("accrued_interest") var accruedInterest: BigNum,
    @SerialName("accrued_interest_currency") var accruedInterestCurrency: BigNum,
    @SerialName("paid_interest") var paidInterest: BigNum,
    @SerialName("paid_interest_currency") var paidInterestCurrency: BigNum,
    @SerialName("can_delete") var canDelete: Int,
    @SerialName("is_allowed_to_transfer") var isAllowedToTransfer: Boolean,
    @SerialName("logo_name") var logoName: String,
    @SerialName("apy") var apy: BigNum,
    @SerialName("pending_bonus") var pendingBonus: Boolean,
    @SerialName("bonus_list") var bonusList: String
)