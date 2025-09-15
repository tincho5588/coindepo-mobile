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

package com.coindepo.datasource.remote.withdraw.service

import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import com.coindepo.datasource.remote.withdraw.data.SendWithdrawVerificationCodeResponseDTO
import com.coindepo.datasource.remote.withdraw.data.WithdrawConfirmationResponseDTO
import com.coindepo.datasource.remote.withdraw.data.WithdrawFeesEstimationResponseDTO
import com.coindepo.datasource.remote.withdraw.data.WithdrawRequestResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path
import korlibs.bignumber.BigNum

interface WithdrawService {
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
    ): ApiResult<WithdrawFeesEstimationResponseDTO>

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
    ): ApiResult<WithdrawRequestResponseDTO>

    suspend fun confirmWithdraw(
        emailVerificationCode: String,
        twoFACode: String?,
        requestCode: String,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): ApiResult<WithdrawConfirmationResponseDTO>

    suspend fun sendEmailVerificationCode(
        userName: String,
        userId: Int,
        clientToken: String,
        requestCode: String
    ): ApiResult<SendWithdrawVerificationCodeResponseDTO>
}

class WithdrawServiceImpl(
    private val client: HttpClient
): WithdrawService {
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
    ): ApiResult<WithdrawFeesEstimationResponseDTO> =
        client.safePost<WithdrawFeesEstimationResponseDTO> {
            url {
                path("withdraw_funds_request")
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("user_id", userId)
                            append("username", userName)
                            append("client_token", clientToken)
                            append("coin", coinName)
                            append("account_id", accountId)
                            append("amount", amount.toString())
                            append("address", address)
                            tag?.let { append("tag", tag) }
                            append("request_id", requestId)
                            append("estimate", true)
                        }
                    )
                )
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
    ): ApiResult<WithdrawRequestResponseDTO> = client.safePost<WithdrawRequestResponseDTO> {
        url {
            path("withdraw_funds_request")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("user_id", userId)
                        append("username", userName)
                        append("client_token", clientToken)
                        append("coin", coinName)
                        append("account_id", accountId)
                        append("amount", amount.toString())
                        append("address", address)
                        tag?.let { append("tag", tag) }
                        append("request_id", requestId)
                    }
                )
            )
        }
    }

    override suspend fun confirmWithdraw(
        emailVerificationCode: String,
        twoFACode: String?,
        requestCode: String,
        requestId: String,
        userName: String,
        userId: Int,
        clientToken: String
    ): ApiResult<WithdrawConfirmationResponseDTO> = client.safePost<WithdrawConfirmationResponseDTO> {
        url {
            path("withdraw_funds_confirmation")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("email_token", emailVerificationCode)
                        twoFACode?.let { append("totp", twoFACode) }
                        append("request_code", requestCode)
                        append("request_id", requestId)
                        append("username", userName)
                        append("user_id", userId)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }

    override suspend fun sendEmailVerificationCode(
        userName: String,
        userId: Int,
        clientToken: String,
        requestCode: String
    ): ApiResult<SendWithdrawVerificationCodeResponseDTO> = client.safePost<SendWithdrawVerificationCodeResponseDTO> {
        url {
            path("resend_withdrawal_code")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("request_code", requestCode)
                        append("username", userName)
                        append("user_id", userId)
                        append("client_token", clientToken)
                    }
                )
            )
        }
    }
}

private var withdrawService: WithdrawService? = null

internal fun getWithdrawService(): WithdrawService {
    if (withdrawService == null) {
        withdrawService = WithdrawServiceImpl(httpClient)
    }

    return withdrawService!!
}