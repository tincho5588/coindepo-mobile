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

package com.coindepo.app.feature.mainscreen.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.wallet.Wallet
import com.coindepo.domain.usecases.wallet.GetWalletDataUseCase
import com.coindepo.domain.usecases.login.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WalletDataViewModel(
    private val getWalletDataUseCase: GetWalletDataUseCase
): ViewModel() {
    private val _walletData = MutableStateFlow<Wallet?>(null)
    val walletData: MutableStateFlow<Wallet?> = _walletData

    fun getWalletData(
        coin: String
    ) {
        viewModelScope.launch {
            val result = getWalletDataUseCase.getWalletData(coin)

            if (result is OperationResult.Success<*>) {
                _walletData.value = result.data as? Wallet
            }
        }
    }
}