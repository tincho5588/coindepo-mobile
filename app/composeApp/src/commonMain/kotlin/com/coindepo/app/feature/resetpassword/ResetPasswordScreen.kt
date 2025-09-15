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

package com.coindepo.app.feature.resetpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.check_email_note_line_1
import coindepo.app.composeapp.generated.resources.check_email_note_line_2
import coindepo.app.composeapp.generated.resources.check_your_email
import coindepo.app.composeapp.generated.resources.check_your_email_description_line_1
import coindepo.app.composeapp.generated.resources.email
import coindepo.app.composeapp.generated.resources.invalid_email_error
import coindepo.app.composeapp.generated.resources.resend_email
import coindepo.app.composeapp.generated.resources.reset_password
import coindepo.app.composeapp.generated.resources.reset_your_password
import coindepo.app.composeapp.generated.resources.reset_your_password_description
import coindepo.app.composeapp.generated.resources.too_soon
import coindepo.app.composeapp.generated.resources.too_soon_description
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.ErrorDialog
import com.coindepo.app.feature.common.LoadingSpinner
import com.coindepo.app.feature.common.NoNetworkErrorDialog
import com.coindepo.app.feature.common.UnknownErrorDialog
import com.coindepo.app.feature.login.CoinDepoHeader
import com.coindepo.app.feature.login.maskedEmailAddress
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.app.ui.theme.Contrast
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ResetPasswordScreen(
    resetPasswordViewModel: ResetPasswordViewModel
) {
    val isLoading = resetPasswordViewModel.isLoading.collectAsState().value

    if (isLoading) {
        LoadingSpinner()
    } else {
        ResetPasswordScreenContent(
            resetPasswordViewModel.resetPasswordScreenState.collectAsState().value,
            resetPasswordViewModel.error.collectAsState().value,
            resetPasswordViewModel::cleanError
        ) {
            resetPasswordViewModel.requestPasswordReset(it)
        }
    }
}

@Composable
fun ResetPasswordScreenContent(
    resetPasswordScreenState: ResetPasswordScreenState,
    error: ResetPasswordError?,
    onCleanError: () -> Unit,
    onResetPassword: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        when (resetPasswordScreenState) {
            is InitialResetPasswordScreenState -> RequestPasswordResetCard(onResetPassword)
            is PasswordResetRequestedScreenState -> ResetPasswordRequestedCard(resetPasswordScreenState.email, onResetPassword)
        }

        when(error) {
            ResetPasswordError.TOO_FREQUENT_REQUESTS -> ErrorDialog(
                title = stringResource(Res.string.too_soon),
                description = stringResource(Res.string.too_soon_description)
            ) { onCleanError() }
            ResetPasswordError.NO_NETWORK -> NoNetworkErrorDialog { onCleanError() }
            ResetPasswordError.UNKNOWN_ERROR -> UnknownErrorDialog { onCleanError() }
            null -> {}
        }
    }
}

@Composable
fun RequestPasswordResetCard(
    onResetPassword: (String) -> Unit
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        val email = remember { mutableStateOf("") }
        val isEmailError = remember { mutableStateOf(false) }

        CoinDepoHeader()

        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.reset_your_password), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.reset_your_password_description), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            value = email.value,
            onValueChange = {
                if (isEmailError.value) isEmailError.value = false
                email.value = it
            },
            isError = isEmailError.value,
            supportingText = {
                if (isEmailError.value) {
                    Text(stringResource(Res.string.invalid_email_error))
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = { Text(stringResource(Res.string.email)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            onClick = {
                if (email.value.contains("@") && email.value.contains(".")) {
                    onResetPassword(email.value)
                } else {
                    isEmailError.value = true
                }
            },
            content = { Text(stringResource(Res.string.reset_password)) },
            enabled = email.value.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ResetPasswordRequestedCard(
    email: String,
    onResetPassword: (String) -> Unit
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        CoinDepoHeader()

        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.check_your_email), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.check_your_email_description_line_1), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = email.maskedEmailAddress, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center) // TODO: mask email here
        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.check_your_email_description_line_1), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            onClick = { onResetPassword(email) },
            content = { Text(stringResource(Res.string.resend_email)) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.check_email_note_line_1), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFa19ead)))
        Text(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), text = stringResource(Res.string.check_email_note_line_2), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFa19ead)))

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun InitialResetPasswordScreen() {
    CoinDepoTheme(
        contrast = Contrast.MEDIUM
    ) {
        ResetPasswordScreenContent(
            InitialResetPasswordScreenState(), null, {}
        ) {}
    }
}

@Preview
@Composable
fun InitialResetPasswordScreenNoNetwork() {
    CoinDepoTheme(
        contrast = Contrast.MEDIUM
    ) {
        val error = remember { mutableStateOf<ResetPasswordError?>(ResetPasswordError.NO_NETWORK) }

        ResetPasswordScreenContent(
            InitialResetPasswordScreenState(), error.value, { error.value = null }
        ) {}
    }
}

@Preview
@Composable
fun ResetPasswordRequestedScreen() {
    CoinDepoTheme(
        contrast = Contrast.MEDIUM
    ) {
        ResetPasswordScreenContent(
            PasswordResetRequestedScreenState("some@mail.com"), null, {}
        ) {}
    }
}