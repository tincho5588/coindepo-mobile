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

package com.coindepo.app.feature.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.ReloginRequiredException
import com.coindepo.domain.entities.TooManyRequestsException
import com.coindepo.domain.usecases.resetpassword.ResetPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val passwordResetUseCase: ResetPasswordUseCase
): ViewModel() {

    private val _resetPasswordScreenState = MutableStateFlow<ResetPasswordScreenState>(InitialResetPasswordScreenState())
    val resetPasswordScreenState: StateFlow<ResetPasswordScreenState> = _resetPasswordScreenState

    private val _error = MutableStateFlow<ResetPasswordError?>(null)
    val error: StateFlow<ResetPasswordError?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun requestPasswordReset(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            when (val result = passwordResetUseCase.requestPasswordReset(email)) {
                is OperationResult.Failure -> {
                    _error.value = when (result.e) {
                        is NoNetworkException -> ResetPasswordError.NO_NETWORK
                        is OtherErrorException -> ResetPasswordError.UNKNOWN_ERROR
                        is TooManyRequestsException -> ResetPasswordError.TOO_FREQUENT_REQUESTS
                        is ReloginRequiredException -> ResetPasswordError.UNKNOWN_ERROR
                    }
                    _isLoading.value = false
                }
                is OperationResult.Success<*> -> {
                    _resetPasswordScreenState.value = PasswordResetRequestedScreenState(email)
                    _isLoading.value = false
                }
            }
        }
    }

    fun cleanError() {
        _error.value = null
    }
}

sealed interface ResetPasswordScreenState

data class InitialResetPasswordScreenState(val email: String? = null): ResetPasswordScreenState
data class PasswordResetRequestedScreenState(val email: String): ResetPasswordScreenState

enum class ResetPasswordError {
    TOO_FREQUENT_REQUESTS,
    NO_NETWORK,
    UNKNOWN_ERROR
}

