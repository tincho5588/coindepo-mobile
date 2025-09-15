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

package com.coindepo.domain.entities.stats.balance

import korlibs.bignumber.BigNum
import kotlinx.serialization.Serializable

data class BorrowBalance(
    val availablePortfolioBalance: BigNum,
    val creditLineLimitCurrency: BigNum,
    val currentLoansCurrency: BigNum,
    val availableCreditLimitCurrency: BigNum,
    val ltv: BigNum,
    val apr: BigNum,
    val aprs: List<LoanApr>
)

@Serializable
data class LoanApr(
    val apr: String,
    val minLtv: Double, // TODO: Migrate this to BigNum
    val maxLtv: Double // TODO: Migrate this to BigNum
)