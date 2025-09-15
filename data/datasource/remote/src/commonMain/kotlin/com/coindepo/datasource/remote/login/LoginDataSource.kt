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

package com.coindepo.datasource.remote.login

import com.coindepo.datasource.contracts.login.LoginDataSource
import com.coindepo.datasource.remote.login.data.LoginResponseDTO
import com.coindepo.datasource.remote.login.data.asEmailLoginResult
import com.coindepo.datasource.remote.login.service.LoginService
import com.coindepo.datasource.remote.login.service.getLoginService
import com.coindepo.datasource.remote.utils.ErrorApiResult
import com.coindepo.datasource.remote.utils.NoNetworkApiResult
import com.coindepo.datasource.remote.utils.SuccessApiResult
import com.coindepo.datasource.remote.utils.asResult
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.login.BlockedLoginState
import com.coindepo.domain.entities.login.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LoginDataSourceImpl internal constructor(
    private val loginService: LoginService
): LoginDataSource {

    constructor() : this(getLoginService())

    override suspend fun performEmailLogin(
        email: String,
        password: String,
        emailVerificationCode: String?,
        twoFactorCode: String?
    ): Result<LoginState> = withContext(Dispatchers.IO) {
        val result = loginService.emailLogin(
            email = email,
            password = password,
            emailVerificationCode = emailVerificationCode,
            twoFactorCode = twoFactorCode
        )

        when(result) {
            is ErrorApiResult<*> -> {
                if (result.httpResponse?.status?.value == 401) {
                    Result.success(BlockedLoginState(email, password))
                } else {
                    Result.failure(OtherErrorException())
                }
            }
            is NoNetworkApiResult -> Result.failure(NoNetworkException())
            is SuccessApiResult<LoginResponseDTO> -> Result.success(result.body.asEmailLoginResult(email, password, emailVerificationCode))
        }
    }

    override suspend fun resendVerificationCode(email: String): Result<Unit> =
        loginService.resendVerificationCode(
            email = email
        ).asResult {  }
}