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

package com.coindepo.datasource.remote.withdraw

import com.coindepo.datasource.contracts.withdraw.RemoteWithdrawDataSource
import com.coindepo.datasource.remote.utils.ErrorApiResult
import com.coindepo.datasource.remote.utils.NoNetworkApiResult
import com.coindepo.datasource.remote.utils.SuccessApiResult
import com.coindepo.datasource.remote.utils.asResult
import com.coindepo.datasource.remote.withdraw.data.WithdrawFeesEstimationResponseDTO
import com.coindepo.datasource.remote.withdraw.data.asWithdrawFee
import com.coindepo.datasource.remote.withdraw.data.asWithdrawRequest
import com.coindepo.datasource.remote.withdraw.service.WithdrawService
import com.coindepo.datasource.remote.withdraw.service.getWithdrawService
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.withdraw.WithdrawFee
import com.coindepo.domain.entities.withdraw.WithdrawRequest
import korlibs.bignumber.BigNum

class RemoteWithdrawDataSourceImpl internal constructor(
    private val withdrawService: WithdrawService
) : RemoteWithdrawDataSource {

    constructor() : this(getWithdrawService())

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
    ): Result<WithdrawFee> {
        val result = withdrawService.getWithdrawFees(
            coinName,
            accountId,
            amount,
            address,
            tag,
            requestId,
            userName,
            userId,
            clientToken
        )

        return when(result) {
            is ErrorApiResult<*> -> {
                if (result.maybeBody?.code == "FB_ERROR_CALCULATING_FEE") {
                    Result.success(WithdrawFee(requestId, null))
                } else {
                    Result.failure(OtherErrorException())
                }
            }
            is NoNetworkApiResult<*> -> Result.failure(NoNetworkException())
            is SuccessApiResult<WithdrawFeesEstimationResponseDTO> -> Result.success(result.body.asWithdrawFee(requestId))
        }
    }

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
    ): Result<WithdrawRequest> = withdrawService.requestWithdraw(
        coinName,
        accountId,
        amount,
        address,
        tag,
        requestId,
        userName,
        userId,
        clientToken
    ).asResult { this.asWithdrawRequest }

    override suspend fun confirmWithdraw(
        emailVerificationCode: String,
        twoFACode: String?,
        requestCode: String,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): Result<Unit> = withdrawService.confirmWithdraw(
        emailVerificationCode,
        twoFACode,
        requestCode,
        requestId,
        userName,
        userId,
        clientToken
    ).asResult { }

    override suspend fun sendEmailVerificationCode(
        userName: String,
        userId: Int,
        clientToken: String,
        requestCode: String
    ): Result<Unit> = withdrawService.sendEmailVerificationCode(
        userName,
        userId,
        clientToken,
        requestCode,
    ).asResult { }
}