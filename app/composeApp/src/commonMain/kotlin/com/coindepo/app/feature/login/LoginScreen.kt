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

package com.coindepo.app.feature.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.cancel
import coindepo.app.composeapp.generated.resources.click_here
import coindepo.app.composeapp.generated.resources.code_sent
import coindepo.app.composeapp.generated.resources.coin_depo
import coindepo.app.composeapp.generated.resources.dont_have_account
import coindepo.app.composeapp.generated.resources.email
import coindepo.app.composeapp.generated.resources.email_verification_code
import coindepo.app.composeapp.generated.resources.forgot_password
import coindepo.app.composeapp.generated.resources.hide_password
import coindepo.app.composeapp.generated.resources.i_understand
import coindepo.app.composeapp.generated.resources.ic_coin_depo_logo_36
import coindepo.app.composeapp.generated.resources.invalid_2fa
import coindepo.app.composeapp.generated.resources.invalid_email_verification_code_error
import coindepo.app.composeapp.generated.resources.invalid_email_verification_limit_reached_error
import coindepo.app.composeapp.generated.resources.log_in
import coindepo.app.composeapp.generated.resources.password
import coindepo.app.composeapp.generated.resources.resend_code
import coindepo.app.composeapp.generated.resources.security_verification
import coindepo.app.composeapp.generated.resources.security_verification_description
import coindepo.app.composeapp.generated.resources.show_password
import coindepo.app.composeapp.generated.resources.sign_up
import coindepo.app.composeapp.generated.resources.submit
import coindepo.app.composeapp.generated.resources.twofa
import coindepo.app.composeapp.generated.resources.verification_code_note
import coindepo.app.composeapp.generated.resources.verification_code_note_dialog_text_1
import coindepo.app.composeapp.generated.resources.verification_code_note_dialog_text_2
import coindepo.app.composeapp.generated.resources.verification_code_note_dialog_title
import coindepo.app.composeapp.generated.resources.verify_your_identity
import coindepo.app.composeapp.generated.resources.verify_your_identity_description
import coindepo.app.composeapp.generated.resources.wrong_email_or_password_error
import com.coindepo.app.browser.openUrlInBrowser
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.InformationTooltip
import com.coindepo.app.feature.common.LoadingSpinner
import com.coindepo.app.feature.common.NoNetworkErrorDialog
import com.coindepo.app.feature.common.UnknownErrorDialog
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.login.BlockedLoginState
import com.coindepo.domain.entities.login.EmailVerificationRequiredLoginState
import com.coindepo.domain.entities.login.LoggedInLoginState
import com.coindepo.domain.entities.login.LoggedOutLoginState
import com.coindepo.domain.entities.login.LoginState
import com.coindepo.domain.entities.login.OtpRequiredLoginState
import com.coindepo.domain.entities.login.UserSession
import com.coindepo.domain.entities.login.WrongCredentialsLoginState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onResetPassword: () -> Unit,
    onLoginDone: (UserSession) -> Unit
) {
    val isLoading = loginViewModel.isLoading.collectAsState().value
    val loginScreenState = loginViewModel.loginState.collectAsState().value

    if (isLoading) {
        LoadingSpinner()
    } else {
        when (loginScreenState) {
            is LoggedInLoginState -> onLoginDone(loginScreenState.userSession)
            else -> {
                LoginScreenContent(
                    loginState = loginScreenState,
                    error = loginViewModel.loginError.collectAsState().value,
                    resendVerificationCodeState = loginViewModel.resendVerificationCodeState.collectAsState().value,
                    onCleanError = loginViewModel::cleanError,
                    onResetPassword = onResetPassword,
                    onEmailLogin = { email, password, emailVerificationCode, twoFA ->
                        loginViewModel.performEmailLogin(
                            email,
                            password,
                            emailVerificationCode,
                            twoFA
                        )
                    },
                    onResendVerificationCode = { email ->
                        loginViewModel.resendVerificationCode(email)
                    },
                    onCanceled = {
                        loginViewModel.logOut()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    loginState: LoginState,
    error: LoginError?,
    resendVerificationCodeState: ResendVerificationCodeState,
    onCleanError: () -> Unit,
    onResetPassword: () -> Unit,
    onEmailLogin: (String, String, String?, String?) -> Unit,
    onResendVerificationCode: (String) -> Unit,
    onCanceled: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        when (loginState) {
            is LoggedOutLoginState -> EmailAndPasswordInputCard(
                isError = false,
                onResetPassword = onResetPassword
            ) { email, password -> onEmailLogin(email, password, null, null) }

            is WrongCredentialsLoginState -> EmailAndPasswordInputCard(
                email = loginState.email,
                password = loginState.password,
                isError = true,
                onResetPassword = onResetPassword
            ) { email, password -> onEmailLogin(email, password, null, null) }

            is EmailVerificationRequiredLoginState -> EmailVerificationCodeInputCard(
                loginState.email,
                loginState.invalidCode,
                false,
                resendVerificationCodeState,
                onCanceled,
                { onEmailLogin(loginState.email, loginState.password, it, null) },
                { onResendVerificationCode(loginState.email) }
            )

            is OtpRequiredLoginState -> TwoFaCodeInputCard(
                loginState.invalidCode,
                onCanceled
            ) {
                onEmailLogin(
                    loginState.email,
                    loginState.password,
                    loginState.emailVerificationCode!!,
                    it
                )
            }

            is BlockedLoginState -> EmailVerificationCodeInputCard(
                loginState.email,
                false,
                true,
                resendVerificationCodeState,
                onCanceled,
                { onEmailLogin(loginState.email, loginState.password, it, null) },
                {}
            )
            is LoggedInLoginState -> throw IllegalArgumentException()
        }

        when(error) {
            LoginError.NO_NETWORK -> NoNetworkErrorDialog { onCleanError() }
            LoginError.UNKNOWN_ERROR -> UnknownErrorDialog { onCleanError() }
            null -> {}
        }
    }
}

@Composable
fun EmailAndPasswordInputCard(
    email: String? = null,
    password: String? = null,
    isError: Boolean,
    onResetPassword: () -> Unit,
    onEmailLoginClicked: (String, String) -> Unit
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        val email = remember { mutableStateOf(email ?: "") }
        val password = remember { mutableStateOf(password ?: "") }
        val passwordVisible = remember { mutableStateOf(false) }

        CoinDepoHeader()

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = stringResource(Res.string.log_in),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            value = email.value,
            onValueChange = {
                email.value = it
            },
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = { Text(stringResource(Res.string.email)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            value = password.value,
            onValueChange = {
                password.value = it
            },
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(stringResource(Res.string.wrong_email_or_password_error))
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = { Text(stringResource(Res.string.password)) },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                val description = stringResource(if (passwordVisible.value) Res.string.hide_password else Res.string.show_password)

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        TextButton(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            onClick = onResetPassword,
        ) {
            Text(text = stringResource(Res.string.forgot_password))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            onClick = { onEmailLoginClicked(email.value, password.value) },
            content = { Text(stringResource(Res.string.log_in)) },
            enabled = email.value.isNotEmpty() && password.value.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(Res.string.dont_have_account), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            TextButton(
                onClick = { openUrlInBrowser("https://app.coindepo.com/auth/sign-up?ref=R-XFn89cU3") }
            ) {
                Text(stringResource(Res.string.sign_up), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EmailVerificationCodeInputCard(
    email: String,
    isInvalidCode: Boolean,
    isBlocked: Boolean,
    resendVerificationCodeState: ResendVerificationCodeState,
    onEmailVerificationCancelClicked: () -> Unit,
    onEmailVerificationDoneClicked: (String) -> Unit,
    onResendVerificationCode: () -> Unit
) {
    val showCodeNotesDialog = remember { mutableStateOf(false) }

    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        val emailVerificationCode = remember { mutableStateOf("") }

        CoinDepoHeader()

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = stringResource(Res.string.security_verification),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = stringResource(Res.string.security_verification_description, email.maskedEmailAddress),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isBlocked) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.clickable(
                            enabled = resendVerificationCodeState.enabled,
                            onClick = {
                                onResendVerificationCode()
                            }
                        ),
                    text = if (resendVerificationCodeState.enabled) stringResource(Res.string.resend_code) else stringResource(Res.string.code_sent),
                    style = MaterialTheme.typography.labelLarge.copy(color = if (resendVerificationCodeState.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                )
                if (!resendVerificationCodeState.enabled) {
                    Spacer(Modifier.width(8.dp))
                    InformationTooltip(
                        "Haven't received code? Request new code in ${resendVerificationCodeState.timeRemaining} seconds. The code will expire after 30 minutes."
                    )
                }
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            value = emailVerificationCode.value,
            onValueChange = {
                if (it.length <= 8) emailVerificationCode.value = it
            },
            isError = isInvalidCode || isBlocked,
            supportingText = {
                if (isInvalidCode) {
                    Text(stringResource(Res.string.invalid_email_verification_code_error))
                } else if (isBlocked) {
                    Text(stringResource(Res.string.invalid_email_verification_limit_reached_error))
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = { Text(stringResource(Res.string.email_verification_code)) },
            enabled = !isBlocked
        )
        if (!isBlocked) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.verification_code_note),
                    style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.primary)
                )
                Row(
                    Modifier.clickable(
                        enabled = true,
                        onClick = {
                            showCodeNotesDialog.value = true
                        }
                    )
                ) {
                    Text(
                        text = stringResource(Res.string.click_here),
                        style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                        textDecoration = TextDecoration.Underline
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Filled.Info,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = ""
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                onClick = { onEmailVerificationCancelClicked() },
                content = { Text(stringResource(Res.string.cancel)) },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            )

            Button(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                onClick = { onEmailVerificationDoneClicked(emailVerificationCode.value) },
                content = { Text(stringResource(Res.string.submit)) },
                enabled = !isBlocked && emailVerificationCode.value.length == 8
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showCodeNotesDialog.value) {
        AlertDialog(
            onDismissRequest = { showCodeNotesDialog.value = false },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showCodeNotesDialog.value = false }
                ) {
                    Text(stringResource(Res.string.i_understand))
                }
            },
            title = { Text(stringResource(Res.string.verification_code_note_dialog_title), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(Res.string.verification_code_note_dialog_text_1), textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(Res.string.verification_code_note_dialog_text_2, email.maskedEmailAddress), textAlign = TextAlign.Start
                    )
                }
            }
        )
    }
}

@Composable
fun TwoFaCodeInputCard(
    isInvalidCode: Boolean,
    onTwoFACanceled: () -> Unit,
    onTwFASendClicked: (String) -> Unit
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        val twoFACode = remember { mutableStateOf("") }

        CoinDepoHeader()

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = stringResource(Res.string.verify_your_identity),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            text = stringResource(Res.string.verify_your_identity_description),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            value = twoFACode.value,
            onValueChange = {
                if (it.length <= 6) twoFACode.value = it
            },
            isError = isInvalidCode,
            supportingText = { if (isInvalidCode) Text(stringResource(Res.string.invalid_2fa)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            label = {
                Text(
                    stringResource(Res.string.twofa),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                onClick = { onTwoFACanceled() },
                content = { Text(stringResource(Res.string.cancel)) },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            )

            Button(
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                onClick = { onTwFASendClicked(twoFACode.value) },
                content = { Text(stringResource(Res.string.submit)) },
                enabled = twoFACode.value.length == 6
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CoinDepoHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_coin_depo_logo_36),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Text(
            modifier = Modifier.wrapContentSize().padding(horizontal = 4.dp, vertical = 32.dp),
            text = stringResource(Res.string.coin_depo),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun LoggedOutLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            LoggedOutLoginState,
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

@Preview
@Composable
fun WrongPasswordLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            WrongCredentialsLoginState("some@mail.com", "somepassword"),
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

@Preview
@Composable
fun EmailVerificationRequiredLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            EmailVerificationRequiredLoginState("some@mail.com", "somepassword", false),
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

@Preview
@Composable
fun InvalidCodeEmailVerificationRequiredLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            EmailVerificationRequiredLoginState("some@mail.com", "somepassword", true),
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

@Preview
@Composable
fun OtpRequiredLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            OtpRequiredLoginState("some@mail.com", "somepassword", "somecode", false),
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

@Preview
@Composable
fun InvalidOtpRequiredLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            OtpRequiredLoginState("some@mail.com", "somepassword", "somecode", true),
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

@Preview
@Composable
fun BlockedLoginResultPreview() {
    CoinDepoTheme {
        LoginScreenContent(
            BlockedLoginState("some@mail.com", "somepassword"),
            null,
            ResendVerificationCodeState(true, 0),
            {},
            {},
            { _, _, _, _ -> },
            {},
            {}
        )
    }
}

val emailAddressMaskingRegex = """^([^@]{2})([^@]+)""".toRegex()
val String.maskedEmailAddress: String
    get() = this.replace(emailAddressMaskingRegex) {
            it.groupValues[1] + "*".repeat(it.groupValues[2].length)
        }