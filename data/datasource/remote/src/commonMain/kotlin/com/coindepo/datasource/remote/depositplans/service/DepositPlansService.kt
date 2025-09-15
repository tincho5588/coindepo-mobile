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

package com.coindepo.datasource.remote.depositplans.service

import com.coindepo.datasource.remote.depositplans.data.OpenDepositPlanResponseDTO
import com.coindepo.datasource.remote.depositplans.data.SetAccountVisibilityResponseDTO
import com.coindepo.datasource.remote.depositplans.data.TransferBetweenAccountsResponseDTO
import com.coindepo.datasource.remote.httpClient
import com.coindepo.datasource.remote.utils.ApiResult
import com.coindepo.datasource.remote.utils.safePost
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.http.path
import korlibs.bignumber.BigNum

interface DepositPlansService {
    suspend fun setDepositPlanVisibility(
        visibile: Boolean,
        walletName: String,
        depositPlanId: Int,
        userName: String,
        clientToken: String
    ): ApiResult<SetAccountVisibilityResponseDTO>

    suspend fun transferBetweenPlans(
        fromAccountId: Int,
        toAccountId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): ApiResult<TransferBetweenAccountsResponseDTO>

    suspend fun openDepositPlan(
        depositPlanId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): ApiResult<OpenDepositPlanResponseDTO>
}

class DepositPlansServiceImpl(
    private val client: HttpClient
) : DepositPlansService {
    override suspend fun setDepositPlanVisibility(
        visibile: Boolean,
        walletName: String,
        depositPlanId: Int,
        userName: String,
        clientToken: String
    ): ApiResult<SetAccountVisibilityResponseDTO> = client.safePost<SetAccountVisibilityResponseDTO> {
        url {
            path("account_visibility")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("client_token", clientToken)
                        append("visible", if (visibile) 1 else 0)
                        append("wallet_name", walletName)
                        append("deposit_plan_id", depositPlanId)
                    }
                )
            )
        }
    }

    override suspend fun transferBetweenPlans(
        fromAccountId: Int,
        toAccountId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): ApiResult<TransferBetweenAccountsResponseDTO> = client.safePost<TransferBetweenAccountsResponseDTO> {
        url {
            path("internal_transfer")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("user_id", userId)
                        append("client_token", clientToken)
                        append("from_account_id", fromAccountId)
                        append("to_account_id", toAccountId)
                        append("amount", amount.toString())
                        append("coin_id", coinId)
                        append("comments", "")
                    }
                )
            )
        }
    }

    override suspend fun openDepositPlan(
        depositPlanId: Int,
        coinId: Int,
        amount: BigNum,
        userName: String,
        userId: Int,
        clientToken: String
    ): ApiResult<OpenDepositPlanResponseDTO> = client.safePost<OpenDepositPlanResponseDTO> {
        url {
            path("create_deposit_plan")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("username", userName)
                        append("user_id", userId)
                        append("client_token", clientToken)
                        append("plan_id", depositPlanId)
                        append("amount", amount.toString())
                        append("coin_id", coinId)
                    }
                )
            )
        }
    }
}

private var depositPlansService: DepositPlansService? = null

internal fun getDepositPlansService(): DepositPlansService {
    if (depositPlansService == null) {
        depositPlansService = DepositPlansServiceImpl(httpClient)
    }

    return depositPlansService!!
}