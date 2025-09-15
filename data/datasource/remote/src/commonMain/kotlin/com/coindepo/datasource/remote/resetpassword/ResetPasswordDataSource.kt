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

package com.coindepo.datasource.remote.resetpassword

import com.coindepo.datasource.contracts.passwordreset.ResetPasswordDataSource
import com.coindepo.datasource.remote.resetpassword.service.ResetPasswordService
import com.coindepo.datasource.remote.resetpassword.service.getResetPasswordService
import com.coindepo.datasource.remote.utils.asResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ResetPasswordDataSourceImpl internal constructor(
    private val resetPasswordService: ResetPasswordService
) : ResetPasswordDataSource {

    constructor() : this(getResetPasswordService())

    override suspend fun requestPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        resetPasswordService.requestPasswordReset(email).asResult { }
    }
}