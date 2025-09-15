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

package com.coindepo.app.di

import com.coindepo.app.feature.login.LoginViewModel
import com.coindepo.app.feature.mainscreen.AccountStatsViewModel
import com.coindepo.app.feature.mainscreen.deposit.WalletDataViewModel
import com.coindepo.app.feature.mainscreen.transactions.TransactionsViewModel
import com.coindepo.app.feature.mainscreen.transfer.TransferViewModel
import com.coindepo.app.feature.mainscreen.userdetails.UserDetailsViewModel
import com.coindepo.app.feature.mainscreen.withdraw.WithdrawViewModel
import com.coindepo.app.feature.resetpassword.ResetPasswordViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope

@OptIn(KoinExperimentalAPI::class)
val viewModelModule = module {
    viewModelScope {
        viewModelOf(::LoginViewModel)
        viewModelOf(::ResetPasswordViewModel)
        viewModelOf(::UserDetailsViewModel)
        viewModelOf(::AccountStatsViewModel)
        viewModelOf(::WalletDataViewModel)
        viewModelOf(::TransferViewModel)
        viewModelOf(::WithdrawViewModel)
        viewModelOf(::TransactionsViewModel)
    }
}