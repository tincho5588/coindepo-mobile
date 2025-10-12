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

import com.coindepo.domain.usecases.depositplans.SetDepositPlanVisibleUseCase
import com.coindepo.domain.usecases.depositplans.TransferBetweenPlansUseCase
import com.coindepo.domain.usecases.login.GetUserSessionUseCase
import com.coindepo.domain.usecases.login.LogOutUseCase
import com.coindepo.domain.usecases.login.LoginUseCase
import com.coindepo.domain.usecases.login.ResendVerificationCodeUseCase
import com.coindepo.domain.usecases.resetpassword.ResetPasswordUseCase
import com.coindepo.domain.usecases.stats.GetAccountStatsUseCase
import com.coindepo.domain.usecases.transactions.CancelTransactionUseCase
import com.coindepo.domain.usecases.transactions.GetTransactionsListPaged
import com.coindepo.domain.usecases.userdetails.GetUserDetailsUseCase
import com.coindepo.domain.usecases.wallet.GetWalletDataUseCase
import com.coindepo.domain.usecases.withdraw.ConfirmWithdrawUseCase
import com.coindepo.domain.usecases.withdraw.GetWithdrawFeesEstimateUseCase
import com.coindepo.domain.usecases.withdraw.RequestWithdrawUseCase
import com.coindepo.domain.usecases.withdraw.SendWithdrawEmailVerificationCodeUseCase
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope

@OptIn(KoinExperimentalAPI::class)
val useCaseModule = module {
    viewModelScope {
        scoped<GetUserSessionUseCase> { GetUserSessionUseCase(get()) }
        scoped<LoginUseCase> { LoginUseCase(get()) }
        scoped<ResendVerificationCodeUseCase> { ResendVerificationCodeUseCase(get()) }
        scoped<LogOutUseCase> { LogOutUseCase(get()) }
        scoped<ResetPasswordUseCase> { ResetPasswordUseCase(get()) }
        scoped<GetUserDetailsUseCase> { GetUserDetailsUseCase(get(), get(), get()) }
        scoped<GetAccountStatsUseCase> { GetAccountStatsUseCase(get(), get(), get()) }
        scoped<SetDepositPlanVisibleUseCase> { SetDepositPlanVisibleUseCase(get(), get(), get()) }
        scoped<GetWalletDataUseCase> { GetWalletDataUseCase(get(), get(), get()) }
        scoped<TransferBetweenPlansUseCase> { TransferBetweenPlansUseCase(get(), get(), get()) }
        scoped<GetWithdrawFeesEstimateUseCase> { GetWithdrawFeesEstimateUseCase(get(), get(), get()) }
        scoped<RequestWithdrawUseCase> { RequestWithdrawUseCase(get(), get(), get()) }
        scoped<ConfirmWithdrawUseCase> { ConfirmWithdrawUseCase(get(), get(), get()) }
        scoped<SendWithdrawEmailVerificationCodeUseCase> { SendWithdrawEmailVerificationCodeUseCase(get(), get(), get()) }
        scoped<GetTransactionsListPaged> { GetTransactionsListPaged(get(), get(), get()) }
        scoped<CancelTransactionUseCase> { CancelTransactionUseCase(get(), get(), get()) }
    }
}