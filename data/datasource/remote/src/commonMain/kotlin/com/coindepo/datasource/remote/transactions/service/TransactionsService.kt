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