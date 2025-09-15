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

package com.coindepo.domain.usecases

import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.ReloginRequiredException
import com.coindepo.domain.usecases.login.GetUserSessionUseCase
import com.coindepo.domain.usecases.login.LogOutUseCase

abstract class LoggedInUseCase(
    val getUserSessionUseCase: GetUserSessionUseCase,
    val logOutUseCase: LogOutUseCase
) {

    suspend fun executeWithUserSession(
        block: suspend (email: String, userName: String, userId: Int, clientToken: String) -> OperationResult
    ): OperationResult = getUserSessionUseCase.getUserSession()?.let {
        val result = block(it.email, it.userName, it.userId, it.accessToken)

        if (result is OperationResult.Failure && result.e is ReloginRequiredException) {
            logOutUseCase.performLogout()
        } else {
            result
        }
    } ?: run { logOutUseCase.performLogout() }
}