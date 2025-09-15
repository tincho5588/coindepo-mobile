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

import io.ktor.serialization.JsonConvertException

val String.asBoolean: Boolean
    get () = when(this) {
        "0" -> false
        "1" -> true
        "true" -> true
        "false" -> false
        else -> throw JsonConvertException(message = "Invalid value for boolean conversion: $this")
    }

val Int.asBoolean: Boolean
    get () = when(this) {
        0 -> false
        1 -> true
        else -> throw JsonConvertException(message = "Invalid value for boolean conversion: $this")
    }