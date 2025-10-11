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

package com.coindepo.app.feature.mainscreen.transfer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.cancel
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.InformationTooltip
import com.coindepo.app.feature.common.MyExposedDropDownMenu
import com.coindepo.app.feature.common.WarningBanner
import com.coindepo.app.feature.common.WarningBannerColor
import com.coindepo.app.feature.common.formatCrypto
import com.coindepo.app.feature.mainscreen.AccountStatsViewModel
import com.coindepo.app.feature.mainscreen.earn.periodStringRes
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.entities.utils.asBigNum
import korlibs.bignumber.BigNum
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TransferScreen(
    accountStatsViewModel: AccountStatsViewModel,
    transferViewModel: TransferViewModel,
    fromPlanId: String,
    showSnackBar: (String, SnackbarDuration) -> Unit,
    onCancel: () -> Unit
) {
    val transferState = transferViewModel.state.collectAsState().value
    if (transferState == Success) {
        onCancel()
    } else {
        val coin =
            accountStatsViewModel.coins.collectAsState().value.first { it.depositPlans.any { it.id == fromPlanId } }
        val transferData = remember { mutableStateOf<TransferData?>(null) }

        TransferScreenContent(
            coin,
            fromPlanId,
            { fromPlan, toPlan, amount ->
                transferData.value = TransferData(fromPlan, toPlan, amount)
            },
            onCancel
        )

        when (transferState) {
            Failure -> {
                showSnackBar("Failed to transfer funds.", SnackbarDuration.Short)
                transferData.value = null
                transferViewModel.resetState()
            }

            null, InProgress -> if (transferData.value != null) {
                TransferConfirmationDialog(
                    coin,
                    transferData.value!!,
                    transferState,
                    {
                        transferViewModel.transfer(
                            coin,
                            it.fromPlan,
                            it.toPlan,
                            it.amount,
                            accountStatsViewModel.currency.value
                        )
                    },
                    {
                        transferData.value = null
                    }
                )
            }

            NoNetwork -> {
                showSnackBar("Failed to transfer funds. Check your internet connection and try again.", SnackbarDuration.Short)
                transferData.value = null
                transferViewModel.resetState()
            }

            Success -> {}
        }
    }
}

