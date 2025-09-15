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
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.localeWithLocaleIdentifier

actual fun formatCurrency(amount: BigNum, currency: Currency, maskBalance: Boolean): String {
    val currencyFormatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        locale = NSLocale.localeWithLocaleIdentifier(
            when(currency) {
                Currency.USD -> "en_US"
                Currency.EUR -> "cy_GB"
                Currency.GBP -> "es_ES"
            }
        )
    }

    return if (maskBalance) {
        currencyFormatter.stringFromNumber(NSDecimalNumber(amount.toString()))!!.replace(maskingRegex, "•")
    } else {
        currencyFormatter.stringFromNumber(NSDecimalNumber(amount.toString()))!!
    }
}

actual fun formatCrypto(amount: BigNum, coinName: String, precision: Int, maskBalance: Boolean): String {
    val currencyFormatter = NSNumberFormatter().apply {
        locale = NSLocale.localeWithLocaleIdentifier("en_US")
        minimumFractionDigits = precision.toULong()
        maximumFractionDigits = precision.toULong()
    }

    return if (maskBalance) {
        (currencyFormatter.stringFromNumber(NSDecimalNumber(amount.toString()))?.replace(currencyFormatter.currencySymbol, "")!!).replace(maskingRegex, "•") + " $coinName"
    } else {
        (currencyFormatter.stringFromNumber(NSDecimalNumber(amount.toString()))?.replace(currencyFormatter.currencySymbol, "")!!) + " $coinName"
    }
}