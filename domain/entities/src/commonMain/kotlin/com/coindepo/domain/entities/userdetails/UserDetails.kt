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

package com.coindepo.domain.entities.userdetails

import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val userId: Int,
    val status: String, // TODO: use an enum here
    val totp: Boolean,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val phone: String? = null,
    val country: String? = null,
    val address1: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val documentCrc: String? = null,
    val documentCountry: String? = null,
    val documentType: String? = null,
    val documentNumber: String? = null,
    val documentExpiryDate: String? = null,
    val documentPhoto1: String? = null,
    val documentPhoto2: String? = null,
    val userType: String,
    val sourceOfFunds: String? = null,
    val language: String,
    val emailPromo: String, // TODO: use an boolean here
    val emailActivities: String, // TODO: use an boolean here
    val emailTrx: String, // TODO: use an boolean here
    val emailNews: String, // TODO: use an boolean here
    val emailSystem: String, // TODO: use an boolean here
    val avatar: List<Byte>,
    val creditLineActive: String,
    val isAffiliate: String,  // TODO: use an boolean here
    val regCountry: String,
    val hasPassword: String,  // TODO: use an boolean here
    val ipLock: String,  // TODO: use an boolean here
    val referralCodes: List<ReferralCodes> = emptyList(),
    val affiliateCodes: List<String> = emptyList()
)

@Serializable
data class ReferralCodes(
    val id: Int,
    val referral: String,
    val type: String
)