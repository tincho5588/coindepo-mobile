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

package com.coindepo.repository.implementation.login

import com.coindepo.datasource.contracts.login.LoginDataSource
import com.coindepo.datasource.contracts.login.UserSessionDataSource
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.login.LoggedInLoginState
import com.coindepo.domain.entities.login.LoggedOutLoginState
import com.coindepo.domain.entities.login.LoginState
import com.coindepo.domain.entities.login.UserSession
import com.coindepo.repository.contracts.login.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginRepositoryImpl(
    private val loginDataSource: LoginDataSource,
    private val userSessionDataSource: UserSessionDataSource
) : LoginRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val userSession: StateFlow<UserSession?>
        get() = userSessionDataSource.userSessionFlow.stateIn(
        scope = scope,
        started= SharingStarted.Eagerly,
        initialValue = null
    )

    override val loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoggedOutLoginState)

    init {
        scope.launch {
            userSession.collect { userSession ->
                loginState.value = if (userSession != null) {
                    LoggedInLoginState(userSession.email, userSession)
                } else {
                    LoggedOutLoginState
                }
            }
        }
    }

    override suspend fun performEmailLogin(
        email: String,
        password: String,
        emailVerificationCode: String?,
        twoFactorCode: String?
    ): OperationResult = withContext(Dispatchers.IO) {
        loginDataSource.performEmailLogin(email, password, emailVerificationCode, twoFactorCode)
            .fold(
                onSuccess = { newLoginState ->
                    if (newLoginState is LoggedInLoginState) {
                        userSessionDataSource.saveUserSession(newLoginState.userSession)
                    }
                    loginState.value = newLoginState
                    OperationResult.Success(newLoginState)
                },
                onFailure = { e ->
                    OperationResult.Failure(
                        e as? CoinDepoException ?: OtherErrorException()
                    )
                }
            )
    }

    override suspend fun resendVerificationCode(email: String): OperationResult = withContext(Dispatchers.IO) {
        loginDataSource.resendVerificationCode(email)
            .fold(
                onSuccess = {
                    OperationResult.Success(Unit)
                },
                onFailure = { e ->
                    OperationResult.Failure(
                        e as? CoinDepoException ?: OtherErrorException()
                    )
                }
            )
    }

    override suspend fun getUserSession(): UserSession? =
        userSessionDataSource.getUserSession()

    override suspend fun logout(): OperationResult {
        userSessionDataSource.deleteAll()
        loginState.value = LoggedOutLoginState
        return OperationResult.Success(Unit)
    }
}