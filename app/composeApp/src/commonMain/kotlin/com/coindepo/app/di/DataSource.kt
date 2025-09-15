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

import com.coindepo.datasource.contracts.depositplans.RemoteDepositPlansDataSource
import com.coindepo.datasource.contracts.login.LoginDataSource
import com.coindepo.datasource.contracts.login.UserSessionDataSource
import com.coindepo.datasource.contracts.passwordreset.ResetPasswordDataSource
import com.coindepo.datasource.contracts.stats.LocalAccountStatsDataSource
import com.coindepo.datasource.contracts.stats.RemoteAccountStatsDataSource
import com.coindepo.datasource.contracts.transactions.LocalTransactionsDataSource
import com.coindepo.datasource.contracts.transactions.RemoteTransactionsDataSource
import com.coindepo.datasource.contracts.userdetails.LocalUserDetailsDataSource
import com.coindepo.datasource.contracts.userdetails.RemoteUserDetailsDataSource
import com.coindepo.datasource.contracts.wallet.RemoteWalletDataSource
import com.coindepo.datasource.contracts.withdraw.RemoteWithdrawDataSource
import com.coindepo.datasource.local.Transactions.LocalTransactionsDataSourceImpl
import com.coindepo.datasource.local.stats.LocalAccountStatsDataSourceImpl
import com.coindepo.datasource.local.userdetails.LocalUserDetailsDataSourceImpl
import com.coindepo.datasource.local.usersession.UserSessionSourceImpl
import com.coindepo.datasource.remote.depositplans.RemoteDepositPlansDataSourceImpl
import com.coindepo.datasource.remote.login.LoginDataSourceImpl
import com.coindepo.datasource.remote.resetpassword.ResetPasswordDataSourceImpl
import com.coindepo.datasource.remote.stats.RemoteAccountStatsDataSourceImpl
import com.coindepo.datasource.remote.transactions.RemoteTransactionsDataSourceImpl
import com.coindepo.datasource.remote.userdetails.RemoteUserDetailsDataSourceImpl
import com.coindepo.datasource.remote.wallet.RemoteWalletDataSourceImpl
import com.coindepo.datasource.remote.withdraw.RemoteWithdrawDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

expect val coinDepoDatabaseModule: Module

val dataSourcesModule = module {
    single<LoginDataSource> { LoginDataSourceImpl() }
    single<UserSessionDataSource> { UserSessionSourceImpl(get()) }
    single<ResetPasswordDataSource> { ResetPasswordDataSourceImpl() }
    single<LocalUserDetailsDataSource> { LocalUserDetailsDataSourceImpl(get()) }
    single<RemoteUserDetailsDataSource> { RemoteUserDetailsDataSourceImpl() }
    single<LocalAccountStatsDataSource> { LocalAccountStatsDataSourceImpl(get()) }
    single<RemoteAccountStatsDataSource> { RemoteAccountStatsDataSourceImpl() }
    single<RemoteWalletDataSource> { RemoteWalletDataSourceImpl() }
    single<RemoteDepositPlansDataSource> { RemoteDepositPlansDataSourceImpl() }
    single<RemoteWithdrawDataSource> { RemoteWithdrawDataSourceImpl() }
    single<LocalTransactionsDataSource> { LocalTransactionsDataSourceImpl(get()) }
    single<RemoteTransactionsDataSource> { RemoteTransactionsDataSourceImpl() }
}