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

package com.coindepo.datasource.local.database

import androidx.paging.PagingSource
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.util.performInTransactionSuspending
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.coindepo.datasource.local.database.dao.AccountBalanceEntity
import com.coindepo.datasource.local.database.dao.AccountStatsDao
import com.coindepo.datasource.local.database.dao.AvailableLoansEntity
import com.coindepo.datasource.local.database.dao.BorrowBalanceEntity
import com.coindepo.datasource.local.database.dao.CoinBalanceEntity
import com.coindepo.datasource.local.database.dao.CoinDetailsEntity
import com.coindepo.datasource.local.database.dao.DepositPlanEntity
import com.coindepo.datasource.local.database.dao.TransactionEntity
import com.coindepo.datasource.local.database.dao.TransactionsDao
import com.coindepo.datasource.local.database.dao.UserDetailsDao
import com.coindepo.datasource.local.database.dao.UserDetailsEntity
import com.coindepo.datasource.local.database.dao.UserSessionDao
import com.coindepo.datasource.local.database.dao.UserSessionEntity
import com.coindepo.datasource.local.database.dao.asAccountBalance
import com.coindepo.datasource.local.database.dao.asAccountBalanceEntity
import com.coindepo.datasource.local.database.dao.asAccountStats
import com.coindepo.datasource.local.database.dao.asAvailableLoansEntity
import com.coindepo.datasource.local.database.dao.asBorrowBalance
import com.coindepo.datasource.local.database.dao.asBorrowBalanceEntity
import com.coindepo.datasource.local.database.dao.asCoin
import com.coindepo.datasource.local.database.dao.asCoinBalanceEntity
import com.coindepo.datasource.local.database.dao.asCoinDetailsEntity
import com.coindepo.datasource.local.database.dao.asDepositPlanEntity
import com.coindepo.datasource.local.database.dao.asTransaction
import com.coindepo.datasource.local.database.dao.asTransactionEntity
import com.coindepo.datasource.local.database.dao.asUserDetails
import com.coindepo.datasource.local.database.dao.asUserDetailsEntity
import com.coindepo.datasource.local.database.dao.asUserSession
import com.coindepo.datasource.local.database.dao.asUserSessionEntity
import com.coindepo.domain.entities.login.UserSession
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.userdetails.UserDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Database(
    entities = [
        UserSessionEntity::class,
        UserDetailsEntity::class,
        AccountBalanceEntity::class,
        BorrowBalanceEntity::class,
        CoinDetailsEntity::class,
        CoinBalanceEntity::class,
        DepositPlanEntity::class,
        AvailableLoansEntity::class,
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(Converters::class)
@ConstructedBy(DatabaseConstructor::class)
abstract class CoinDepoDataBase : RoomDatabase() {

    internal abstract val userSessionDao: UserSessionDao
    internal abstract val userDetailsDao: UserDetailsDao
    internal abstract val accountStatsDao: AccountStatsDao
    internal abstract val transactionsDao: TransactionsDao

    val userSessionFlow: Flow<UserSession?>
        get() = userSessionDao.getUserSessionFlow().map {
            check(it.size <= 1)
            it.firstOrNull()?.asUserSession
        }.distinctUntilChanged()

    val userDetailsFlow: Flow<UserDetails?>
        get() = userDetailsDao.getUserDetailsFlow().map {
            check(it.size <= 1)
            it.firstOrNull()?.asUserDetails
        }.distinctUntilChanged()

    val accountStatsFlow: Flow<AccountStats?>
        get() = accountStatsDao.getAccountStatsFlow().map {
            check(it.size <= 1)
            it.firstOrNull()?.asAccountStats
        }

    val accountBalance: Flow<AccountBalance?>
        get() = accountStatsDao.getAccountBalanceFlow().map {
            check(it.size <= 1)
            it.firstOrNull()?.asAccountBalance
        }
    val borrowBalance: Flow<BorrowBalance?>
        get() = accountStatsDao.getBorrowBalanceFlow().map {
            check(it.size <= 1)
            it.firstOrNull()?.asBorrowBalance
        }
    val coins: Flow<List<Coin>>
        get() = accountStatsDao.getCoinsFlow().map {
            it.map { it.asCoin }
        }
    val tokens: Flow<List<Coin>>
        get() = accountStatsDao.getTokensFlow().map {
            it.map { it.asCoin }
        }

    suspend fun saveUserSession(userSession: UserSession) = userSessionDao.insertUserSession(userSession.asUserSessionEntity)

    suspend fun getUserSession(): UserSession? = with(userSessionDao.getUserSession()) {
        check(size <= 1)
        firstOrNull()?.asUserSession
    }

    suspend fun saveUserDetails(userDetails: UserDetails) {
        userDetailsDao.insertUserDetails(userDetails.asUserDetailsEntity)
    }

    suspend fun saveAccountStats(accountStats: AccountStats, userId: Int) {
        performInTransactionSuspending(this) {
            //accountStatsDao.nuke()
            accountStatsDao.saveAccountStats(
                accountStats.accountBalance.asAccountBalanceEntity(userId),
                accountStats.borrowBalance.asBorrowBalanceEntity(userId),
                accountStats.coins.map { it.asCoinDetailsEntity(userId) } + accountStats.tokens.map { it.asCoinDetailsEntity(userId) },
                accountStats.coins.mapNotNull { it.coinBalance?.asCoinBalanceEntity(it.coinId) } + accountStats.tokens.mapNotNull { it.coinBalance?.asCoinBalanceEntity(it.coinId) },
                accountStats.coins.map { it.depositPlans.map { depositPlan -> depositPlan.asDepositPlanEntity(it.coinId) } }.flatten()
                        + accountStats.tokens.map { it.depositPlans.map { depositPlan -> depositPlan.asDepositPlanEntity(it.coinId) } }.flatten(),
                accountStats.coins.mapNotNull { it.availableLoans?.asAvailableLoansEntity(it.coinId) }
            )
        }
    }

    suspend fun getAccountStats(userId: Int): AccountStats? {
        return accountStatsDao.getAccountStats(userId)?.asAccountStats
    }

    fun getTransactionsPagingSource(): PagingSource<Int, TransactionEntity> = transactionsDao.getTransactionsPagingSource()

    suspend fun saveTransactions(transactions: List<Transaction>, clean: Boolean) {
        if (clean) {
            transactionsDao.saveTransactionsClean(transactions.map { it.asTransactionEntity })
        } else {
            transactionsDao.saveTransactions(transactions.map { it.asTransactionEntity })
        }
    }

    suspend fun getAllTransactions(): List<Transaction> = transactionsDao.getAllTransactions().map { it.asTransaction }

    suspend fun nuke() {
        userSessionDao.nuke()
        userDetailsDao.nuke()
        accountStatsDao.nuke()
        transactionsDao.nuke()
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DatabaseConstructor : RoomDatabaseConstructor<CoinDepoDataBase> {
    override fun initialize(): CoinDepoDataBase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<CoinDepoDataBase>
): CoinDepoDataBase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .build()
}

const val DATABASE_NAME = "coindepo_database.db"