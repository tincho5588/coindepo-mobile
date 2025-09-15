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

@file:UseSerializers(BigNumSerializer::class)

package com.coindepo.datasource.remote.stats.data

import com.coindepo.datasource.remote.BigNumSerializer
import korlibs.bignumber.BigNum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class CoinDTO(
    @SerialName("coin_id") val coinId: Int,
    @SerialName("coin_name") val coinName: String,
    @SerialName("logo_name") val logoName: String,
    val production: Int,
    val precision: Int,
    @SerialName("limit_24h") val limit24h: BigNum,
    @SerialName("explorer_url") val explorerUrl: String,
    @SerialName("withdraw_limit") val withdrawLimit: BigNum,
    @SerialName("binance_ticker") val binanceTicker: String,
    @SerialName("desc") val description: String,
    @SerialName("desc_ticker") val descriptionTicker: String,
    val order: Int,
    @SerialName("stable_coin") val stableCoin: Int,
    val network: String,
    @SerialName("dep_name") val depName: String,
    @SerialName("wth_name") val wthName: String,
    @SerialName("is_active") val isActive: Int,
    @SerialName("is_token") val isToken: Int,
    val apy: BigNum,
    val apr: BigNum,
    val fee: BigNum,
    @SerialName("private_price") val privatePrice: BigNum,
    @SerialName("listing_price") val listingPrice: BigNum,
    @SerialName("private_discount") val privateDiscount: BigNum
)

const val COIN_LOGO_URL = "https://static.coindepo.com/icons/"

val CoinDTO.logoUrl: String
    get() = "$COIN_LOGO_URL$logoName.svg"