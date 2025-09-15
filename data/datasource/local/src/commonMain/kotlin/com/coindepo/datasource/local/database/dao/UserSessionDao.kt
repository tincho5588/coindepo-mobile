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
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.coindepo.domain.entities.login.UserSession
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSession(pokemonSpecies: UserSessionEntity)

    @Query("SELECT * FROM user_session_data")
    suspend fun getUserSession(): List<UserSessionEntity>

    @Query("SELECT * FROM user_session_data")
    fun getUserSessionFlow(): Flow<List<UserSessionEntity>>

    @Query("DELETE FROM user_session_data")
    suspend fun nuke()
}

@Entity(
    tableName = "user_session_data",
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserSessionEntity(
    @PrimaryKey val userId: Int,
    val email: String,
    val userName: String,
    val accessToken: String
)

val UserSessionEntity.asUserSession: UserSession
    get() = UserSession(email, userName, userId, accessToken)

val UserSession.asUserSessionEntity: UserSessionEntity
    get() = UserSessionEntity(userId, email, userName, accessToken)