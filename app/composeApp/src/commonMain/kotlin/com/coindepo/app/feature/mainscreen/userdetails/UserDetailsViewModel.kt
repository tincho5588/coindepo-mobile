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

package com.coindepo.app.feature.mainscreen.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coindepo.domain.entities.userdetails.UserDetails
import com.coindepo.domain.usecases.login.LogOutUseCase
import com.coindepo.domain.usecases.login.LoginUseCase
import com.coindepo.domain.usecases.userdetails.GetUserDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserDetailsViewModel(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val loginUseCase: LoginUseCase
): ViewModel() {

    val userDetails: StateFlow<UserDetails?> by lazy { getUserDetailsUseCase.userDetailsFlow }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun fetchUserDetails(updateRefreshingState: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (updateRefreshingState) _isRefreshing.value = true

            getUserDetailsUseCase.fetchUserDetails()
            if (updateRefreshingState) _isRefreshing.value = false
        }
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            logOutUseCase.performLogout()
        }
    }
}