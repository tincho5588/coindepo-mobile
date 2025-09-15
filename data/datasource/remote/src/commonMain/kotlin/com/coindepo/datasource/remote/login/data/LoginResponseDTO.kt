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

package com.coindepo.datasource.remote.login.data

import com.coindepo.datasource.remote.stats.data.BaseResponseDTO
import com.coindepo.domain.entities.login.BlockedLoginState
import com.coindepo.domain.entities.login.EmailVerificationRequiredLoginState
import com.coindepo.domain.entities.login.LoggedInLoginState
import com.coindepo.domain.entities.login.LoggedOutLoginState
import com.coindepo.domain.entities.login.LoginState
import com.coindepo.domain.entities.login.OtpRequiredLoginState
import com.coindepo.domain.entities.login.UserSession
import com.coindepo.domain.entities.login.WrongCredentialsLoginState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    override val status: String? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("user_type") val userType: String? = null,
    val token: String? = null,
    val provider: String? = null,
    val username: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

fun LoginResponseDTO.asEmailLoginResult(
    email: String,
    password: String,
    emailVerificationCode: String?
): LoginState = when(code) {
        "WRONG_PASSWORD" -> WrongCredentialsLoginState(email, password)
        "EMAIL_VERIFICATION_CODE_REQUIRED" -> EmailVerificationRequiredLoginState(
            email,
            password,
            false
        )
        "EMAIL_VERIFICATION_CODE_INVALID" -> EmailVerificationRequiredLoginState(
            email,
            password,
            true
        )
        "2FA_CODE_REQUIRED" -> OtpRequiredLoginState(email, password, emailVerificationCode, false)
        "INVALID_2FA_CODE" -> OtpRequiredLoginState(email, password, emailVerificationCode, true)
        "LIMIT_EXCEEDED" -> BlockedLoginState(email, password)
        "LOGIN_OK" -> LoggedInLoginState(email, UserSession(email, username?.takeIf { it.isNotEmpty() } ?: email, userId!!, token!!))
        else -> LoggedOutLoginState
    }