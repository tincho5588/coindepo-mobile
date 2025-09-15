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

package com.coindepo.domain.entities.stats.coin

import korlibs.bignumber.BigNum

data class Coin(
    val coinId: Int,
    val coinName: String,
    val logoUrl: String,
    val production: Boolean,
    val precision: Int,
    val limit24h: BigNum,
    val explorerUrl: String,
    val withdrawLimit: BigNum,
    val binanceTicker: String,
    val description: String,
    val descriptionTicker: String,
    val isStableCoin: Boolean,
    val network: String,
    val depName: String,
    val wthName: String,
    val isActive: Boolean,
    val isToken: Boolean,
    val apy: BigNum,
    val apr: BigNum,
    val fee: BigNum,
    val privatePrice: BigNum,
    val listingPrice: BigNum,
    val privateDiscount: BigNum,
    val coinBalance: CoinBalance?,
    val depositPlans: List<DepositPlan>,
    val availableLoans: AvailableLoans?,
    val order: Int
)

data class AvailableLoans(
    val available: List<String>,
    val rate: String
)

val Coin.supportsTag: Boolean
    get() = when (binanceTicker) {
        "AKT",
        "AXL",
        "TIA",
        "ATOM",
        "DYDX",
        "EOS",
        "HBAR",
        "KAVA",
        "OSMO",
        "SEI",
        "STX",
        "XLM",
        "ZETACHAIN",
        "XRP" -> true

        else -> false
    }