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

package com.coindepo.datasource.local.userdetails

import com.coindepo.datasource.contracts.userdetails.LocalUserDetailsDataSource
import com.coindepo.datasource.local.database.CoinDepoDataBase
import com.coindepo.domain.entities.userdetails.UserDetails
import kotlinx.coroutines.flow.Flow

class LocalUserDetailsDataSourceImpl(
    private val coinDepoDataBase: CoinDepoDataBase
) : LocalUserDetailsDataSource {

    override val userDetails: Flow<UserDetails?>
        get() = coinDepoDataBase.userDetailsFlow

    override suspend fun saveUserDetails(userDetails: UserDetails) {
        coinDepoDataBase.saveUserDetails(userDetails)
    }
}