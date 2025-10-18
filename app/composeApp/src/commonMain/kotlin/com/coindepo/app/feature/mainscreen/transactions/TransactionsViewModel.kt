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

package com.coindepo.app.feature.mainscreen.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.coindepo.domain.entities.CoinDepoException
import com.coindepo.domain.entities.OperationResult
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.transactions.TransactionsFilters
import com.coindepo.domain.usecases.transactions.CancelTransactionUseCase
import com.coindepo.domain.usecases.transactions.GetTransactionsListPaged
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val getTransactionsListPaged: GetTransactionsListPaged,
    private val cancelTransactionUseCase: CancelTransactionUseCase
) : ViewModel() {

    private val transactionsPager = getTransactionsListPaged.getTransactionsListPager()
    val transactionsFlow: Flow<PagingData<Transaction>> = transactionsPager.transactions.cachedIn(viewModelScope)

    private val _transactionCancellationState = MutableStateFlow<TransactionCancellationState?>(null)
    val transactionCancellationState: StateFlow<TransactionCancellationState?> = _transactionCancellationState

    private val _transactionsFilters = MutableStateFlow(TransactionsFilters())
    val transactionsFilters: StateFlow<TransactionsFilters> = _transactionsFilters


    fun startTransactionCancellation(transaction: Transaction) {
        _transactionCancellationState.value = TransactionCancellationNotConfirmed(transaction)
    }

    fun performTransactionCancellation(transaction: Transaction) {
        viewModelScope.launch {
            _transactionCancellationState.value = TransactionCancellationLoading

            val result = cancelTransactionUseCase.performTransactionCancellation(transaction)

            _transactionCancellationState.value = when(result) {
                is OperationResult.Failure -> TransactionCancellationFailure(result.e)
                is OperationResult.Success<*> -> TransactionCancellationSuccess
            }
        }
    }

    fun setFilters(newFilters: TransactionsFilters) {
        transactionsPager.filtersSetter.setFilters(newFilters)
        _transactionsFilters.value = newFilters
    }

    fun clearTransactionCancellationState() {
        _transactionCancellationState.value = null
    }
}

sealed interface TransactionCancellationState

data class TransactionCancellationNotConfirmed(
    val transaction: Transaction
) : TransactionCancellationState
data object TransactionCancellationLoading : TransactionCancellationState
data object TransactionCancellationSuccess : TransactionCancellationState
data class TransactionCancellationFailure(
    val e: CoinDepoException
) : TransactionCancellationState
