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

package com.coindepo.app.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.login.LoginState
import com.coindepo.domain.usecases.login.LogOutUseCase
import com.coindepo.domain.usecases.login.LoginUseCase
import com.coindepo.domain.usecases.login.ResendVerificationCodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val resendVerificationCodeUseCase: ResendVerificationCodeUseCase,
    private val logOutUseCase: LogOutUseCase
): ViewModel() {

    val loginState: StateFlow<LoginState> by lazy { loginUseCase.loginState }

    private val _loginError = MutableStateFlow<LoginError?>(null)
    val loginError: StateFlow<LoginError?> = _loginError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _resendVerificationCodeState = MutableStateFlow(ResendVerificationCodeState(true, 0))
    val resendVerificationCodeState: StateFlow<ResendVerificationCodeState> = _resendVerificationCodeState

    fun performEmailLogin(
        email: String,
        password: String,
        emailVerificationCode: String? = null,
        twoFactorCode: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            if (emailVerificationCode == null && twoFactorCode == null) startResendVerificationCodeCountDown()

            when(val result = loginUseCase.performEmailLogin(email, password, emailVerificationCode, twoFactorCode)) {
                is OperationResult.Failure -> {
                    _loginError.value = when(result.e) {
                        is NoNetworkException -> LoginError.NO_NETWORK
                        else -> LoginError.UNKNOWN_ERROR
                    }
                    _isLoading.value = false
                }
                is OperationResult.Success<*> -> {
                    _isLoading.value = false
                }
            }
        }
    }

    fun resendVerificationCode(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            startResendVerificationCodeCountDown()
            when(resendVerificationCodeUseCase.resendVerificationCode(email)) {
                is OperationResult.Failure -> ResendVerificationCodeResult.FAILURE
                is OperationResult.Success<*> -> ResendVerificationCodeResult.SUCCESS
            }
        }
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            logOutUseCase.performLogout()
        }
    }

    fun cleanError() {
        _loginError.value = null
    }

    private fun startResendVerificationCodeCountDown() {
        viewModelScope.launch {
            _resendVerificationCodeState.value = ResendVerificationCodeState(false, 30)
            while (isActive) {
                delay(1.seconds.inWholeMilliseconds)
                val newTimeRemaining = _resendVerificationCodeState.value.timeRemaining - 1
                if (newTimeRemaining == 0) {
                    _resendVerificationCodeState.value = ResendVerificationCodeState(true, 0)
                    return@launch
                } else {
                    _resendVerificationCodeState.value = ResendVerificationCodeState(false, newTimeRemaining)
                }
            }
        }
    }
}

enum class LoginError {
    NO_NETWORK,
    UNKNOWN_ERROR
}

enum class ResendVerificationCodeResult {
    SUCCESS,
    FAILURE
}

data class ResendVerificationCodeState(
    val enabled: Boolean,
    val timeRemaining: Int
)