@Composable
fun TransferConfirmationDialog(
    coin: Coin,
    transferData: TransferData,
    transferState: TransferState?,
    onConfirm: (TransferData) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (transferState == null) onCancel()
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = coin.logoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${coin.binanceTicker} Transfer Confirmation",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Text(text = buildAnnotatedString {
                append("Please confirm your transfer of ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${transferData.amount} ${coin.binanceTicker}")
                }
                append(" from ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(transferData.fromPlan.accountName)
                }
                append(" to the ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(transferData.toPlan.accountName)
                }
            })
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    onClick = onCancel,
                    enabled = transferState == null
                ) {
                    Text(stringResource(Res.string.cancel))
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onConfirm(transferData)
                    }
                ) {
                    if (transferState == null) {
                        Text("Confirm")
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun TransferScreenContent(
    coin: Coin,
    initialFromPlanId: String,
    onTransfer: (DepositPlan, DepositPlan, BigNum) -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .verticalScroll(scrollState)
    ) {
        CoinDepoElevatedCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp)
            ) {
                val selectedFromPlanId = remember { mutableStateOf(initialFromPlanId) }
                val selectedFromPlan =
                    coin.depositPlans.first { it.id == selectedFromPlanId.value }

                val selectedToPlanId = remember { mutableStateOf(
                    if (selectedFromPlan.accountTypeId != 1) coin.depositPlans.first { it.accountTypeId == 1 }.id else null
                ) }
                val selectedToPlan =
                    coin.depositPlans.find { it.id == selectedToPlanId.value }

                val amount = remember { mutableStateOf<String?>(null) }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = coin.logoUrl,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transfer Between ${coin.binanceTicker} Accounts",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                AccountSelectionDropDownMenu(
                    "From",
                    coin.binanceTicker,
                    coin.precision,
                    selectedFromPlan,
                    coin.depositPlans.filter { it.isVisible }
                ) { newPlanId ->
                    selectedFromPlanId.value = newPlanId

                    val newSelectedFromPlan =
                        coin.depositPlans.first { it.id == newPlanId }

                    selectedToPlanId.value = if (newSelectedFromPlan.accountTypeId != 1) {
                        coin.depositPlans.first { it.accountTypeId == 1 }.id
                    } else {
                        null
                    }

                    amount.value = null
                }
                Spacer(Modifier.height(16.dp))
                AccountSelectionDropDownMenu(
                    "To",
                    coin.binanceTicker,
                    coin.precision,
                    selectedToPlan,
                    if (selectedFromPlan.accountTypeId != 1) coin.depositPlans.filter { it.accountTypeId == 1 } else coin.depositPlans.filter { it.isVisible && it.id != selectedFromPlanId.value && it.isAllowedToTransfer }
                ) {
                    selectedToPlanId.value = it
                }
                Spacer(Modifier.height(16.dp))
                AmountInputTextHeader("Amount", coin.binanceTicker, coin.precision, selectedFromPlan.balanceAvailable, "The amount available for transfer may differ from the account balance if you have already made a partial withdrawal of funds from this account and it is in the \"Pending\" status. Until the processing is completed, this amount is displayed on the account balance.") { amount.value = it.toString() }
                AmountInputText(amount.value, coin, BigNum.ZERO, selectedFromPlan.balanceAvailable, coin.binanceTicker) { amount.value = it}
                Spacer(Modifier.height(16.dp))
                WarningBanner(
                    false,
                    "WARNING! When transferring from Current Interest Account",
                    "Please note that you can transfer funds from your Current Interest Account to your other Interest Accounts in two cases only:\n" +
                            "1) When the Payout Date of the Interest Account you are transferring funds to, is matching with the transfer date. For example, if today is 10th March and the Payout Date of the Weekly Interest Account is 10th March.\n" +
                            "2) When the start date of the compounding period is matching with the transfer date. For example, if you opened a Weekly Account on 10th March and today is 17th March, the next Payout Interest Date is 24th March. Today's date is considered as start date and you can transfer funds to this account.\n" +
                            "If on the date of transfer, you do not have any Interest Accounts available for transfer, you can instantly open a new account with any compounding period using the function \"Add New Account\".\n" +
                            "This limitation allows you to avoid misuse of your funds because if you fund your account before the end of the compounding period, Accrued Interest will be recalculated after the Payout Date",
                    WarningBannerColor.RED
                )
                Spacer(Modifier.height(16.dp))
                WarningBanner(
                    false,
                    "WARNING! When transferring to Current Interest Account",
                    "You can transfer your asset from any Interest Account to the Current Interest Account at any time.\n" +
                            "For example, if you open a Weekly Interest Account on 10th March and fund it with 10,000 USDT, on 17th March at 14:00 UTC you will receive Accrued Interest based on Account Balance of 10,000 USDT.\n" +
                            "In another case, if during the compounding period, you transfer 2,000 USDT from the Weekly Account before the Interest Payout Date, the Accrued Interest will be recalculated. The Payout Date will not be changed and will remain as 17th March but Accrued Interest for the entire compounding period will be based on the Account Balance of 8,000 USDT.\n" +
                            "Please note that if you choose to transfer funds to the Current Interest Account in full on the Payout Date but before 14:00 UTC, you will lose the Accrued Interest. On the Payout Date, transfer funds in full only after 14:00 UTC once the Accrued Interest has been added to your Account Balance. If the Account Balance after the transfer to the Current Account becomes zero, then this account is automatically closed.",
                    WarningBannerColor.BLUE
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onCancel()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onTransfer(
                                selectedFromPlan,
                                selectedToPlan!!,
                                amount.value!!.asBigNum
                            )
                        },
                        enabled = selectedToPlan != null && (amount.value?.asBigNum
                            ?.isValidAmount(selectedFromPlan.balanceAvailable, BigNum.ZERO) ?: false)
                    ) {
                        Text("Transfer")
                    }
                }
            }
        }
    }
}

