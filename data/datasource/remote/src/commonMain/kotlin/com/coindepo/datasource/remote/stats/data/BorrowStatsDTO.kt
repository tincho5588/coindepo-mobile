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

@kotlinx.serialization.Serializable
data class BorrowStatsDTO(
    @SerialName("available_portfolio_balance") val availablePortfolioBalance: BigNum? = null,
    @SerialName("credit_line_limit_currency") val creditLineLimitCurrency: BigNum? = null,
    @SerialName("current_loans_currency") val currentLoansCurrency: BigNum? = null,
    @SerialName("available_credit_limit_currency") val availableCreditLimitCurrency: BigNum? = null,
    @SerialName("ltv") val ltv: BigNum? = null,
    @SerialName("apr") val apr: BigNum? = null,
    @SerialName("aprs") val aprs: List<BigNum> = emptyList(),
    @SerialName("currency") val currency: String? = null,
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("loans_by_coin") val loansByCoin: List<LoansByCoin> = emptyList(),
    override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

@Serializable
data class LoansByCoin(
    @SerialName("coin_id") val coinId: Int,
    @SerialName("available") val available: List<BigNum>,
    @SerialName("rate") val rate: BigNum
)