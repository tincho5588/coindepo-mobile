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

package com.coindepo.app.feature.common

import com.coindepo.domain.entities.currency.Currency
import korlibs.bignumber.BigNum
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency as JavaCurrency

actual fun formatCurrency(
    amount: BigNum,
    currency: Currency,
    maskBalance: Boolean
): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2
    format.currency = JavaCurrency.getInstance(currency.name)

    return if (maskBalance) {
        format.format(BigDecimal(amount.toString())).replace(format.currency!!.symbol, "${format.currency!!.symbol} ").replace(maskingRegex, "•")
    } else {
        format.format(BigDecimal(amount.toString())).replace(format.currency!!.symbol, "${format.currency!!.symbol} ")
    }
}

actual fun formatCrypto(
    amount: BigNum,
    coinName: String,
    precision: Int,
    maskBalance: Boolean
): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.minimumFractionDigits = precision
    format.maximumFractionDigits = precision
    format.currency = JavaCurrency.getInstance("USD")

    return if (maskBalance) {
        format.format(BigDecimal(amount.toString())).replace(format.currency!!.symbol, "").replace(maskingRegex, "•") + " $coinName"
    } else {
        format.format(BigDecimal(amount.toString())).replace(format.currency!!.symbol, "") + " $coinName"
    }
}