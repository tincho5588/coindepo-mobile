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

import androidx.paging.PagingSource
import androidx.paging.PagingState

fun <Key: Any, Q: Any, T: Any> PagingState<Key, Q>.convert(
    transformer: (Q) ->  T
): PagingState<Key, T> =
    PagingState(
        pages = pages.map { page ->
            PagingSource.LoadResult.Page(
                data = page.data.map { transformer(it) },
                prevKey = page.prevKey,
                nextKey = page.nextKey,
                itemsBefore = page.itemsBefore,
                itemsAfter = page.itemsAfter
            )
        },
        anchorPosition = anchorPosition,
        config = config,
        leadingPlaceholderCount = getLeadingPlaceholderCount()
    )

// Hope the property will be opened. Or the mapping function become a part of the library.
// https://issuetracker.google.com/issues/168309730
private fun <Key : Any, Value : Any> PagingState<Key, Value>.getLeadingPlaceholderCount(): Int {
    val stringForm = this.toString()
    val searchString = "leadingPlaceholderCount="
    return stringForm.substring(stringForm.indexOf(searchString) + searchString.length , stringForm.indexOfLast { it == ')' } ).toInt()
}