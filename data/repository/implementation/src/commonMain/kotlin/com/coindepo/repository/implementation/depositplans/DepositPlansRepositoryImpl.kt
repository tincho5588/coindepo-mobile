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

package com.coindepo.repository.implementation.depositplans

import com.coindepo.datasource.contracts.depositplans.RemoteDepositPlansDataSource
import com.coindepo.datasource.contracts.stats.LocalAccountStatsDataSource
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.repository.contracts.depositplans.DepositPlansRepository
import korlibs.bignumber.BigNum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DepositPlansRepositoryImpl(
    private val remoteDepositPlansDataSource: RemoteDepositPlansDataSource,
    private val localAccountStatsDataSource: LocalAccountStatsDataSource
): DepositPlansRepository {
    override suspend fun setDepositPlanVisibility(
        visibile: Boolean,
        walletName: String,
        depositPlanId: Int,
        userName: String,
        userId: Int,
        currency: Currency,
        clientToken: String
    ): OperationResult = withContext(Dispatchers.IO) {
        remoteDepositPlansDataSource.setDepositPlanVisibility(
            visibile, walletName, depositPlanId, userName, clientToken
        ).fold(
            onSuccess = {
                localAccountStatsDataSource.getAccountStats(userId)?.let {
                    remoteDepositPlansDataSource.updateDepositPlans(
                        it, userName, userId, currency, clientToken
                    ).fold(
                        onSuccess = {
                            localAccountStatsDataSource.saveAccountStats(it, userId)
                            OperationResult.Success(Unit)
                        },
                        onFailure = {
                            OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
                        }
                    )
                }
                OperationResult.Success(Unit)
            },
            onFailure = {
                OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
            }
        )
    }

    override suspend fun transferBetweenPlans(
        fromAccountId: Int,
        toAccountId: Int,
        coinId: Int,
        amount: BigNum,
        currency: Currency,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult =
        remoteDepositPlansDataSource.transferBetweenPlans(
            fromAccountId, toAccountId, coinId, amount, userName, userId, clientToken
        ).fold(
            onSuccess = {
                localAccountStatsDataSource.getAccountStats(userId)?.let {
                    remoteDepositPlansDataSource.updateDepositPlans(
                        it, userName, userId, currency, clientToken
                    ).fold(
                        onSuccess = {
                            localAccountStatsDataSource.saveAccountStats(it, userId)
                            OperationResult.Success(Unit)
                        },
                        onFailure = {
                            OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
                        }
                    )
                }
                OperationResult.Success(Unit)
            },
            onFailure = {
                OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
            }
        )

    override suspend fun openDepositPlan(
        depositPlanId: Int,
        coinId: Int,
        amount: BigNum,
        currency: Currency,
        userName: String,
        userId: Int,
        clientToken: String
    ): OperationResult =
        remoteDepositPlansDataSource.openDepositPlan(
            depositPlanId, coinId, amount, userName, userId, clientToken
        ).fold(
            onSuccess = {
                localAccountStatsDataSource.getAccountStats(userId)?.let {
                    remoteDepositPlansDataSource.updateDepositPlans(
                        it, userName, userId, currency, clientToken
                    ).fold(
                        onSuccess = {
                            localAccountStatsDataSource.saveAccountStats(it, userId)
                            OperationResult.Success(Unit)
                        },
                        onFailure = {
                            OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
                        }
                    )
                }
                OperationResult.Success(Unit)
            },
            onFailure = {
                OperationResult.Failure(it as? CoinDepoException ?: OtherErrorException())
            }
        )
}