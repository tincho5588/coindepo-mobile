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

package com.coindepo.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.coindepo.datasource.local.database.CoinDepoDataBase
import com.coindepo.datasource.local.database.DATABASE_NAME

fun getDatabaseBuilder(appContext: Context): RoomDatabase.Builder<CoinDepoDataBase> {
    val dbFile = appContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder<CoinDepoDataBase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}