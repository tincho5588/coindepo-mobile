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

package com.coindepo.repository.implementation.withdraw

import com.coindepo.datasource.contracts.withdraw.RemoteWithdrawDataSource
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.repository.contracts.withdraw.WithdrawRepository
import korlibs.bignumber.BigNum

class WithdrawRepositoryImpl(
    private val remoteWithdrawDataSource: RemoteWithdrawDataSource
) : WithdrawRepository {
    override suspend fun getWithdrawFees(
        coinName: String,
        accountId: Int,
        amount: BigNum,
        address: String,
        tag: String?,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult = remoteWithdrawDataSource.getWithdrawFees(
        coinName,
        accountId,
        amount,
        address,
        tag,
        requestId,
        userName,
        userId,
        clientToken
    ).fold(
        onSuccess = {
            OperationResult.Success(it)
        },
        onFailure = {
            OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
        }
    )

    override suspend fun requestWithdraw(
        coinName: String,
        accountId: Int,
        amount: BigNum,
        address: String,
        tag: String?,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult =
        remoteWithdrawDataSource.requestWithdraw(coinName, accountId, amount, address, tag, requestId, userName, userId, clientToken).fold(
            onSuccess = {
                OperationResult.Success(it)
            },
            onFailure = {
                OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
            }
        )

    override suspend fun confirmWithdraw(
        emailVerificationCode: String,
        twoFACode: String?,
        requestCode: String,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult = remoteWithdrawDataSource.confirmWithdraw(
        emailVerificationCode,
        twoFACode,
        requestCode,
        requestId,
        userName,
        userId,
        clientToken
    ).fold(
        onSuccess = { OperationResult.Success(it) },
        onFailure = {
            OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
        }
    )

    override suspend fun sendEmailVerificationCode(
        userName: String,
        userId: Int,
        clientToken: String,
        requestCode: String
    ): OperationResult = remoteWithdrawDataSource.sendEmailVerificationCode(
        userName,
        userId,
        clientToken,
        requestCode
    ).fold(
        onSuccess = { OperationResult.Success(it) },
        onFailure = {
            OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
        }
    )
}