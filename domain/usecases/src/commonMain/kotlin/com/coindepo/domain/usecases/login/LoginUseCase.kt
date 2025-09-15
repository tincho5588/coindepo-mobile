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

package com.coindepo.domain.usecases.login

import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.login.LoginState
import com.coindepo.repository.contracts.login.LoginRepository
import kotlinx.coroutines.flow.StateFlow

class LoginUseCase(
    private val loginRepository: LoginRepository
) {
    val loginState: StateFlow<LoginState> by lazy { loginRepository.loginState }

    suspend fun performEmailLogin(
        email: String,
        password: String,
        emailVerificationCode: String?,
        twoFactorCode: String?
    ): OperationResult = loginRepository.performEmailLogin(email, password, emailVerificationCode, twoFactorCode)
}