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

package com.coindepo.app.feature.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.tier.UserTier
import com.coindepo.domain.usecases.depositplans.SetDepositPlanVisibleUseCase
import com.coindepo.domain.usecases.stats.GetAccountStatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountStatsViewModel(
    private val getAccountStatsUseCase: GetAccountStatsUseCase,
    private val setDepositPlanVisibleUseCase: SetDepositPlanVisibleUseCase
) : ViewModel() {

    private val _currency = MutableStateFlow(Currency.USD)
    val currency: StateFlow<Currency> = _currency

    val accountStats: StateFlow<AccountStats?> by lazy { getAccountStatsUseCase.accountStats }
    val accountBalance: StateFlow<AccountBalance?> by lazy { getAccountStatsUseCase.accountBalance }
    val borrowBalance: StateFlow<BorrowBalance?> by lazy { getAccountStatsUseCase.borrowBalance }
    val userTier: StateFlow<UserTier?> by lazy { getAccountStatsUseCase.userTier }
    val coins: StateFlow<List<Coin>> by lazy { getAccountStatsUseCase.coins }
    val tokens: StateFlow<List<Coin>> by lazy { getAccountStatsUseCase.tokens }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun fetchAccountStats(updateRefreshingState: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchAccountStats(updateRefreshingState, _currency.value)
        }
    }

    private suspend fun fetchAccountStats(updateRefreshingState: Boolean = false, currency: Currency) {
        if (updateRefreshingState) _isRefreshing.value = true
        getAccountStatsUseCase.fetchAccountStats(currency)
        if (updateRefreshingState) _isRefreshing.value = false
    }

    fun changeCurrency(newCurrency: Currency) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchAccountStats(true, newCurrency)
            _currency.value = newCurrency
        }
    }

    fun setDepositPlanVisible(visible: Boolean, depositPlanId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true

            setDepositPlanVisibleUseCase.setDepositPlanVisibility(visible, depositPlanId, currency.value)

            _isRefreshing.value = false
        }
    }
}