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

package com.coindepo.datasource.remote.wallet.service

import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import com.coindepo.datasource.remote.wallet.data.WalletDataResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path

interface WalletDataService {
    suspend fun getWalletData(
        coin: String,
        userName: String,
        clientToken: String
    ): ApiResult<WalletDataResponseDTO>
}

class WalletDataServiceImpl(
    private val client: HttpClient
): WalletDataService {
    override suspend fun getWalletData(
        coin: String,
        userName: String,
        clientToken: String
    ): ApiResult<WalletDataResponseDTO> =
        client.safePost<WalletDataResponseDTO> {
            url {
                path("get_user_wallet")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("username", userName)
                            append("client_token", clientToken)
                            append("coin", coin)
                        }
                    )
                )
            }
        }
}

private var walletDataService: WalletDataService? = null

internal fun getWalletDataService(): WalletDataService {
    if (walletDataService == null) {
        walletDataService = WalletDataServiceImpl(httpClient)
    }

    return walletDataService!!
}