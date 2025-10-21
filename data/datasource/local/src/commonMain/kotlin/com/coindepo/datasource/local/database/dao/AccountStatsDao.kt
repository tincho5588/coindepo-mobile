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

package com.coindepo.datasource.local.database.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.coindepo.domain.entities.stats.AccountStats
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.balance.LoanApr
import com.coindepo.domain.entities.stats.coin.AccountType
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.CoinBalance
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.entities.stats.tier.UserTier
import korlibs.bignumber.BigNum
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountStatsDao {

    @Transaction
    @Query("SELECT * FROM account_balance_table")
    fun getAccountStatsFlow(): Flow<List<AccountStatsEntity>>

    @Transaction
    @Query("SELECT * FROM account_balance_table WHERE userId = :userId")
    suspend fun getAccountStats(userId: Int): AccountStatsEntity?

    @Transaction
    @Query("SELECT * FROM account_balance_table")
    fun getAccountBalanceFlow(): Flow<List<AccountBalanceEntity>>

    @Transaction
    @Query("SELECT * FROM borrow_balance_table")
    fun getBorrowBalanceFlow(): Flow<List<BorrowBalanceEntity>>

    @Transaction
    @Query("SELECT * FROM user_tier_table")
    fun getUserTierFlow(): Flow<List<UserTierEntity>>

    @Transaction
    @Query("SELECT * FROM coin_details_table WHERE isToken = 0 ORDER BY displayOrder ASC")
    fun getCoinsFlow(): Flow<List<CoinEntity>>

    @Transaction
    @Query("SELECT * FROM coin_details_table WHERE isToken = 1 ORDER BY displayOrder ASC")
    fun getTokensFlow(): Flow<List<CoinEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAccountStats(
        accountBalance: AccountBalanceEntity,
        borrowBalance: BorrowBalanceEntity,
        userTierEntity: UserTierEntity,
        coins: List<CoinDetailsEntity>,
        coinBalances: List<CoinBalanceEntity>,
        depositPlans: List<DepositPlanEntity>,
        availableLoans: List<AvailableLoansEntity>
    )

    @Query("DELETE FROM account_balance_table")
    suspend fun nuke()
}

