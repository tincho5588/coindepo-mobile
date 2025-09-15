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

package com.coindepo.datasource.remote.userdetails.data

import com.coindepo.datasource.remote.stats.data.BaseResponseDTO
import com.coindepo.domain.entities.userdetails.ReferralCodes
import com.coindepo.domain.entities.userdetails.UserDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64


@Serializable
data class UserDetailsResponseDTO(
    @SerialName("user_id") val userId: Int? = null,
    val totp: Boolean? = null,
    val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("create_date") val createDate: String? = null,
    @SerialName("last_update") val lastUpdate: String? = null,
    @SerialName("entity_id") val entityId: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val phone: String? = null,
    val country: String? = null,
    val address1: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    @SerialName("document_crc") val documentCrc: String? = null,
    @SerialName("document_country") val documentCountry: String? = null,
    @SerialName("document_type") val documentType: String? = null,
    @SerialName("document_number") val documentNumber: String? = null,
    @SerialName("document_expiry_date") val documentExpiryDate: String? = null,
    @SerialName("document_photo1") val documentPhoto1: String? = null,
    @SerialName("document_photo2") val documentPhoto2: String? = null,
    @SerialName("user_type") val userType: String? = null,
    @SerialName("source_of_funds") val sourceOfFunds: String? = null,
    val language: String? = null,
    @SerialName("email_promo") val emailPromo: String? = null,
    @SerialName("email_activities") val emailActivities: String? = null,
    @SerialName("email_trx") val emailTrx: String? = null,
    @SerialName("email_news") val emailNews: String? = null,
    @SerialName("email_system") val emailSystem: String? = null,
    val avatar: String? = null,
    @SerialName("credit_line_active") val creditLineActive: String? = null,
    @SerialName("click_id") val clickId: String? = null,
    @SerialName("is_affiliate") val isAffiliate: String? = null,
    @SerialName("reg_country") val regCountry: String? = null,
    @SerialName("ga_client_id") val gaClientId: String? = null,
    @SerialName("ga_session_id") val gaSessionId: String? = null,
    @SerialName("registered_via") val registeredVia: String? = null,
    @SerialName("login_type") val loginType: String? = null,
    @SerialName("has_password") val hasPassword: String? = null,
    @SerialName("keitaro_id") val keitaroId: String? = null,
    @SerialName("ip_lock") val ipLock: String? = null,
    @SerialName("referral_codes") val referralCodes: List<ReferralCodesDTO> = emptyList(),
    @SerialName("affiliate_codes") val affiliateCodes: List<String> = emptyList(),
    override val status: String? = null,
    override val code: String? = null,
    @SerialName("error_code") override val errorCode: String? = null
): BaseResponseDTO

@Serializable
data class ReferralCodesDTO(
    val id: Int,
    val referral: String,
    val type: String
)

val UserDetailsResponseDTO.asUserDetails: UserDetails
    get() = UserDetails(
        userId = userId!!,
        status = status!!,
        totp = totp!!,
        email = email!!,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        phone = phone,
        country = country,
        address1 = address1,
        city = city,
        state = state,
        zip = zip,
        documentCrc = documentCrc,
        documentCountry = documentCountry,
        documentType = documentType,
        documentNumber = documentNumber,
        documentExpiryDate = documentExpiryDate,
        documentPhoto1 = documentPhoto1,
        documentPhoto2 = documentPhoto2,
        userType = userType!!,
        sourceOfFunds = sourceOfFunds,
        language = language!!,
        emailPromo = emailPromo!!,
        emailActivities = emailActivities!!,
        emailTrx = emailTrx!!,
        emailNews = emailNews!!,
        emailSystem = emailSystem!!,
        avatar = avatar?.decodeAvatar ?: emptyList(),
        creditLineActive = creditLineActive!!,
        isAffiliate = isAffiliate!!,
        regCountry = regCountry!!,
        hasPassword = hasPassword!!,
        ipLock = ipLock!!,
        referralCodes = referralCodes.map { it.asReferralCodes },
        affiliateCodes = affiliateCodes
    )

val ReferralCodesDTO.asReferralCodes: ReferralCodes
    get() = ReferralCodes(
        id, referral, type
    )

val String.decodeAvatar: List<Byte>
    get() {
        // 1. Decode the Base64 String to a Byte Array
        // Handle potential data URI prefix
        val imageData = if (contains(",")) {
            split(",")[1]
        } else {
            this
        }.trim()

        return Base64.decode(imageData).toList()
    }