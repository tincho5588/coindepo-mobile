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

@file:OptIn(ExperimentalTime::class)

package com.coindepo.domain.entities.transactions

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Transaction(
    val entryId: String,
    val trxId: Int,
    val title: String,
    val firstTxt: String,
    val secondTxt: String,
    val foreignTrxId: String,
    val amount: String,
    val coinName: String,
    val coinBinanceTicker: String,
    val coinLogoUrl: String,
    val userFrom: Int,
    val userTo: Int,
    val trxType: TransactionType,
    val trxStatus: TransactionStatus,
    val operation: Operation,
    val sourceAddress: String? = null,
    val destinationAddress: String? = null,
    val destinationTag: String? = null,
    val txHash: String? = null,
    val createDate: Instant,
    val lastUpdate: Instant,
    val isToken: Boolean
)

enum class Operation {
    DEBIT,
    CREDIT
}

enum class TransactionType {
    BETWEEN_MY_ACCOUNTS,
    DEPOSIT,
    WITHDRAWAL,
    INTEREST_PAYMENT,
    REFERRAL_PAYMENT,
    LOAN_WITHDRAWAL,
    LOAN_REPAYMENT,
    AFFILIATE_PAYMENT,
    PROMO_BONUS_PAYMENT,
    REWARDS_PAYMENT,
    INSTANT_SWAP,
    REBALANCE_WITHDRAWAL,
    LOAN_LIQUIDATION,
    LOAN_INTEREST_REPAYMENT
}

enum class TransactionStatus(
    val id: Int
) {
    PROCESSING(2),
    PENDING(3),
    COMPLETED(4),
    CANCELLED(5),
    FAILED(6)
}