@Composable
fun AmountInputTextHeader(
    title: String,
    coinName: String,
    coinPrecision: Int,
    balanceAvailable: BigNum,
    info: String?,
    enabled: Boolean = true,
    onMaxAmountSelected: (BigNum) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                Color(0xFFa19ead),
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.clickable(
                    enabled = enabled,
                    onClick = { onMaxAmountSelected(balanceAvailable) }
                ),
                text = formatCrypto(
                    balanceAvailable,
                    coinName,
                    coinPrecision
                ) + " available",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            info?.let {
                Spacer(Modifier.width(8.dp))
                InformationTooltip(info)
            }
        }
    }
}

@Composable
fun AmountInputText(
    amount: String?,
    coin: Coin,
    minimumAmount: BigNum,
    maximumAmount: BigNum,
    trailingText: String,
    enabled: Boolean = true,
    onAmountChanged: (String?) -> Unit
) {
    val isInvalidAmount = !(amount?.asBigNum?.isValidAmount(maximumAmount, minimumAmount) ?: true)
    val isFocused = remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().onFocusEvent(
            {
                isFocused.value = it.isFocused
            }
        ),
        value = amount ?: "",
        onValueChange = {
            val newValue = it.replace(",", ".").replace(" ", "").replace("-", "")
            if ((newValue.length == 1 && newValue.first() == '.') ||
                (newValue.count { it == '.' } > 1)
            ) return@OutlinedTextField

            onAmountChanged(newValue.takeIf { it.isNotEmpty() })
        },
        isError = isInvalidAmount,
        supportingText = {
            if (isInvalidAmount) {
                if ((amount.asBigNum) < minimumAmount) {
                    Text("The amount must be at least $minimumAmount ${coin.binanceTicker}")
                } else {
                    Text("The amount cannot exceed $maximumAmount ${coin.binanceTicker}")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors().copy(
            unfocusedIndicatorColor = Color(0xFFa19ead)
        ),
        label = {
            if (amount == null && !isFocused.value) Text(
                formatCrypto(
                    BigNum.ZERO,
                    "",
                    coin.precision
                )
            )
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Number
        ),
        trailingIcon = {
            Text(text = trailingText, modifier = Modifier.padding(horizontal = 8.dp))
        },
        enabled = enabled
    )
}

fun BigNum.isValidAmount(maximumAmount: BigNum, minimumAmount: BigNum): Boolean =
    this > BigNum.ZERO && this >= minimumAmount && this <= maximumAmount

@Composable
fun AccountSelectionDropDownMenu(
    label: String,
    coinName: String,
    coinPrecision: Int,
    selectedPlan: DepositPlan?,
    availablePlans: Collection<DepositPlan>,
    enabled: Boolean = true,
    onPlanSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium.copy(
            Color(0xFFa19ead),
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    MyExposedDropDownMenu(
        modifier = Modifier.border(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(8.dp)
        ),
        expanded = expanded,
        enabled = enabled,
        buttonContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedPlan == null) {
                    Text(
                        text = "Select Account",
                        style = MaterialTheme.typography.titleMedium.copy(
                            Color(0xFFa19ead),
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Column {
                        Text(
                            text = selectedPlan.dropDownLabel,
                            style = MaterialTheme.typography.bodySmall.copy(
                                Color(0xFFa19ead),
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatCrypto(
                                selectedPlan.balance,
                                coinName,
                                coinPrecision
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        },
        items = {
            availablePlans.forEach {
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = it.dropDownLabel,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    Color(0xFFa19ead),
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = formatCrypto(it.balance, coinName, coinPrecision),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    onClick = {
                        expanded.value = false
                        onPlanSelected(it.id)
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun TransferScreenPreview() {
    CoinDepoTheme {
        TransferScreenContent(
            Coin(
                coinId = 16,
                coinName = "USDT_BSC",
                logoUrl = "https://static.coindepo.com/icons/usdt_bsc.svg",
                production = true,
                precision = 8,
                limit24h = 99999999999.0.asBigNum,
                explorerUrl = "https://bscscan.com/tx/",
                withdrawLimit = 30.0.asBigNum,
                binanceTicker = "USDT",
                description = "USDT BEP20",
                descriptionTicker = "USDT BEP20 (USDT)",
                isStableCoin = true,
                network = "BSC",
                depName = "Tether USD (USDT) BEP-20",
                wthName = "BNB Smart Chain (BEP20)",
                isActive = true,
                isToken = false,
                apy = 0.0.asBigNum,
                apr = 18.0.asBigNum,
                fee = 0.0.asBigNum,
                privatePrice = 0.0.asBigNum,
                listingPrice = 0.0.asBigNum,
                privateDiscount = 0.0.asBigNum,
                coinBalance = null,
                depositPlans = listOf(
                    DepositPlan(
                        id = "0_145",
                        userAccountId = 0,
                        depositPlanId = 145,
                        accountTypeId = 1,
                        accountName = "Current Compound Interest Account",
                        walletName = "1",
                        openDate = "",
                        lastPayout = "Jan 01 2020",
                        payoutDate = "Sep 14 2025",
                        period = "",
                        apr = 12.0.asBigNum,
                        precision = 8,
                        balance = "0.00000000".asBigNum,
                        balanceCurrency = 0.0.asBigNum,
                        inTransit = 0.0.asBigNum,
                        balanceAvailable = "0.00000000".asBigNum,
                        summaryBalance = 0.0.asBigNum,
                        summaryBalanceCurrency = 0.0.asBigNum,
                        accruedInterest = 0.0.asBigNum,
                        accruedInterestCurrency = 0.0.asBigNum,
                        paidInterest = "0.00000000".asBigNum,
                        paidInterestCurrency = 0.0.asBigNum,
                        canDelete = false,
                        isAllowedToTransfer = true,
                        apy = 0.0.asBigNum,
                        pendingBonus = false,
                        bonusList = "",
                        isVisible = true
                    ),
                    DepositPlan(
                        id = "0_146",
                        userAccountId = 0,
                        depositPlanId = 146,
                        accountTypeId = 2,
                        accountName = "Weekly Compound Interest Account",
                        walletName = "1",
                        openDate = "",
                        lastPayout = "Jan 01 2020",
                        payoutDate = "Sep 14 2025",
                        period = "",
                        apr = 12.0.asBigNum,
                        precision = 8,
                        balance = "0.00000000".asBigNum,
                        balanceCurrency = 0.0.asBigNum,
                        inTransit = 0.0.asBigNum,
                        balanceAvailable = "0.00000000".asBigNum,
                        summaryBalance = 0.0.asBigNum,
                        summaryBalanceCurrency = 0.0.asBigNum,
                        accruedInterest = 0.0.asBigNum,
                        accruedInterestCurrency = 0.0.asBigNum,
                        paidInterest = "0.00000000".asBigNum,
                        paidInterestCurrency = 0.0.asBigNum,
                        canDelete = true,
                        isAllowedToTransfer = true,
                        apy = 0.0.asBigNum,
                        pendingBonus = false,
                        bonusList = "",
                        isVisible = true
                    )
                ),
                availableLoans = AvailableLoans(
                    listOf(),
                    "10.0"

                ),
                order = 0
            ),
            "0_145",
            { _, _, _ -> },
            {}
        )
    }
}

data class TransferData(
    val fromPlan: DepositPlan,
    val toPlan: DepositPlan,
    val amount: BigNum
)

@get:Composable
private val DepositPlan.dropDownLabel: String
    get() {
        val walletName = if (accountTypeId != 1) "#${walletName}" else ""
        return stringResource(accountTypeId.periodStringRes) + " Interest Account $walletName"
    }