data class AccountStatsEntity(
    @Embedded val accountBalance: AccountBalanceEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val borrowBalance: BorrowBalanceEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val userTierEntity: UserTierEntity,
    @Relation(
        entity = CoinDetailsEntity::class,
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val coins: List<CoinEntity>
)

val AccountStatsEntity.asAccountStats: AccountStats
    get() = AccountStats(
        accountBalance = accountBalance.asAccountBalance,
        borrowBalance = borrowBalance.asBorrowBalance,
        userTier = userTierEntity.asUserTier,
        coins = coins.map { it.asCoin }.filter { !it.isToken },
        tokens = coins.map { it.asCoin }.filter { it.isToken }
    )


@Entity(
    tableName = "account_balance_table",
    indices = [Index(value = ["userId"], unique = true)]
)
data class AccountBalanceEntity(
    @PrimaryKey val userId: Int,
    val totalAccruedInterestCurrency: BigNum,
    val totalInterestPaidCurrency: BigNum,
    val totalBalance: BigNum,
    val totalAvailableBalance: BigNum,
    val totalTokensBalanceCurrency: BigNum,
    val totalTokensAccruedInterestCurrency: BigNum,
    val totalTokensInterestPaidCurrency: BigNum,
    val totalTokensBalanceAvailable: BigNum
)

val AccountBalanceEntity.asAccountBalance: AccountBalance
    get() = AccountBalance(
        totalAccruedInterestCurrency = totalAccruedInterestCurrency,
        totalInterestPaidCurrency = totalInterestPaidCurrency,
        totalBalance = totalBalance,
        totalAvailableBalance = totalAvailableBalance,
        totalTokensBalanceCurrency = totalTokensBalanceCurrency,
        totalTokensAccruedInterestCurrency = totalTokensAccruedInterestCurrency,
        totalTokensInterestPaidCurrency = totalTokensInterestPaidCurrency,
        totalTokensBalanceAvailable = totalTokensBalanceAvailable
    )

fun AccountBalance.asAccountBalanceEntity(userId: Int): AccountBalanceEntity = AccountBalanceEntity(
    userId = userId,
    totalAccruedInterestCurrency = totalAccruedInterestCurrency,
    totalInterestPaidCurrency = totalInterestPaidCurrency,
    totalBalance = totalBalance,
    totalAvailableBalance = totalAvailableBalance,
    totalTokensBalanceCurrency = totalTokensBalanceCurrency,
    totalTokensAccruedInterestCurrency = totalTokensAccruedInterestCurrency,
    totalTokensInterestPaidCurrency = totalTokensInterestPaidCurrency,
    totalTokensBalanceAvailable = totalTokensBalanceAvailable
)

@Entity(
    tableName = "borrow_balance_table",
    foreignKeys = [
        ForeignKey(
            entity = AccountBalanceEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class BorrowBalanceEntity(
    @PrimaryKey val userId: Int,
    val availablePortfolioBalance: BigNum,
    val creditLineLimitCurrency: BigNum,
    val currentLoansCurrency: BigNum,
    val availableCreditLimitCurrency: BigNum,
    val ltv: BigNum,
    val apr: BigNum,
    val aprs: List<LoanApr>  // TODO: make a relation for this
)

val BorrowBalanceEntity.asBorrowBalance
    get() = BorrowBalance(
        availablePortfolioBalance = availablePortfolioBalance,
        creditLineLimitCurrency = creditLineLimitCurrency,
        currentLoansCurrency = currentLoansCurrency,
        availableCreditLimitCurrency = availableCreditLimitCurrency,
        ltv = ltv,
        apr = apr,
        aprs = aprs
    )

fun BorrowBalance.asBorrowBalanceEntity(userId: Int): BorrowBalanceEntity = BorrowBalanceEntity(
    userId = userId,
    availablePortfolioBalance = availablePortfolioBalance,
    creditLineLimitCurrency = creditLineLimitCurrency,
    currentLoansCurrency = currentLoansCurrency,
    availableCreditLimitCurrency = availableCreditLimitCurrency,
    ltv = ltv,
    apr = apr,
    aprs = aprs
)

@Entity(
    tableName = "user_tier_table",
    foreignKeys = [
        ForeignKey(
            entity = AccountBalanceEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserTierEntity(
    @PrimaryKey val userId: Int,
    val coinDepoTokenPercentage: BigNum,
    val tierId: Int? = null
)

val UserTierEntity.asUserTier: UserTier
    get() = UserTier(
        coinDepoTokenPercentage,
        tierId
    )

fun UserTier.asUserTierEntity(userId: Int): UserTierEntity = UserTierEntity(
    userId,
    coinDepoTokenPercentage,
    tierId
)

data class CoinEntity(
    @Embedded val coinDetails: CoinDetailsEntity,
    @Relation(
        parentColumn = "coinId",
        entityColumn = "coinId"
    )
    val coinBalance: CoinBalanceEntity?,
    @Relation(
        parentColumn = "coinId",
        entityColumn = "coinId"
    )
    val depositPlans: List<DepositPlanEntity>,
    @Relation(
        parentColumn = "coinId",
        entityColumn = "coinId"
    )
    val availableLoans: AvailableLoansEntity?
)

val CoinEntity.asCoin: Coin
    get() = Coin(
        coinId = coinDetails.coinId,
        coinName = coinDetails.coinName,
        logoUrl = coinDetails.logoUrl,
        production = coinDetails.production,
        precision = coinDetails.precision,
        limit24h = coinDetails.limit24h,
        explorerUrl = coinDetails.explorerUrl,
        withdrawLimit = coinDetails.withdrawLimit,
        binanceTicker = coinDetails.binanceTicker,
        description = coinDetails.description,
        descriptionTicker = coinDetails.descriptionTicker,
        isStableCoin = coinDetails.isStableCoin,
        network = coinDetails.network,
        depName = coinDetails.depName,
        wthName = coinDetails.wthName,
        isActive = coinDetails.isActive,
        isToken = coinDetails.isToken,
        apy = coinDetails.apy,
        apr = coinDetails.apr,
        fee = coinDetails.fee,
        privatePrice = coinDetails.privatePrice,
        listingPrice = coinDetails.listingPrice,
        privateDiscount = coinDetails.privateDiscount,
        coinBalance = coinBalance?.asCoinBalance,
        depositPlans = depositPlans.map { it.asDepositPlan },
        availableLoans = availableLoans?.asAvailableLoans,
        order = coinDetails.displayOrder
    )

@Entity(
    tableName = "coin_details_table",
    foreignKeys = [
        ForeignKey(
            entity = AccountBalanceEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["coinId"], unique = true), Index(value = ["userId"], unique = false)]
)
data class CoinDetailsEntity(
    @PrimaryKey val coinId: Int,
    val userId: Int,
    val coinName: String,
    val logoUrl: String,
    val production: Boolean,
    val precision: Int,
    val limit24h: BigNum,
    val explorerUrl: String,
    val withdrawLimit: BigNum,
    val binanceTicker: String,
    val description: String,
    val descriptionTicker: String,
    val isStableCoin: Boolean,
    val network: String,
    val depName: String,
    val wthName: String,
    val isActive: Boolean,
    val isToken: Boolean,
    val apy: BigNum,
    val apr: BigNum,
    val fee: BigNum,
    val privatePrice: BigNum,
    val listingPrice: BigNum,
    val privateDiscount: BigNum,
    val displayOrder: Int
)

fun Coin.asCoinDetailsEntity(userId: Int): CoinDetailsEntity = CoinDetailsEntity(
    coinId = coinId,
    userId = userId,
    coinName = coinName,
    logoUrl = logoUrl,
    production = production,
    precision = precision,
    limit24h = limit24h,
    explorerUrl = explorerUrl,
    withdrawLimit = withdrawLimit,
    binanceTicker = binanceTicker,
    description = description,
    descriptionTicker = descriptionTicker,
    isStableCoin = isStableCoin,
    network = network,
    depName = depName,
    wthName = wthName,
    isActive = isActive,
    isToken = isToken,
    apy = apy,
    apr = apr,
    fee = fee,
    privatePrice = privatePrice,
    listingPrice = listingPrice,
    privateDiscount = privateDiscount,
    displayOrder = order
)

@Entity(
    tableName = "coin_balance_table",
    foreignKeys = [
        ForeignKey(
            entity = CoinDetailsEntity::class,
            parentColumns = ["coinId"],
            childColumns = ["coinId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["coinId"], unique = true)]
)
data class CoinBalanceEntity(
    @PrimaryKey val coinId: Int,
    val accruedInterest: BigNum,
    val accruedInterestCurrency: BigNum,
    val balance: BigNum,
    val balanceAvailable: BigNum,
    val balanceAvailableCurrency: BigNum,
    val balanceCurrency: BigNum,
    val binance24h: BigNum,
    val binance24hCurrency: BigNum,
    val freezedTrxBalance: BigNum,
    val freezedTrxBalanceCurrency: BigNum,
    val inTransit: BigNum,
    val inTransitCurrency: BigNum,
    val interest: BigNum,
    val interestCurrency: BigNum,
    val interestCurrencyCurrency: BigNum,
    val newTrxBalance: BigNum,
    val newTrxBalanceCurrency: BigNum,
    val pendingTrxBalance: BigNum,
    val pendingTrxBalanceCurrency: BigNum,
    val precision: Int,
    val processingTrxBalance: BigNum,
    val processingTrxBalanceCurrency: BigNum,
    val queuedTrxBalance: BigNum,
    val queuedTrxBalanceCurrency: BigNum,
    val summaryBalance: BigNum,
    val summaryBalanceCurrency: BigNum,
    val displayOrder: Int
)

val CoinBalanceEntity.asCoinBalance: CoinBalance
    get() = CoinBalance(
        accruedInterest = accruedInterest,
        accruedInterestCurrency = accruedInterestCurrency,
        balance = balance,
        balanceAvailable = balanceAvailable,
        balanceAvailableCurrency = balanceAvailableCurrency,
        balanceCurrency = balanceCurrency,
        binance24h = binance24h,
        binance24hCurrency = binance24hCurrency,
        freezedTrxBalance = freezedTrxBalance,
        freezedTrxBalanceCurrency = freezedTrxBalanceCurrency,
        inTransit = inTransit,
        inTransitCurrency = inTransitCurrency,
        interest = interest,
        interestCurrency = interestCurrency,
        interestCurrencyCurrency = interestCurrencyCurrency,
        newTrxBalance = newTrxBalance,
        newTrxBalanceCurrency = newTrxBalanceCurrency,
        pendingTrxBalance = pendingTrxBalance,
        pendingTrxBalanceCurrency = pendingTrxBalanceCurrency,
        precision = precision,
        processingTrxBalance = processingTrxBalance,
        processingTrxBalanceCurrency = processingTrxBalanceCurrency,
        queuedTrxBalance = queuedTrxBalance,
        queuedTrxBalanceCurrency = queuedTrxBalanceCurrency,
        summaryBalance = summaryBalance,
        summaryBalanceCurrency = summaryBalanceCurrency,
        displayOrder = displayOrder
    )

fun CoinBalance.asCoinBalanceEntity(coinId: Int): CoinBalanceEntity = CoinBalanceEntity(
    coinId = coinId,
    accruedInterest = accruedInterest,
    accruedInterestCurrency = accruedInterestCurrency,
    balance = balance,
    balanceAvailable = balanceAvailable,
    balanceAvailableCurrency = balanceAvailableCurrency,
    balanceCurrency = balanceCurrency,
    binance24h = binance24h,
    binance24hCurrency = binance24hCurrency,
    freezedTrxBalance = freezedTrxBalance,
    freezedTrxBalanceCurrency = freezedTrxBalanceCurrency,
    inTransit = inTransit,
    inTransitCurrency = inTransitCurrency,
    interest = interest,
    interestCurrency = interestCurrency,
    interestCurrencyCurrency = interestCurrencyCurrency,
    newTrxBalance = newTrxBalance,
    newTrxBalanceCurrency = newTrxBalanceCurrency,
    pendingTrxBalance = pendingTrxBalance,
    pendingTrxBalanceCurrency = pendingTrxBalanceCurrency,
    precision = precision,
    processingTrxBalance = processingTrxBalance,
    processingTrxBalanceCurrency = processingTrxBalanceCurrency,
    queuedTrxBalance = queuedTrxBalance,
    queuedTrxBalanceCurrency = queuedTrxBalanceCurrency,
    summaryBalance = summaryBalance,
    summaryBalanceCurrency = summaryBalanceCurrency,
    displayOrder = displayOrder
)

@Entity(
    tableName = "deposit_plan_table",
    foreignKeys = [
        ForeignKey(
            entity = CoinDetailsEntity::class,
            parentColumns = ["coinId"],
            childColumns = ["coinId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["coinId"], unique = false), Index(value = ["depositPlanId"], unique = false), Index(value = ["id"], unique = true)]
)
data class DepositPlanEntity(
    @PrimaryKey val id: String,
    val depositPlanId: Int,
    val coinId: Int,
    val userAccountId: Int,
    val accountTypeId: AccountType,
    val accountName: String,
    val walletName: String,
    val openDate: String,
    val lastPayout: String,
    val payoutDate: String,
    val period: String,
    val apr: BigNum,
    val precision: Int,
    val balance: BigNum,
    val balanceCurrency: BigNum,
    val inTransit: BigNum,
    val balanceAvailable: BigNum,
    val summaryBalance: BigNum,
    val summaryBalanceCurrency: BigNum,
    val accruedInterest: BigNum,
    val accruedInterestCurrency: BigNum,
    val paidInterest: BigNum,
    val paidInterestCurrency: BigNum,
    val canDelete: Boolean,
    val isAllowedToTransfer: Boolean,
    val apy: BigNum,
    val pendingBonus: Boolean,
    val bonusList: String,
    val isVisible: Boolean
)

val DepositPlanEntity.asDepositPlan: DepositPlan
    get() = DepositPlan(
        id = id,
        userAccountId = userAccountId,
        depositPlanId = depositPlanId,
        accountType = accountTypeId,
        accountName = accountName,
        walletName = walletName,
        openDate = openDate,
        lastPayout = lastPayout,
        payoutDate = payoutDate,
        period = period,
        apr = apr,
        precision = precision,
        balance = balance,
        balanceCurrency = balanceCurrency,
        inTransit = inTransit,
        balanceAvailable = balanceAvailable,
        summaryBalance = summaryBalance,
        summaryBalanceCurrency = summaryBalanceCurrency,
        accruedInterest = accruedInterest,
        accruedInterestCurrency = accruedInterestCurrency,
        paidInterest = paidInterest,
        paidInterestCurrency = paidInterestCurrency,
        canDelete = canDelete,
        isAllowedToTransfer = isAllowedToTransfer,
        apy = apy,
        pendingBonus = pendingBonus,
        bonusList = bonusList,
        isVisible = isVisible
    )

fun DepositPlan.asDepositPlanEntity(coinId: Int): DepositPlanEntity = DepositPlanEntity(
    id = id,
    coinId = coinId,
    userAccountId = userAccountId,
    depositPlanId = depositPlanId,
    accountTypeId = accountType,
    accountName = accountName,
    walletName = walletName,
    openDate = openDate,
    lastPayout = lastPayout,
    payoutDate = payoutDate,
    period = period,
    apr = apr,
    precision = precision,
    balance = balance,
    balanceCurrency = balanceCurrency,
    inTransit = inTransit,
    balanceAvailable = balanceAvailable,
    summaryBalance = summaryBalance,
    summaryBalanceCurrency = summaryBalanceCurrency,
    accruedInterest = accruedInterest,
    accruedInterestCurrency = accruedInterestCurrency,
    paidInterest = paidInterest,
    paidInterestCurrency = paidInterestCurrency,
    canDelete = canDelete,
    isAllowedToTransfer = isAllowedToTransfer,
    apy = apy,
    pendingBonus = pendingBonus,
    bonusList = bonusList,
    isVisible = isVisible
)

@Entity(
    tableName = "available_loans_table",
    foreignKeys = [
        ForeignKey(
            entity = CoinDetailsEntity::class,
            parentColumns = ["coinId"],
            childColumns = ["coinId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["coinId"], unique = true)]
)
data class AvailableLoansEntity(
    @PrimaryKey val coinId: Int,
    val available: List<String>,
    val rate: String
)

val AvailableLoansEntity.asAvailableLoans: AvailableLoans
    get() = AvailableLoans(
        available = available,
        rate = rate
    )

fun AvailableLoans.asAvailableLoansEntity(coinId: Int): AvailableLoansEntity = AvailableLoansEntity(
    coinId = coinId,
    available = available,
    rate = rate
)