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

package com.coindepo.datasource.remote

import co.touchlab.kermit.Severity
import io.ktor.client.HttpClient
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import korlibs.bignumber.BigNum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as UtilsLogger

private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
}

val httpClient: HttpClient = HttpClient {
    expectSuccess = false
    followRedirects = true
    install(ContentNegotiation) {
        json(json, contentType = ContentType.Application.Json)
    }
    install(Logging) {
        level = LogLevel.ALL
        logger = object : UtilsLogger {
            override fun log(message: String) {
                KermitLogger.log(Severity.Debug, "HttpClient", null, message)
            }

        }
    }
    defaultRequest {
        url("https://app.coindepo.com/api/")
    }
    BrowserUserAgent()
    install(HttpTimeout) {
        requestTimeoutMillis = 2.minutes.inWholeMilliseconds
        socketTimeoutMillis = 2.minutes.inWholeMilliseconds
        connectTimeoutMillis = 2.minutes.inWholeMilliseconds
    }
}

object BigNumSerializer: KSerializer<BigNum> {
    override fun deserialize(decoder: Decoder): BigNum {
        return BigNum(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: BigNum) {
        encoder.encodeString(value.toString())
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BigNum", PrimitiveKind.STRING)
}