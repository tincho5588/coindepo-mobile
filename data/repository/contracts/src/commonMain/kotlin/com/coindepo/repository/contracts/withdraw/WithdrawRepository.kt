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

package com.coindepo.repository.contracts.withdraw

import com.coindepo.domain.entities.OperationResult
import korlibs.bignumber.BigNum

interface WithdrawRepository {
    suspend fun getWithdrawFees(
        coinName: String,
        accountId: Int,
        amount: BigNum,
        address: String,
        tag: String?,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult

    suspend fun requestWithdraw(
        coinName: String,
        accountId: Int,
        amount: BigNum,
        address: String,
        tag: String?,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult

    suspend fun confirmWithdraw(
        emailVerificationCode: String,
        twoFACode: String?,
        requestCode: String,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult

    suspend fun sendEmailVerificationCode(
        userName: String,
        userId: Int,
        clientToken: String,
        requestCode: String
    ): OperationResult
}