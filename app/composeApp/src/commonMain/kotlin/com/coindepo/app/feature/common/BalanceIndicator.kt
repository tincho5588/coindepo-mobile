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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.coindepo.domain.entities.currency.Currency
import korlibs.bignumber.BigNum

@Composable
fun BalanceIndicator(
    title: String,
    balance: BigNum,
    balanceTextColor: Color? = null,
    currency: Currency,
    info: String,
    maskBalance: Boolean,
    big: Boolean = false
) {
    val titleTextStyle = if (big) {
        MaterialTheme.typography.titleLarge
    } else {
        MaterialTheme.typography.titleMedium
    }
    val balanceTextStyle = if (big) {
        MaterialTheme.typography.headlineLarge
    } else {
        MaterialTheme.typography.headlineMedium
    }

    Column(
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = titleTextStyle.copy(Color(0xFFa19ead), fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            InformationTooltip(info)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = formatCurrency(balance, currency, maskBalance),
            style = balanceTextStyle.copy(fontWeight = FontWeight.Bold)
                .copy(color = balanceTextColor ?: TextStyle.Default.color)
        )
        if (big) Spacer(Modifier.height(4.dp))
    }
}