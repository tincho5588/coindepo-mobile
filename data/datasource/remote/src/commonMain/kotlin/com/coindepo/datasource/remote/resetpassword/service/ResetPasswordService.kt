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

package com.coindepo.datasource.remote.resetpassword.service

import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.resetpassword.data.ResetPasswordResponseDTO
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path

interface ResetPasswordService {
    suspend fun requestPasswordReset(email: String): ApiResult<ResetPasswordResponseDTO>
}

class ResetPasswordServiceImpl(
    private val client: HttpClient
) : ResetPasswordService {
    override suspend fun requestPasswordReset(email: String): ApiResult<ResetPasswordResponseDTO> = client.safePost<ResetPasswordResponseDTO> {
        url {
            path("reset_password")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", email)
                        append("lang", "en")
                    }
                )
            )
        }
    }
}

private var resetPasswordService: ResetPasswordService? = null

internal fun getResetPasswordService(): ResetPasswordService {
    if (resetPasswordService == null) {
        resetPasswordService = ResetPasswordServiceImpl(httpClient)
    }

    return resetPasswordService!!
}