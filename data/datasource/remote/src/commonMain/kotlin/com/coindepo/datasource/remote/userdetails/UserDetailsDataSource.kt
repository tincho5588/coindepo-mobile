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

package com.coindepo.datasource.remote.userdetails

import com.coindepo.datasource.contracts.userdetails.RemoteUserDetailsDataSource
import com.coindepo.datasource.remote.userdetails.data.asUserDetails
import com.coindepo.datasource.remote.userdetails.service.UserDetailsService
import com.coindepo.datasource.remote.userdetails.service.getUserDetailsService
import com.coindepo.datasource.remote.utils.asResult
import com.coindepo.domain.entities.userdetails.UserDetails

class RemoteUserDetailsDataSourceImpl internal constructor(
    private val userDetailsService: UserDetailsService
) : RemoteUserDetailsDataSource {

    constructor() : this(getUserDetailsService())

    override suspend fun fetchUserDetails(
        userId: Int,
        userName: String,
        email: String,
        clientToken: String
    ): Result<UserDetails> =
        userDetailsService.getUserDetails(userId, userName, email, clientToken).asResult {
            this.asUserDetails
        }
}