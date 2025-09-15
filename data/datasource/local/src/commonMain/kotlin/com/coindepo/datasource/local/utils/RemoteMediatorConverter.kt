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

package com.coindepo.datasource.local.utils

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

/**
 * Converts a provided remote mediator of T into a remote mediator of Q
 */
@OptIn(ExperimentalPagingApi::class)
class RemoteMediatorConverter<Key: Any, T: Any, Q: Any>(
    private val remoteMediator: RemoteMediator<Key, T>,
    private val transformer: (Q) ->  T
) : RemoteMediator<Key, Q>() {

    override suspend fun initialize(): InitializeAction =
        remoteMediator.initialize()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Key, Q>
    ): MediatorResult {
        return remoteMediator.load(
            loadType,
            state.convert(transformer)
        )
    }
}