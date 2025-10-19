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

data class DepositPlan(
    val id: String,
    val userAccountId: Int,
    val depositPlanId: Int,
    val accountType: AccountType,
    val accountName: String,
    val walletName: String,
    val openDate: String,
    val lastPayout: String,
    val payoutDate: String,
    val period: String,
    val apr: BigNum,
    val precision: Int,
    val balance: BigNum,
    val balanceCurrency: BigNum,
    val inTransit: BigNum,
    val balanceAvailable: BigNum,
    val summaryBalance: BigNum,
    val summaryBalanceCurrency: BigNum,
    val accruedInterest: BigNum,
    val accruedInterestCurrency: BigNum,
    val paidInterest: BigNum,
    val paidInterestCurrency: BigNum,
    val canDelete: Boolean,
    val isAllowedToTransfer: Boolean,
    val apy: BigNum,
    val pendingBonus: Boolean,
    val bonusList: String,
    val isVisible: Boolean
)

enum class AccountType {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    SEMI_ANNUAL,
    ANNUAL
}