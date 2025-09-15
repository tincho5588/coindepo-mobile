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
import com.coindepo.datasource.remote.stats.data.SupportedCoinsDTO
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path

interface CoinsService {
    suspend fun getSupportedCoins(userName: String, clientToken: String): ApiResult<SupportedCoinsDTO>
    suspend fun getSupportedTokens(userName: String, clientToken: String): ApiResult<SupportedCoinsDTO>
}

class CoinsServiceImpl(
    private val client: HttpClient
) : CoinsService {

    override suspend fun getSupportedCoins(
        userName: String,
        clientToken: String
    ): ApiResult<SupportedCoinsDTO> = client.safePost<SupportedCoinsDTO> {
        url {
            path("supported_coins")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }

    override suspend fun getSupportedTokens(
        userName: String,
        clientToken: String
    ): ApiResult<SupportedCoinsDTO> = client.safePost<SupportedCoinsDTO> {
        url {
            path("supported_tokens")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }
}

private var coinsService: CoinsService? = null

internal fun getCoinsService(): CoinsService {
    if (coinsService == null) {
        coinsService = CoinsServiceImpl(httpClient)
    }

    return coinsService!!
}