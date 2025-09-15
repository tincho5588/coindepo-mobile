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

package com.coindepo.app.feature.mainscreen.withdraw

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.app.feature.login.ResendVerificationCodeState
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.entities.withdraw.WithdrawFee
import com.coindepo.domain.entities.withdraw.WithdrawRequest
import com.coindepo.domain.usecases.withdraw.ConfirmWithdrawUseCase
import com.coindepo.domain.usecases.withdraw.GetWithdrawFeesEstimateUseCase
import com.coindepo.domain.usecases.withdraw.RequestWithdrawUseCase
import com.coindepo.domain.usecases.withdraw.SendWithdrawEmailVerificationCodeUseCase
import korlibs.bignumber.BigNum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class WithdrawViewModel(
    private val getWithdrawFeesEstimateUseCase: GetWithdrawFeesEstimateUseCase,
    private val requestWithdrawUseCase: RequestWithdrawUseCase,
    private val confirmWithdrawUseCase: ConfirmWithdrawUseCase,
    private val sendWithdrawEmailVerificationCodeUseCase: SendWithdrawEmailVerificationCodeUseCase
) : ViewModel() {

    private val _withdrawProcessState = MutableStateFlow<WithdrawProcessState>(WithdrawProcessState.NotStarted)
    val withdrawProcessState: StateFlow<WithdrawProcessState> = _withdrawProcessState

    private val _resendVerificationCodeState = MutableStateFlow<ResendVerificationCodeState?>(null)
    val resendVerificationCodeState: StateFlow<ResendVerificationCodeState?> = _resendVerificationCodeState

    fun getWithdrawFeesEstimate(
        coin: Coin,
        depositPlan: DepositPlan,
        amount: BigNum,
        address: String,
        tag: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _withdrawProcessState.value = WithdrawProcessState.LoadingFeeEstimate
            _withdrawProcessState.value = when(val result = getWithdrawFeesEstimateUseCase.getWithdrawFeesEstimate(coin, depositPlan, amount, address, tag)) {
                is OperationResult.Failure -> WithdrawProcessState.FeeEstimationFailure(result.e)
                is OperationResult.Success<*> -> WithdrawProcessState.FeeEstimationSuccess(result.data as WithdrawFee)
            }
        }
    }

    fun resetWithdrawProcessState(toState: WithdrawProcessState) {
        _withdrawProcessState.value = toState
    }

    fun requestWithdraw(
        coin: Coin,
        depositPlan: DepositPlan,
        amount: BigNum,
        address: String,
        tag: String?,
        requestId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val fees = (_withdrawProcessState.value as WithdrawProcessState.FeeEstimationSuccess).withdrawFees

            _withdrawProcessState.value = WithdrawProcessState.LoadingWithdrawRequest(fees)
            _withdrawProcessState.value = when(val result = requestWithdrawUseCase.requestWithdraw(coin, depositPlan, amount, address, tag, requestId)) {
                is OperationResult.Failure -> WithdrawProcessState.WithdrawRequestFailure(result.e, fees)
                is OperationResult.Success<*> -> WithdrawProcessState.WithdrawRequestSuccess(amount, address, result.data as WithdrawRequest, fees)
            }
        }
    }

    fun confirmWithdraw(
        emailVerificationCode: String,
        twoFACode: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val withdrawRequestState = (_withdrawProcessState.value as WithdrawProcessState.WithdrawRequestSuccess)

            _withdrawProcessState.value = WithdrawProcessState.LoadingWithdrawConfirmation(withdrawRequestState.amount, withdrawRequestState.address, withdrawRequestState.withdrawRequest, withdrawRequestState.withdrawFees)
            _withdrawProcessState.value = when(val result = confirmWithdrawUseCase.confirmWithdraw(emailVerificationCode, twoFACode, withdrawRequestState.withdrawRequest.requestCode, withdrawRequestState.withdrawRequest.requestId)) {
                is OperationResult.Failure -> WithdrawProcessState.WithdrawConfirmationFailure(result.e)
                is OperationResult.Success<*> -> WithdrawProcessState.WithdrawConfirmationSuccess
            }
        }
    }

    fun sendEmailVerificationCode() {
        viewModelScope.launch(Dispatchers.IO) {
            val withdrawRequestState = (_withdrawProcessState.value as WithdrawProcessState.WithdrawRequestSuccess)

            startResendVerificationCodeCountDown()
            sendWithdrawEmailVerificationCodeUseCase.sendEmailVerificationCode(withdrawRequestState.withdrawRequest.requestCode)
        }
    }

    private fun startResendVerificationCodeCountDown() {
        viewModelScope.launch {
            _resendVerificationCodeState.value = ResendVerificationCodeState(false, 30)
            while (isActive) {
                delay(1.seconds.inWholeMilliseconds)
                val newTimeRemaining = _resendVerificationCodeState.value!!.timeRemaining - 1
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

sealed interface WithdrawProcessState {
    data object NotStarted : WithdrawProcessState

    data object LoadingFeeEstimate : WithdrawProcessState

    data class FeeEstimationSuccess(
        val withdrawFees: WithdrawFee
    ) : WithdrawProcessState

    data class FeeEstimationFailure(
        val e: CoinDepoException
    ): WithdrawProcessState

    data class LoadingWithdrawRequest(
        val withdrawFees: WithdrawFee
    ): WithdrawProcessState

    data class WithdrawRequestSuccess(
        val amount: BigNum,
        val address: String,
        val withdrawRequest: WithdrawRequest,
        val withdrawFees: WithdrawFee
    ): WithdrawProcessState

    data class WithdrawRequestFailure(
        val e: CoinDepoException,
        val withdrawFees: WithdrawFee
    ): WithdrawProcessState

    data class LoadingWithdrawConfirmation(
        val amount: BigNum,
        val address: String,
        val withdrawRequest: WithdrawRequest,
        val withdrawFees: WithdrawFee
    ): WithdrawProcessState

    data object WithdrawConfirmationSuccess: WithdrawProcessState

    data class WithdrawConfirmationFailure(
        val e: CoinDepoException
    ): WithdrawProcessState
}