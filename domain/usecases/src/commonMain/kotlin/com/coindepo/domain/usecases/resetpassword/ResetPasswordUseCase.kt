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

package com.coindepo.domain.usecases.resetpassword

import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.TooManyRequestsException
import com.coindepo.repository.contracts.resetpassword.ResetPasswordRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ResetPasswordUseCase(
    private val resetPasswordRepository: ResetPasswordRepository
) {
    suspend fun requestPasswordReset(
        email: String
    ): OperationResult =
        with(resetPasswordRepository.lastRequested) {
            if (this == null || this > Clock.System.now().plus(1.minutes)) {
                resetPasswordRepository.requestPasswordReset(email)
            } else {
                OperationResult.Failure(TooManyRequestsException())
            }
        }
}