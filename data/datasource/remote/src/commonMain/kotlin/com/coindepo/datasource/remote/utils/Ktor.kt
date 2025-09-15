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

package com.coindepo.datasource.remote.utils

import com.coindepo.datasource.remote.stats.data.BaseResponseDTO
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.ReloginRequiredException
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

sealed interface ApiResult<T : BaseResponseDTO> {
    val httpResponse: HttpResponse?
}

data class SuccessApiResult<T: BaseResponseDTO>(
    override val httpResponse: HttpResponse,
    val body: T
) : ApiResult<T>

data class ErrorApiResult<T: BaseResponseDTO>(
    override val httpResponse: HttpResponse?,
    val maybeBody: T? = null
) : ApiResult<T>

data class NoNetworkApiResult<T: BaseResponseDTO>(
    override val httpResponse: HttpResponse? = null
) : ApiResult<T>

suspend inline fun <reified T> HttpResponse.tryParseBody(): T? = try {
    body()
} catch (e: NoTransformationFoundException) {
    null
}

expect suspend inline fun <reified T: BaseResponseDTO> HttpClient.safePost(
    block: HttpRequestBuilder.() -> Unit
): ApiResult<T>

@Throws(CoinDepoException::class)
fun <T: BaseResponseDTO> ApiResult<T>.handle(): T =
    when (this) {
        is ErrorApiResult<*> -> throw OtherErrorException()
        is NoNetworkApiResult<*> -> throw NoNetworkException()
        is SuccessApiResult<T> -> {
            val response = body

            if (response.status != "error") {
                body
            } else {
                throw body.errorCode?.errorCodeAsException ?: body.code?.errorCodeAsException ?: OtherErrorException()
            }
        }
    }

fun <T: BaseResponseDTO, Q> ApiResult<T>.asResult(
    bodyTransformation: T.() -> Q
) = try {
    Result.success(this.handle().bodyTransformation())
} catch (e: CoinDepoException) {
    Result.failure(e)
}

val String.errorCodeAsException: CoinDepoException
    get() = when (this) {
        "RELOGIN_REQUIRED" -> ReloginRequiredException()
        else -> OtherErrorException()
    }