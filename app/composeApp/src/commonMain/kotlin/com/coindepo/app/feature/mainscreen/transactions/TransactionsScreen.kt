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

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.coindepo.app.feature.mainscreen.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.no
import coindepo.app.composeapp.generated.resources.yes
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.LoadingSpinner
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.datasource.remote.stats.data.COIN_LOGO_URL
import com.coindepo.domain.entities.transactions.Operation
import com.coindepo.domain.entities.transactions.Transaction
import com.coindepo.domain.entities.transactions.TransactionStatus
import com.coindepo.domain.entities.transactions.TransactionType
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun TransactionsScreen(
    transactionsViewModel: TransactionsViewModel,
    showSnackBar: (String, SnackbarDuration, String?, () -> Unit) -> Unit
) {
    TransactionsScreenContent(
        transactionsViewModel.transactionsFlow.collectAsLazyPagingItems(),
        transactionsViewModel.transactionCancellationState.collectAsState().value,
        transactionsViewModel::startTransactionCancellation,
        transactionsViewModel::performTransactionCancellation,
        transactionsViewModel::clearTransactionCancellationState,
        showSnackBar
    )
}

@Composable
fun TransactionsScreenContent(
    items: LazyPagingItems<Transaction>,
    transactionCancellationState: TransactionCancellationState?,
    requestCancellation: (Transaction) -> Unit,
    cancelTransaction: (Transaction) -> Unit,
    clearTransactionCancellationState: () -> Unit,
    showSnackBar: (String, SnackbarDuration, String?, () -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        CoinDepoElevatedCard(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(1f), text = "Transaction History", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.FilterAlt,
                        contentDescription = ""
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = items.loadState.mediator?.refresh is LoadState.Loading,
                onRefresh = {
                    if (items.loadState.mediator?.refresh !is LoadState.Loading) {
                        items.refresh()
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                ) {
                    items(
                        items.itemCount,
                        items.itemKey {
                            with(it) {
                                "${entryId}_${trxId}_${operation}_${amount}"
                            }
                        }
                    ) { index ->
                        items[index]?.let {
                            TransactionItem(it) { transaction ->
                                requestCancellation(transaction)
                            }
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    if (items.loadState.mediator?.append is LoadState.Loading) {
                        item {
                            LoadingSpinner(
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                backgroundColor = MaterialTheme.colorScheme.background,
                                spinnerColor = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                if (items.loadState.mediator?.append is LoadState.Error) {
                    showSnackBar(
                        "Failed to load transactions.",
                        SnackbarDuration.Short,
                        "Retry",
                        { items.retry() }
                    )
                }

                when(items.loadState.mediator?.refresh) {
                    is LoadState.Error -> {
                        showSnackBar(
                            "Failed to load transactions.",
                            SnackbarDuration.Short,
                            "Retry",
                            { items.retry() }
                        )
                    }
                    is LoadState.Loading -> { }
                    is LoadState.NotLoading -> if (items.itemCount == 0) {
                        // TODO: Show no items view
                    } else {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                    null -> { }
                }
            }
        }

        when(transactionCancellationState) {
            is TransactionCancellationNotConfirmed,
            is TransactionCancellationLoading -> {
                TransactionCancellationDialog(
                    transactionCancellationState,
                    cancelRequest = { clearTransactionCancellationState() },
                    confirmTransactionCancellation = { cancelTransaction(it) }
                )
            }
            is TransactionCancellationSuccess -> {
                showSnackBar("Transaction cancelled", SnackbarDuration.Short, null, {})
                clearTransactionCancellationState()
            }
            is TransactionCancellationFailure -> {
                showSnackBar(
                    "Failed while trying to cancel transaction. Try again later.",
                    SnackbarDuration.Short,
                    null,
                    {}
                )
                clearTransactionCancellationState()
            }
            null -> {}
        }
    }
}

@Composable
fun TransactionCancellationDialog(
    transactionCancellationState: TransactionCancellationState,
    cancelRequest: () -> Unit,
    confirmTransactionCancellation: (Transaction) -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    onClick = {
                        cancelRequest()
                    },
                    enabled = transactionCancellationState is TransactionCancellationNotConfirmed
                ) {
                    Text(stringResource(Res.string.no))
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (transactionCancellationState is TransactionCancellationNotConfirmed) {
                            confirmTransactionCancellation(transactionCancellationState.transaction)
                        }
                    }
                ) {
                    if (transactionCancellationState is TransactionCancellationNotConfirmed) {
                        Text(stringResource(Res.string.yes))
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        title = {
            Text(modifier = Modifier.fillMaxWidth(), text = "Cancellation Request", textAlign = TextAlign.Center)
        },
        text = {
            Text("This withdrawal request is currently in progress. While the withdrawal is in the \"Pending\" status you can cancel it.\n\n" +
            "You will receive an email notification when the cancellation process is completed. This withdrawal will be displayed on the \"Transactions\" page as \"Cancelled\".\n\n" +
            "Are you sure you want to cancel this withdrawal?", textAlign = TextAlign.Center)
        }
    )
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    requestCancellation: (Transaction) -> Unit
) {
    val isExpandable = !transaction.sourceAddress.isNullOrEmpty() || !transaction.destinationAddress.isNullOrEmpty() || !transaction.txHash.isNullOrEmpty()

    val expanded = remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Row(
            Modifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = transaction.coinLogoUrl,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.width(8.dp))
            Column(
                Modifier.weight(1f)
            ) {
                Text(transaction.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                if (transaction.firstTxt.isNotEmpty() || transaction.secondTxt.isNotEmpty()) Text("${transaction.firstTxt} ${transaction.secondTxt}", style = MaterialTheme.typography.titleSmall.copy(Color(0xFFa19ead)))
            }
            if (isExpandable) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(
                            enabled = true,
                            onClick = { expanded.value = !expanded.value }
                        ),
                    imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = ""
                )
            }
        }
        AnimatedVisibility(
            expanded.value
        ) {
            Column {
                Spacer(Modifier.height(8.dp))
                if (!transaction.sourceAddress.isNullOrEmpty()) {
                    Text(modifier = Modifier.padding(start = 56.dp), text = "Source Wallet Address", style = MaterialTheme.typography.bodySmall.copy(Color(0xFFa19ead)))
                    Text(modifier = Modifier.padding(start = 56.dp), text = transaction.sourceAddress!!, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(Modifier.height(8.dp))
                if (!transaction.destinationAddress.isNullOrEmpty()) {
                    Text(modifier = Modifier.padding(start = 56.dp), text = "Destination Wallet Address", style = MaterialTheme.typography.bodySmall.copy(Color(0xFFa19ead)))
                    Text(modifier = Modifier.padding(start = 56.dp), text = transaction.destinationAddress!!, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
                Spacer(Modifier.height(8.dp))
                if (!transaction.txHash.isNullOrEmpty()) {
                    Text(modifier = Modifier.padding(start = 56.dp), text = "Transaction Hash", style = MaterialTheme.typography.bodySmall.copy(Color(0xFFa19ead)))
                    Text(modifier = Modifier.padding(start = 56.dp), text = transaction.txHash!!, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        val displayAmount = when(transaction.operation) {
            Operation.CREDIT -> "- "
            Operation.DEBIT -> "+ "
        } + transaction.amount.replace("+", "").replace("-", "")
        Text("$displayAmount ${transaction.coinBinanceTicker}")
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth().wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when(transaction.trxStatus) {
                TransactionStatus.PROCESSING -> Text(modifier = Modifier.background(Color(0xFFF5F5DC), shape = CircleShape).padding(horizontal = 16.dp, vertical = 4.dp), text = "Processing", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFFFAA33), fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center)
                TransactionStatus.PENDING -> {
                    Row(
                        modifier = Modifier.background(Color(0xFFF5F5DC), shape = CircleShape).padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pending", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFFFAA33), fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center)
                        if (transaction.trxType == TransactionType.WITHDRAWAL) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                modifier = Modifier.size(24.dp).clip(CircleShape).clickable(enabled = true, onClick = { requestCancellation(transaction) }),
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
                TransactionStatus.COMPLETED -> Text(modifier = Modifier.background(Color(0xFFdfffec), shape = CircleShape).padding(horizontal = 16.dp, vertical = 4.dp), text = "Completed", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1e7e34), fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center)
                TransactionStatus.CANCELLED -> Text(modifier = Modifier.background(Color(0xffffe1df), shape = CircleShape).padding(horizontal = 16.dp, vertical = 4.dp), text = "Cancelled", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red, fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center)
                TransactionStatus.FAILED -> Text(modifier = Modifier.background(Color(0xffffe1df), shape = CircleShape).padding(horizontal = 16.dp, vertical = 4.dp), text = "Failed", style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red, fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center)
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(transaction.lastUpdate.formattedDate, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall.copy(Color(0xFFa19ead)))
                Text(transaction.lastUpdate.formattedTime, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall.copy(Color(0xFFa19ead)))
            }
        }
    }
}

private val Instant.formattedDate: String
    get() {
        val formater = LocalDateTime.Format {
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            day()
            chars(", ")
            year()
        }

        return this.toLocalDateTime(TimeZone.UTC).format(formater)
    }

private val Instant.formattedTime: String
    get() {
        val formater = LocalDateTime.Format {
            hour()
            char(':')
            minute()
            chars(" UTC")
        }

        return this.toLocalDateTime(TimeZone.UTC).format(formater)
    }

@Preview
@Composable
fun TransactionScreenPreview() {
    CoinDepoTheme {
        TransactionsScreenContent(
            items = flow<PagingData<Transaction>> {
                PagingData.Companion.from(
                listOf(
                    Transaction(
                        "eeb21042-ae43-49e9-b240-1a25f31f033f",
                        3560781,
                        "Withdrawal",
                        "Withdrawal from Current Interest Account ",
                        "",
                        "ecba7af2-c165-4758-bf3a-77415a6167c7",
                        "-500",
                        "USDT_BSC",
                        "USDT",
                        "${COIN_LOGO_URL}usdt_bsc.svg",
                        19502,
                        19502,
                        TransactionType.WITHDRAWAL,
                        TransactionStatus.PENDING,
                        Operation.CREDIT,
                        "0x39303C730904BE7C7ECFF1C5C2c6F6dC58B3B18C",
                        "0x64e72c6ccbedf7088c35ae5876bac4049da7550d",
                        "",
                        "0x78c888e60898cfab9558571d77234a65c421c33edccbc039f221976ef7f7314e",
                        Clock.System.now(),
                        Clock.System.now(),
                        false
                    )
                )
            ) }.collectAsLazyPagingItems(),
            null,
            {},
            {},
            {},
            {_,_,_,_ -> }
        )
    }
}

@Preview
@Composable
fun TransactionCancelPreview() {
    CoinDepoTheme {
        TransactionsScreenContent(
            items = flow<PagingData<Transaction>> {
                PagingData.Companion.from(
                    listOf(
                        Transaction(
                            "eeb21042-ae43-49e9-b240-1a25f31f033f",
                            3560781,
                            "Withdrawal",
                            "Withdrawal from Current Interest Account ",
                            "",
                            "ecba7af2-c165-4758-bf3a-77415a6167c7",
                            "-500",
                            "USDT_BSC",
                            "USDT",
                            "${COIN_LOGO_URL}usdt_bsc.svg",
                            19502,
                            19502,
                            TransactionType.WITHDRAWAL,
                            TransactionStatus.PENDING,
                            Operation.CREDIT,
                            "0x39303C730904BE7C7ECFF1C5C2c6F6dC58B3B18C",
                            "0x64e72c6ccbedf7088c35ae5876bac4049da7550d",
                            "",
                            "0x78c888e60898cfab9558571d77234a65c421c33edccbc039f221976ef7f7314e",
                            Clock.System.now(),
                            Clock.System.now(),
                            false
                        )
                    )
                ) }.collectAsLazyPagingItems(),
            TransactionCancellationNotConfirmed(
                Transaction(
                    "eeb21042-ae43-49e9-b240-1a25f31f033f",
                    3560781,
                    "Withdrawal",
                    "Withdrawal from Current Interest Account ",
                    "",
                    "ecba7af2-c165-4758-bf3a-77415a6167c7",
                    "-500",
                    "USDT_BSC",
                    "USDT",
                    "${COIN_LOGO_URL}usdt_bsc.svg",
                    19502,
                    19502,
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.PENDING,
                    Operation.CREDIT,
                    "0x39303C730904BE7C7ECFF1C5C2c6F6dC58B3B18C",
                    "0x64e72c6ccbedf7088c35ae5876bac4049da7550d",
                    "",
                    "0x78c888e60898cfab9558571d77234a65c421c33edccbc039f221976ef7f7314e",
                    Clock.System.now(),
                    Clock.System.now(),
                    false
                )
            ),
            {},
            {},
            {},
            {_,_,_,_ -> }
        )
    }
}

@Preview
@Composable
fun TransactionItemPreview() {
    CoinDepoTheme {
        Box(
            Modifier.background(Color.White)
        ) {
            TransactionItem(
                Transaction(
                    "eeb21042-ae43-49e9-b240-1a25f31f033f",
                    3560781,
                    "Withdrawal",
                    "Withdrawal from Current Interest Account ",
                    "",
                    "ecba7af2-c165-4758-bf3a-77415a6167c7",
                    "-500",
                    "USDT_BSC",
                    "USDT",
                    "${COIN_LOGO_URL}usdt_bsc.svg",
                    19502,
                    19502,
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.PENDING,
                    Operation.CREDIT,
                    "0x39303C730904BE7C7ECFF1C5C2c6F6dC58B3B18C",
                    "0x64e72c6ccbedf7088c35ae5876bac4049da7550d",
                    "",
                    "0x78c888e60898cfab9558571d77234a65c421c33edccbc039f221976ef7f7314e",
                    Clock.System.now(),
                    Clock.System.now(),
                    false
                )
            ) {}
        }

    }
}