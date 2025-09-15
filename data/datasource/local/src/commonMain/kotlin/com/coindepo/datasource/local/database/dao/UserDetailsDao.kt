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

package com.coindepo.datasource.local.database.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.coindepo.domain.entities.userdetails.ReferralCodes
import com.coindepo.domain.entities.userdetails.UserDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailsDao {

    @Query("SELECT * FROM user_details_data")
    fun getUserDetailsFlow(): Flow<List<UserDetailsEntity?>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetails(userDetailsEntity: UserDetailsEntity)

    @Query("DELETE FROM user_details_data")
    suspend fun nuke()
}

@Entity(
    tableName = "user_details_data",
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserDetailsEntity(
    @PrimaryKey val userId: Int,
    val status: String,
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
    val emailPromo: String,
    val emailActivities: String,
    val emailTrx: String,
    val emailNews: String,
    val emailSystem: String,
    val avatar: List<Byte>,
    val creditLineActive: String,
    val isAffiliate: String,
    val regCountry: String,
    val hasPassword: String,
    val ipLock: String,
    val referralCodes: List<ReferralCodes> = emptyList(),
    val affiliateCodes: List<String> = emptyList()
)

val UserDetails.asUserDetailsEntity: UserDetailsEntity
    get() = UserDetailsEntity(
        userId = userId,
        status = status,
        totp = totp,
        email = email,
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
        userType = userType,
        sourceOfFunds = sourceOfFunds,
        language = language,
        emailPromo = emailPromo,
        emailActivities = emailActivities,
        emailTrx = emailTrx,
        emailNews = emailNews,
        emailSystem = emailSystem,
        avatar = avatar,
        creditLineActive = creditLineActive,
        isAffiliate = isAffiliate,
        regCountry = regCountry,
        hasPassword = hasPassword,
        ipLock = ipLock,
        referralCodes = referralCodes,
        affiliateCodes = affiliateCodes
    )

val UserDetailsEntity.asUserDetails: UserDetails
    get() = UserDetails(
        userId = userId,
        status = status,
        totp = totp,
        email = email,
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
        userType = userType,
        sourceOfFunds = sourceOfFunds,
        language = language,
        emailPromo = emailPromo,
        emailActivities = emailActivities,
        emailTrx = emailTrx,
        emailNews = emailNews,
        emailSystem = emailSystem,
        avatar = avatar,
        creditLineActive = creditLineActive,
        isAffiliate = isAffiliate,
        regCountry = regCountry,
        hasPassword = hasPassword,
        ipLock = ipLock,
        referralCodes = referralCodes,
        affiliateCodes = affiliateCodes
    )