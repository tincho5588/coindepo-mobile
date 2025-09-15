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

package com.coindepo.datasource.remote.stats.service

import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.stats.data.AccountStatsDTO
import com.coindepo.datasource.remote.stats.data.BalanceStatsDTO
import com.coindepo.datasource.remote.stats.data.BorrowStatsDTO
import com.coindepo.datasource.remote.stats.data.TokensStatsDTO
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import com.coindepo.domain.entities.currency.Currency
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path

interface StatsService {
    suspend fun getAccountStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): ApiResult<AccountStatsDTO>

    suspend fun getTokensStats(
        userName: String,
        currency: Currency,
        clientToken: String
    ): ApiResult<TokensStatsDTO>

    suspend fun getBorrowStats(
        userName: String,
        currency: Currency,
        clientToken: String
    ): ApiResult<BorrowStatsDTO>

    suspend fun getBalanceStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): ApiResult<BalanceStatsDTO>
}

class StatsServiceImpl(
    private val client: HttpClient
) : StatsService {

    override suspend fun getAccountStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): ApiResult<AccountStatsDTO> = client.safePost<AccountStatsDTO> {
        url {
            path("get_account_stats")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("user_id", userId)
                        append("currency", currency.name)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }

    override suspend fun getTokensStats(
        userName: String,
        currency: Currency,
        clientToken: String
    ): ApiResult<TokensStatsDTO> = client.safePost<TokensStatsDTO> {
        url {
            path("get_token_stats")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("currency", currency.name)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }

    override suspend fun getBorrowStats(
        userName: String,
        currency: Currency,
        clientToken: String
    ): ApiResult<BorrowStatsDTO> = client.safePost<BorrowStatsDTO> {
        url {
            path("get_borrow_stats")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("currency", currency.name)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }

    override suspend fun getBalanceStats(
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): ApiResult<BalanceStatsDTO> = client.safePost<BalanceStatsDTO> {
        url {
            path("get_balances")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("user_id", userId)
                        append("currency", currency.name)
                        append("cummulative", true)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }
}

private var statsService: StatsService? = null

internal fun getStatsService(): StatsService {
    if (statsService == null) {
        statsService = StatsServiceImpl(httpClient)
    }

    return statsService!!
}