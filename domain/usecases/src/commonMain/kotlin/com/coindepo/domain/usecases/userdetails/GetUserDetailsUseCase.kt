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

package com.coindepo.domain.usecases.userdetails

import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.userdetails.UserDetails
import com.coindepo.domain.usecases.LoggedInUseCase
import com.coindepo.domain.usecases.login.GetUserSessionUseCase
import com.coindepo.domain.usecases.login.LogOutUseCase
import com.coindepo.repository.contracts.userdetails.UserDetailsRepository
import kotlinx.coroutines.flow.StateFlow

class GetUserDetailsUseCase(
    private val userDetailsRepository: UserDetailsRepository,
    getUserSessionUseCase: GetUserSessionUseCase,
    logOutUseCase: LogOutUseCase
): LoggedInUseCase(getUserSessionUseCase, logOutUseCase) {
    val userDetailsFlow: StateFlow<UserDetails?> by lazy { userDetailsRepository.userDetails }

    suspend fun fetchUserDetails(
    ): OperationResult = executeWithUserSession { email, userName, userId, clientToken ->
        userDetailsRepository.fetchUserDetails(userId, userName, email, clientToken)
    }
}