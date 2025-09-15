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

package com.coindepo.datasource.local.usersession

import com.coindepo.datasource.contracts.login.UserSessionDataSource
import com.coindepo.datasource.local.database.CoinDepoDataBase
import com.coindepo.domain.entities.login.UserSession
import kotlinx.coroutines.flow.Flow

class UserSessionSourceImpl(
    private val coinDepoDataBase: CoinDepoDataBase
) : UserSessionDataSource {
    override val userSessionFlow: Flow<UserSession?>
        get() = coinDepoDataBase.userSessionFlow

    override suspend fun saveUserSession(userSession: UserSession) {
        coinDepoDataBase.saveUserSession(userSession)
    }

    override suspend fun getUserSession(): UserSession? =
        coinDepoDataBase.getUserSession()

    override suspend fun deleteAll() {
        coinDepoDataBase.nuke()
    }
}