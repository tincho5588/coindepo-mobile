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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WarningBanner(
    expanded: Boolean,
    title: String,
    description: String?,
    warningBannerColor: WarningBannerColor
) {
    val expanded = remember { mutableStateOf(expanded) }

    Column(
        modifier = Modifier.fillMaxWidth().background(
            when (warningBannerColor) {
                WarningBannerColor.BLUE -> MaterialTheme.colorScheme.secondaryContainer
                WarningBannerColor.RED -> MaterialTheme.colorScheme.errorContainer
            },
            RoundedCornerShape(16.dp)
        ).padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(64.dp).padding(8.dp),
                imageVector = Icons.Filled.Error,
                contentDescription = "",
                tint = when (warningBannerColor) {
                    WarningBannerColor.BLUE -> MaterialTheme.colorScheme.primary
                    WarningBannerColor.RED -> MaterialTheme.colorScheme.error

                }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (description != null) FontWeight.Bold else FontWeight.Normal,
                    color = when (warningBannerColor) {
                        WarningBannerColor.BLUE -> Color.Unspecified
                        WarningBannerColor.RED -> MaterialTheme.colorScheme.error
                    }
                ),
                modifier = Modifier.fillMaxSize().weight(1f)
            )
            if (description != null) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                        .clip(CircleShape)
                        .clickable(enabled = true, onClick = { expanded.value = !expanded.value })
                )
            }
        }
        if (description != null) {
            AnimatedVisibility(
                expanded.value
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(start = 72.dp),
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = when (warningBannerColor) {
                            WarningBannerColor.BLUE -> Color.Unspecified
                            WarningBannerColor.RED -> MaterialTheme.colorScheme.error
                        }
                    )
                )
            }
        }
    }
}

enum class WarningBannerColor {
    BLUE,
    RED
}