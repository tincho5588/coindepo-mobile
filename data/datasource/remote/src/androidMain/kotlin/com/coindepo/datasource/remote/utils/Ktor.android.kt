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
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.http.isSuccess
import okio.IOException

actual suspend inline fun <reified T: BaseResponseDTO> HttpClient.safePost(
    block: HttpRequestBuilder.() -> Unit
): ApiResult<T> {
    return try {
        val result = post(block)

        if (result.status.isSuccess()) {
            when(val body = result.tryParseBody<T>()) {
                null -> ErrorApiResult(result)
                else -> SuccessApiResult(result, body)
            }
        } else {
            ErrorApiResult(result, result.tryParseBody())
        }
    } catch (e: java.net.UnknownHostException) {
        NoNetworkApiResult()
    } catch (e: java.net.SocketTimeoutException) {
        NoNetworkApiResult()
    } catch (e: IOException) {
        ErrorApiResult(null)
    }
}