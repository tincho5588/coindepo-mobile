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

@file:OptIn(ExperimentalTime::class)

package com.coindepo.datasource.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.coindepo.domain.entities.transactions.Operation
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.transactions.TransactionStatus
import com.coindepo.domain.entities.transactions.TransactionType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import androidx.room.Transaction as RoomTransaction

@Dao
interface TransactionsDao {

    @Query("SELECT * FROM transactions_table ORDER BY createDate DESC")
    fun getTransactionsPagingSource(): PagingSource<Int, TransactionEntity>

    @RoomTransaction
    suspend fun saveTransactionsClean(transactions: List<TransactionEntity>) {
        nuke()
        saveTransactions(transactions)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions_table ORDER BY createDate DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("DELETE FROM transactions_table")
    suspend fun nuke()
}

@Entity(
    tableName = "transactions_table",
    indices = [Index(value = ["id"], unique = true)]
    )
data class TransactionEntity(
    @PrimaryKey val id: String,
    val entryId: String,
    val trxId: Int,
    val title: String,
    val firstTxt: String,
    val secondTxt: String,
    val foreignTrxId: String,
    val amount: String,
    val coinName: String,
    val coinBinanceTicker: String,
    val coinLogoUrl: String,
    val userFrom: Int,
    val userTo: Int,
    val trxType: TransactionType,
    val trxStatus: TransactionStatus,
    val operation: Operation,
    val sourceAddress: String? = null,
    val destinationAddress: String? = null,
    val destinationTag: String? = null,
    val txHash: String? = null,
    val createDate: Long,
    val lastUpdate: Long,
    val isToken: Boolean
)

val TransactionEntity.asTransaction: Transaction
    get() = Transaction(
        entryId = entryId,
        trxId = trxId,
        title = title,
        firstTxt = firstTxt,
        secondTxt = secondTxt,
        foreignTrxId = foreignTrxId,
        amount = amount,
        coinName = coinName,
        coinBinanceTicker = coinBinanceTicker,
        coinLogoUrl = coinLogoUrl,
        userFrom = userFrom,
        userTo = userTo,
        trxType = trxType,
        trxStatus = trxStatus,
        operation = operation,
        sourceAddress = sourceAddress,
        destinationAddress = destinationAddress,
        destinationTag = destinationTag,
        txHash = txHash,
        createDate = Instant.fromEpochMilliseconds(createDate),
        lastUpdate = Instant.fromEpochMilliseconds(lastUpdate),
        isToken = isToken
    )

val Transaction.asTransactionEntity: TransactionEntity
    get() = TransactionEntity(
        "${entryId}_${trxId}_${operation}_${amount}",
        entryId,
        trxId,
        title,
        firstTxt,
        secondTxt,
        foreignTrxId,
        amount,
        coinName,
        coinBinanceTicker,
        coinLogoUrl,
        userFrom,
        userTo,
        trxType,
        trxStatus,
        operation,
        sourceAddress,
        destinationAddress,
        destinationTag,
        txHash,
        createDate.toEpochMilliseconds(),
        lastUpdate.toEpochMilliseconds(),
        isToken
    )