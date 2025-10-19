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

package com.coindepo.datasource.remote.transactions.service

import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.transactions.data.CancelTransactionResponseDTO
import com.coindepo.datasource.remote.transactions.data.TransactionsResponseDTO
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import com.coindepo.domain.entities.stats.coin.AccountType
import com.coindepo.domain.entities.transactions.TransactionType
import com.coindepo.domain.entities.transactions.TransactionsFilters
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface TransactionsService {
    suspend fun getTransactions(
        userName: String,
        clientToken: String,
        page: Int,
        pageSize: Int,
        transactionsFilters: TransactionsFilters
    ): ApiResult<TransactionsResponseDTO>

    suspend fun cancelTransaction(
        userName: String,
        clientToken: String,
        entryId: String
    ): ApiResult<CancelTransactionResponseDTO>
}

class TransactionsServiceImpl(
    private val client: HttpClient
): TransactionsService {
    override suspend fun getTransactions(
        userName: String,
        clientToken: String,
        page: Int,
        pageSize: Int,
        transactionsFilters: TransactionsFilters
    ): ApiResult<TransactionsResponseDTO> =
        client.safePost<TransactionsResponseDTO> {
            url {
                path("get_transactions_list")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("username", userName)
                            append("client_token", clientToken)
                            append("tz_offset", 0)
                            append("lang", "en")
                            append("desc", 1)
                            append("page", page)
                            append("per_page", pageSize)
                            transactionsFilters.dateRange?.let {
                                val formatter = LocalDateTime.Format {
                                    year()
                                    char('-')
                                    monthNumber()
                                    char('-')
                                    day()
                                }
                                append("from_date", Instant.fromEpochMilliseconds(it.startDateMillis).toLocalDateTime(TimeZone.UTC).format(formatter))
                                append("to_date", Instant.fromEpochMilliseconds(it.endDateMillis).toLocalDateTime(TimeZone.UTC).format(formatter))
                            }
                            transactionsFilters.selectedAsset?.let { append("coin_id", it.coinId) }
                            transactionsFilters.accountType?.let {
                                append(
                                    "account_type",
                                    when (it) {
                                        AccountType.DAILY -> "daily"
                                        AccountType.WEEKLY -> "weekly"
                                        AccountType.MONTHLY -> "monthly"
                                        AccountType.QUARTERLY -> "quarterly"
                                        AccountType.SEMI_ANNUAL -> "half-yearly"
                                        AccountType.ANNUAL -> "yearly"
                                    }
                                )
                            }
                            transactionsFilters.transactionType?.let {
                                append(
                                    "trx_type_id",
                                    when(it) {
                                        TransactionType.DEPOSIT -> "3"
                                        TransactionType.WITHDRAWAL -> "4"
                                        TransactionType.REFERRAL_PAYMENT -> "6"
                                        TransactionType.LOAN_WITHDRAWAL -> "7"
                                        TransactionType.LOAN_REPAYMENT -> "9"
                                        TransactionType.AFFILIATE_PAYMENT -> "10"
                                        TransactionType.BETWEEN_MY_ACCOUNTS -> "1,8"
                                        TransactionType.INTEREST_PAYMENT -> "5"
                                        TransactionType.PROMO_BONUS_PAYMENT -> "11,17"
                                        TransactionType.REBALANCE_WITHDRAWAL -> "20"
                                        TransactionType.REWARDS_PAYMENT -> "12"
                                        TransactionType.INSTANT_SWAP -> "13"
                                        TransactionType.LOAN_LIQUIDATION -> "50"
                                        TransactionType.LOAN_INTEREST_REPAYMENT -> "51"
                                    }
                                )
                            }
                        }
                    )
                )
            }
        }

    override suspend fun cancelTransaction(
        userName: String,
        clientToken: String,
        entryId: String
    ): ApiResult<CancelTransactionResponseDTO> =
        client.safePost<CancelTransactionResponseDTO> {
            url {
                path("cancel_withdrawal")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("username", userName)
                            append("client_token", clientToken)
                            append("trxid", entryId)
                        }
                    )
                )
            }
        }
}

private var transactionsService: TransactionsService? = null

internal fun getTransactionsService(): TransactionsService {
    if (transactionsService == null) {
        transactionsService = TransactionsServiceImpl(httpClient)
    }

    return transactionsService!!
}