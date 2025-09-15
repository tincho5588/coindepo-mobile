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

package com.coindepo.datasource.remote.login.service

import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.login.data.LoginResponseDTO
import com.coindepo.datasource.remote.login.data.VerificationCodeResponseDTO
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path

interface LoginService {
    suspend fun emailLogin(
        email: String,
        password: String,
        emailVerificationCode: String? = null,
        twoFactorCode: String? = null
    ): ApiResult<LoginResponseDTO>

    suspend fun resendVerificationCode(email: String): ApiResult<VerificationCodeResponseDTO>
}

class LoginServiceImpl(
    private val client: HttpClient
) : LoginService {
    override suspend fun emailLogin(
        email: String,
        password: String,
        emailVerificationCode: String?,
        twoFactorCode: String?
    ): ApiResult<LoginResponseDTO> =
        client.safePost<LoginResponseDTO> {
            url {
                path("login")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("email", email)
                            append("password", password)
                            append("username", email)
                            append("no_secure_check", "0")
                            append("lang", "en")
                            emailVerificationCode?.let { append("verify_code", it) }
                            twoFactorCode?.let { append("totp", it) }
                        }
                    )
                )
            }
        }

    override suspend fun resendVerificationCode(email: String): ApiResult<VerificationCodeResponseDTO> =
        client.safePost<VerificationCodeResponseDTO> {
            url {
                path("verify_login")
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

private var loginService: LoginService? = null

internal fun getLoginService(): LoginService {
    if (loginService == null) {
        loginService = LoginServiceImpl(httpClient)
    }

    return loginService!!
}