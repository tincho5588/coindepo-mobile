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

package com.coindepo.datasource.remote.withdraw.data

import com.coindepo.datasource.remote.BigNumSerializer
import com.coindepo.datasource.remote.stats.data.BaseResponseDTO
import com.coindepo.domain.entities.withdraw.WithdrawFee
import korlibs.bignumber.BigNum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class WithdrawFeesEstimationResponseDTO(
    val networkFee: BigNum? = null,
    override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

fun WithdrawFeesEstimationResponseDTO.asWithdrawFee(requestId: String): WithdrawFee =
    WithdrawFee(requestId, networkFee)