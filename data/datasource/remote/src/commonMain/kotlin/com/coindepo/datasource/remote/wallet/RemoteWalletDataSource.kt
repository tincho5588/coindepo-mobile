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

package com.coindepo.datasource.remote.wallet

import com.coindepo.datasource.contracts.wallet.RemoteWalletDataSource
import com.coindepo.datasource.remote.utils.asResult
import com.coindepo.datasource.remote.wallet.data.asWalletData
import com.coindepo.datasource.remote.wallet.service.WalletDataService
import com.coindepo.datasource.remote.wallet.service.getWalletDataService
import com.coindepo.domain.entities.wallet.Wallet

class RemoteWalletDataSourceImpl internal constructor(
    private val walletService: WalletDataService
): RemoteWalletDataSource {

    constructor() : this(getWalletDataService())
    override suspend fun getWalletData(
        coin: String,
        userName: String,
        clientToken: String
    ): Result<Wallet> = walletService.getWalletData(coin, userName, clientToken).asResult {
        this.asWalletData
    }
}