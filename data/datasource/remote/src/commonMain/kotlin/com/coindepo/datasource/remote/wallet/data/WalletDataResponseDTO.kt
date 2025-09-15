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

package com.coindepo.datasource.remote.wallet.data

import com.coindepo.datasource.remote.BigNumSerializer
import com.coindepo.datasource.remote.stats.data.BaseResponseDTO
import com.coindepo.domain.entities.wallet.Wallet
import korlibs.bignumber.BigNum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WalletDataResponseDTO(
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("coin_id") val coinId: Int? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("balance") val balance: BigNum? = null,
    @SerialName("legacy_address") val legacyAddress: String? = null,
    @SerialName("tag") val tag: String? = null,
    @SerialName("vault_generation") val vaultGeneration: Int? = null,
    @SerialName("is_legacy") val isLegacy: Boolean? = null,
    @SerialName("status") override val status: String? = null,
    @SerialName("code") override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

val WalletDataResponseDTO.asWalletData: Wallet
    get() = Wallet(
        currency = currency ?: "",
        coinId = coinId ?: 0,
        address = address ?: "",
        tag = tag ?: "",
        balance = balance ?: BigNum.ZERO,
        legacyAddress = legacyAddress ?: "",
        vaultGeneration = vaultGeneration ?: 0,
        isLegacy = isLegacy ?: false
    )