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

import com.coindepo.repository.contracts.depositplans.DepositPlansRepository
import com.coindepo.repository.contracts.login.LoginRepository
import com.coindepo.repository.contracts.resetpassword.ResetPasswordRepository
import com.coindepo.repository.contracts.stats.AccountStatsRepository
import com.coindepo.repository.contracts.transactions.TransactionsRepository
import com.coindepo.repository.contracts.userdetails.UserDetailsRepository
import com.coindepo.repository.contracts.wallet.WalletDataRepository
import com.coindepo.repository.contracts.withdraw.WithdrawRepository
import com.coindepo.repository.implementation.depositplans.DepositPlansRepositoryImpl
import com.coindepo.repository.implementation.login.LoginRepositoryImpl
import com.coindepo.repository.implementation.resetpassword.ResetPasswordRepositoryImpl
import com.coindepo.repository.implementation.stats.AccountStatsRepositoryImpl
import com.coindepo.repository.implementation.transactions.TransactionsRepositoryImpl
import com.coindepo.repository.implementation.userdetails.UserDetailsRepositoryImpl
import com.coindepo.repository.implementation.wallet.WalletDataRepositoryImpl
import com.coindepo.repository.implementation.withdraw.WithdrawRepositoryImpl
import org.koin.dsl.module

val repositoriesModule = module {
    single<LoginRepository> { LoginRepositoryImpl(get(), get()) }
    single<ResetPasswordRepository> { ResetPasswordRepositoryImpl(get()) }
    single<UserDetailsRepository> { UserDetailsRepositoryImpl(get(), get()) }
    single<AccountStatsRepository> { AccountStatsRepositoryImpl(get(), get()) }
    single<WalletDataRepository> { WalletDataRepositoryImpl(get()) }
    single<DepositPlansRepository> { DepositPlansRepositoryImpl(get(), get()) }
    single<WithdrawRepository> { WithdrawRepositoryImpl(get()) }
    single<TransactionsRepository> { TransactionsRepositoryImpl(get(), get()) }
}