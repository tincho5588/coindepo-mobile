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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.no_connectivity
import coindepo.app.composeapp.generated.resources.no_connectivity_description
import coindepo.app.composeapp.generated.resources.ok
import coindepo.app.composeapp.generated.resources.unknown_error
import coindepo.app.composeapp.generated.resources.unknown_error_description
import com.coindepo.app.ui.theme.CoinDepoTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ErrorDialog(
    title: String,
    description: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest,
        icon = { Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp)) },
        title = { Text(title) },
        text = { Text(modifier = Modifier.fillMaxWidth(), text = description, textAlign = TextAlign.Center) },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.ok))
            }
        }
    )
}

@Composable
fun NoNetworkErrorDialog(
    onDismissRequest: () -> Unit
) {
    ErrorDialog(
        stringResource(Res.string.no_connectivity),
        stringResource(Res.string.no_connectivity_description),
        onDismissRequest
    )
}

@Composable
fun UnknownErrorDialog(
    onDismissRequest: () -> Unit
) {
    ErrorDialog(
        stringResource(Res.string.unknown_error),
        stringResource(Res.string.unknown_error_description),
        onDismissRequest
    )
}

@Preview
@Composable
fun ErrorDialogPreview() {
    CoinDepoTheme {
        ErrorDialog(
            "Some title",
            "Some description",
            {}
        )
    }
}