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

package com.coindepo.app.feature.mainscreen.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.usecases.depositplans.TransferBetweenPlansUseCase
import korlibs.bignumber.BigNum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransferViewModel(
    private val transferBetweenPlansUseCase: TransferBetweenPlansUseCase
): ViewModel() {
    private val _state = MutableStateFlow<TransferState?>(null)
    val state: StateFlow<TransferState?> = _state

    fun transfer(
        coin: Coin,
        fromPlan: DepositPlan,
        toPlan: DepositPlan,
        amount: BigNum,
        currency: Currency
    ) {
        viewModelScope.launch {
            _state.value = InProgress

            when (val result = transferBetweenPlansUseCase.transferBetweenPlans(
                coin = coin,
                fromPlan = fromPlan,
                toPlan = toPlan,
                amount = amount,
                currency = currency
            )) {
                is OperationResult.Failure -> _state.value = when(result.e) {
                    is NoNetworkException -> NoNetwork
                    else -> Failure
                }
                is OperationResult.Success<*> -> _state.value = Success
            }
        }
    }

    fun resetState() {
        _state.value = null
    }
}

sealed interface TransferState

data object InProgress: TransferState

data object Success: TransferState

data object Failure: TransferState

data object NoNetwork: TransferState