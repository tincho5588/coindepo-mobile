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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropDownMenu(
    modifier: Modifier = Modifier,
    expanded: MutableState<Boolean>,
    enabled: Boolean = true,
    buttonContent: @Composable RowScope.() -> Unit,
    items: @Composable ColumnScope.() -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded.value,
        onExpandedChange = { },
    ) {
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = { if (!expanded.value) expanded.value = true },
            colors = ButtonDefaults.textButtonColors()
                .copy(contentColor = MaterialTheme.colorScheme.onBackground),
            content = buttonContent
        )
        ExposedDropdownMenu(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            content = items
        )
    }
}