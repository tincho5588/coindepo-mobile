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

package com.coindepo.repository.implementation.userdetails

import com.coindepo.datasource.contracts.userdetails.LocalUserDetailsDataSource
import com.coindepo.datasource.contracts.userdetails.RemoteUserDetailsDataSource
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.userdetails.UserDetails
import com.coindepo.repository.contracts.userdetails.UserDetailsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class UserDetailsRepositoryImpl(
    private val remoteUserDetailsDataSource: RemoteUserDetailsDataSource,
    private val localUserDetailsDataSource: LocalUserDetailsDataSource
) : UserDetailsRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    override val userDetails: StateFlow<UserDetails?>
        get() = localUserDetailsDataSource.userDetails.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override suspend fun fetchUserDetails(
        userId: Int,
        userName: String,
        email: String,
        clientToken: String
    ): OperationResult =
        remoteUserDetailsDataSource.fetchUserDetails(userId, userName, email, clientToken).fold(
            onSuccess = {
                localUserDetailsDataSource.saveUserDetails(it)
                OperationResult.Success(it)
            },
            onFailure = {
                OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
            }
        )
}