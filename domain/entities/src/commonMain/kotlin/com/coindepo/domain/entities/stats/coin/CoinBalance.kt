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

data class CoinBalance(
    val accruedInterest: BigNum,
    val accruedInterestCurrency: BigNum,
    val balance: BigNum,
    val balanceAvailable: BigNum,
    val balanceAvailableCurrency: BigNum,
    val balanceCurrency: BigNum,
    val binance24h: BigNum,
    val binance24hCurrency: BigNum,
    val freezedTrxBalance: BigNum,
    val freezedTrxBalanceCurrency: BigNum,
    val inTransit: BigNum,
    val inTransitCurrency: BigNum,
    val interest: BigNum,
    val interestCurrency: BigNum,
    val interestCurrencyCurrency: BigNum,
    val newTrxBalance: BigNum,
    val newTrxBalanceCurrency: BigNum,
    val pendingTrxBalance: BigNum,
    val pendingTrxBalanceCurrency: BigNum,
    val precision: Int,
    val processingTrxBalance: BigNum,
    val processingTrxBalanceCurrency: BigNum,
    val queuedTrxBalance: BigNum,
    val queuedTrxBalanceCurrency: BigNum,
    val summaryBalance: BigNum,
    val summaryBalanceCurrency: BigNum,
    val displayOrder: Int
)

