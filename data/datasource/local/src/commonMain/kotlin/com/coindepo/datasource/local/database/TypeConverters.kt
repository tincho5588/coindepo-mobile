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

package com.coindepo.datasource.local.database

import androidx.room.TypeConverter
import com.coindepo.domain.entities.stats.balance.LoanApr
import com.coindepo.domain.entities.userdetails.ReferralCodes
import korlibs.bignumber.BigNum
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromReferralCodesList(list: List<ReferralCodes>): String =
        Json.encodeToString(list)

    @TypeConverter
    fun toReferralCodesList(json: String): List<ReferralCodes> =
        Json.decodeFromString(json)

    @TypeConverter
    fun fromStringList(list: List<String>): String =
        Json.encodeToString(list)

    @TypeConverter
    fun toStringList(json: String): List<String> =
        Json.decodeFromString(json)

    @TypeConverter
    fun fromBytesList(list: List<Byte>): String =
        Json.encodeToString(list)

    @TypeConverter
    fun toBytesList(json: String): List<Byte> =
        Json.decodeFromString(json)

    @TypeConverter
    fun fromListOfLoanApr(list: List<LoanApr>): String =
        Json.encodeToString(list)

    @TypeConverter
    fun toListOfLoanApr(json: String): List<LoanApr> =
        Json.decodeFromString(json)

    @TypeConverter
    fun fromBigNum(num: BigNum): String =
        num.toString()

    @TypeConverter
    fun toBigNum(num: String): BigNum =
        BigNum(num)
}