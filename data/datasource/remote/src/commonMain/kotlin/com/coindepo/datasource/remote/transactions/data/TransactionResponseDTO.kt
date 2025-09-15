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

package com.coindepo.datasource.remote.transactions.data

import com.coindepo.datasource.remote.stats.data.BaseResponseDTO
import com.coindepo.datasource.remote.stats.data.COIN_LOGO_URL
import com.coindepo.domain.entities.transactions.Operation
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.transactions.TransactionStatus
import com.coindepo.domain.entities.transactions.TransactionType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class TransactionsResponseDTO(
    @SerialName("total_trx") val totalTrx: Int,
    @SerialName("transactions") val transactions: List<TransactionDTO>,
    override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

@Serializable
data class TransactionDTO(
    @SerialName("entry_id") val entryId: String,
    @SerialName("trx_id") val trxId: Int,
    @SerialName("title") val title: String,
    @SerialName("first_txt") val firstTxt: String,
    @SerialName("second_txt") val secondTxt: String,
    @SerialName("foreign_trx_id") val foreignTrxId: String,
    @SerialName("amount") val amount: String,
    @SerialName("coin") val coin: String,
    @SerialName("coin_id") val coinId: Int,
    @SerialName("binance_ticker") val binanceTicker: String,
    @SerialName("user_from") val userFrom: Int,
    @SerialName("user_to") val userTo: Int,
    @SerialName("trx_type_id") val trxTypeId: Int,
    @SerialName("trx_status_id") val trxStatusId: Int,
    @SerialName("operation") val operation: String,
    @SerialName("source_address") val sourceAddress: String? = null,
    @SerialName("destination_address") val destinationAddress: String? = null,
    @SerialName("destination_tag") val destinationTag: String? = null,
    @SerialName("tx_hash") val txHash: String? = null,
    @SerialName("create_date") val createDate: String,
    @SerialName("last_update") val lastUpdate: String,
    @SerialName("logo_name") val logoName: String,
    @SerialName("is_token") val isToken: Int?
)

@OptIn(ExperimentalTime::class)
val TransactionDTO.asTransaction: Transaction
    get() = Transaction(
        entryId,
        trxId,
        title,
        firstTxt,
        secondTxt,
        foreignTrxId,
        amount,
        coin,
        binanceTicker,
        "$COIN_LOGO_URL$logoName.svg",
        userFrom,
        userTo,
        trxTypeId.asTransactionType,
        trxStatusId.asTransactionStatus,
        operation.asOperation,
        sourceAddress,
        destinationAddress,
        destinationTag,
        txHash,
        createDate.asInstant(),
        lastUpdate.asInstant(),
        isToken == 1
    )

val Int.asTransactionType: TransactionType
    get() = when(this) {
        1 -> TransactionType.BETWEEN_MY_ACCOUNTS
        3 -> TransactionType.DEPOSIT
        4 -> TransactionType.WITHDRAWAL
        5 -> TransactionType.INTEREST_PAYMENT
        6 -> TransactionType.REFERRAL_PAYMENT
        7 -> TransactionType.LOAN_WITHDRAWAL
        8 -> TransactionType.BETWEEN_MY_ACCOUNTS
        9 -> TransactionType.LOAN_REPAYMENT
        10 -> TransactionType.AFFILIATE_PAYMENT
        11 -> TransactionType.PROMO_BONUS_PAYMENT
        12 -> TransactionType.REWARDS_PAYMENT
        13 -> TransactionType.INSTANT_SWAP
        17 -> TransactionType.PROMO_BONUS_PAYMENT
        20 -> TransactionType.REBALANCE_WITHDRAWAL
        50 -> TransactionType.LOAN_LIQUIDATION
        51 -> TransactionType.LOAN_INTEREST_REPAYMENT
        else -> throw IllegalArgumentException()
    }

val Int.asTransactionStatus: TransactionStatus
    get() = when(this) {
        2 -> TransactionStatus.PROCESSING
        3 -> TransactionStatus.PENDING
        4 -> TransactionStatus.COMPLETED
        5 -> TransactionStatus.CANCELLED
        6 -> TransactionStatus.FAILED
        else -> throw IllegalArgumentException()
    }

val String.asOperation: Operation
    get() = when(this) {
        "debit" -> Operation.DEBIT
        "credit" -> Operation.CREDIT
        else -> throw IllegalArgumentException()
    }

@OptIn(ExperimentalTime::class)
fun String.asInstant(): Instant {
    val customFormat = LocalDateTime.Format {
        year(padding = Padding.NONE)
        char('-')
        monthNumber()
        char('-')
        day()
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
    }
    return customFormat.parse(this).toInstant(UtcOffset(0))
}