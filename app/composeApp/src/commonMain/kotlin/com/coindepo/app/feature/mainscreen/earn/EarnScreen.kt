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

package com.coindepo.app.feature.mainscreen.earn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.accrued_interest
import coindepo.app.composeapp.generated.resources.accrued_interest_info
import coindepo.app.composeapp.generated.resources.add_new_account
import coindepo.app.composeapp.generated.resources.annual
import coindepo.app.composeapp.generated.resources.apr_placeholder
import coindepo.app.composeapp.generated.resources.available_to_withdraw
import coindepo.app.composeapp.generated.resources.available_to_withdraw_info
import coindepo.app.composeapp.generated.resources.balance
import coindepo.app.composeapp.generated.resources.cant_close_account_description
import coindepo.app.composeapp.generated.resources.close_account
import coindepo.app.composeapp.generated.resources.close_account_description
import coindepo.app.composeapp.generated.resources.compound_interest_payout_frequency
import coindepo.app.composeapp.generated.resources.compund_interest_pediod
import coindepo.app.composeapp.generated.resources.daily
import coindepo.app.composeapp.generated.resources.i_understand
import coindepo.app.composeapp.generated.resources.interest_account_rules
import coindepo.app.composeapp.generated.resources.interest_accounts
import coindepo.app.composeapp.generated.resources.interest_paid
import coindepo.app.composeapp.generated.resources.interest_paid_info
import coindepo.app.composeapp.generated.resources.interest_payout_date
import coindepo.app.composeapp.generated.resources.interest_payout_date_info
import coindepo.app.composeapp.generated.resources.monthly
import coindepo.app.composeapp.generated.resources.no
import coindepo.app.composeapp.generated.resources.period_placeholder
import coindepo.app.composeapp.generated.resources.portfolio_balance
import coindepo.app.composeapp.generated.resources.portfolio_balance_info
import coindepo.app.composeapp.generated.resources.quarterly
import coindepo.app.composeapp.generated.resources.select_interest_account_type
import coindepo.app.composeapp.generated.resources.semi_annual
import coindepo.app.composeapp.generated.resources.total_interest_accrued
import coindepo.app.composeapp.generated.resources.total_interest_accrued_info
import coindepo.app.composeapp.generated.resources.total_interest_paid
import coindepo.app.composeapp.generated.resources.total_interest_paid_info
import coindepo.app.composeapp.generated.resources.weekly
import coindepo.app.composeapp.generated.resources.yes
import com.coindepo.app.browser.openUrlInBrowser
import com.coindepo.app.feature.common.BalanceIndicator
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.ExposedDropDownMenu
import com.coindepo.app.feature.common.InformationTooltip
import com.coindepo.app.feature.common.formatCrypto
import com.coindepo.app.feature.common.formatCurrency
import com.coindepo.app.feature.mainscreen.AccountStatsViewModel
import com.coindepo.app.feature.mainscreen.CoinId
import com.coindepo.app.feature.mainscreen.DepositPlanId
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.entities.utils.asBigNum
import korlibs.bignumber.BigNum
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarnScreen(
    accountStatsViewModel: AccountStatsViewModel,
    initialCoinId: Int?,
    isRefreshing: Boolean,
    maskBalances: Boolean,
    goToDepositScreen: (CoinId) -> Unit,
    goToWithdrawScreen: (CoinId, DepositPlanId?) -> Unit,
    goToTransferScreen: (String) -> Unit,
    onRefresh: () -> Unit
) {
    LaunchedEffect(Unit) {
        accountStatsViewModel.fetchAccountStats()
    }

    val state: PullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = state,
        onRefresh = onRefresh,
        isRefreshing = isRefreshing,
        modifier = Modifier.fillMaxSize()
    ) {
        val closeAccountDialog = remember { mutableStateOf<String?>(null) }
        val coins = accountStatsViewModel.coins.collectAsState().value
        EarnScreenContent(
            initialCoinId,
            accountStatsViewModel.accountBalance.collectAsState().value,
            coins,
            accountStatsViewModel.currency.collectAsState().value,
            maskBalances
        ) {
            when(it) {
                is SetVisibility -> {
                    if (it.visible) {
                        accountStatsViewModel.setDepositPlanVisible(it.visible, it.depositPlan.depositPlanId)
                    } else {
                        closeAccountDialog.value = it.depositPlan.id
                    }
                }
                is Deposit -> { goToDepositScreen(it.coinId) }
                is Transfer -> { goToTransferScreen(it.depositPlan.id) }
                is Withdraw -> { goToWithdrawScreen(it.coinId, it.depositPlan.id) }
            }
        }

        if (closeAccountDialog.value != null) {
            val plan = coins.first { it.depositPlans.any { it.id == closeAccountDialog.value } }.depositPlans.first { it.id == closeAccountDialog.value }
            if (plan.balance.compareTo(BigNum.ZERO) == 0) {
                CloseAccountConfirmationDialog(
                    plan,
                    onConfirm = {
                        accountStatsViewModel.setDepositPlanVisible(
                            false,
                            it
                        )
                        closeAccountDialog.value = null
                    },
                    onCancel = {
                        closeAccountDialog.value = null
                    }
                )
            } else {
                UnableToCloseAccountDialog { closeAccountDialog.value = null }
            }
        }
    }
}

@Composable
fun CloseAccountConfirmationDialog(
    plan: DepositPlan,
    onConfirm: (Int) -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(modifier = Modifier.fillMaxWidth(), text = stringResource(Res.string.close_account), textAlign = TextAlign.Center) },
        text = { Text(stringResource(Res.string.close_account_description), textAlign = TextAlign.Center) },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    onClick = onCancel
                ) {
                    Text(stringResource(Res.string.no))
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onConfirm(plan.depositPlanId) }
                ) {
                    Text(stringResource(Res.string.yes))
                }
            }
        }
    )
}

@Composable
fun UnableToCloseAccountDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(modifier = Modifier.fillMaxWidth(), text = stringResource(Res.string.close_account), textAlign = TextAlign.Center) },
        text = { Text(stringResource(Res.string.cant_close_account_description), textAlign = TextAlign.Center) },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismissRequest
            ) {
                Text(stringResource(Res.string.i_understand))
            }
        }
    )
}

@Composable
fun EarnScreenContent(
    initialCoinId: Int?,
    accountBalance: AccountBalance?,
    coins: Collection<Coin>,
    currency: Currency,
    maskBalances: Boolean,
    onPlanActions: (PlanActions) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .verticalScroll(state = scrollState)
    ) {
        Spacer(Modifier.height(16.dp))
        EarnBalanceCard(accountBalance, currency, maskBalances)
        Spacer(Modifier.height(16.dp))
        CoinPlansSection(
            initialCoinId,
            coins,
            currency,
            maskBalances,
            onPlanActions
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun EarnBalanceCard(
    accountBalance: AccountBalance?,
    currency: Currency,
    maskBalances: Boolean
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp)
        ) {
            Text(stringResource(Res.string.interest_accounts), style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(4.dp))
            BalanceIndicator(
                title = stringResource(Res.string.portfolio_balance),
                balance = accountBalance?.totalBalance ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.portfolio_balance_info),
                maskBalance = maskBalances,
                big = true
            )
            BalanceIndicator(
                title = stringResource(Res.string.available_to_withdraw),
                balance = accountBalance?.totalAvailableBalance ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.available_to_withdraw_info),
                maskBalance = maskBalances,
            )
            BalanceIndicator(
                title = stringResource(Res.string.total_interest_paid),
                balance = accountBalance?.totalInterestPaidCurrency ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.total_interest_paid_info),
                maskBalance = maskBalances,
            )
            BalanceIndicator(
                title = stringResource(Res.string.total_interest_accrued),
                balance = accountBalance?.totalAccruedInterestCurrency ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.total_interest_accrued_info),
                maskBalance = maskBalances,
            )
        }
    }
}

@Composable
fun CoinPlansSection(
    initialCoinId: Int?,
    coins: Collection<Coin>,
    currency: Currency,
    maskBalances: Boolean,
    onPlanAction: (PlanActions) -> Unit
) {
    if (coins.isNotEmpty()) {
        val selectedCoinId = rememberSaveable { mutableStateOf(initialCoinId ?: coins.first().coinId) }
        val coin = coins.first { it.coinId == selectedCoinId.value }
        CoinSelector(coins, coin, selectedCoinId)
        Spacer(Modifier.height(8.dp))
        coin.depositPlans.filter { it.isVisible }.forEach {
            CoinPlanCard(coin, it, currency, maskBalances, onPlanAction)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinSelector(
    coinList: Collection<Coin>,
    selectedCoin: Coin,
    selectedCoinId: MutableState<Int>
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        val expanded = remember { mutableStateOf(false) }
        ExposedDropDownMenu(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
            expanded = expanded,
            buttonContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = selectedCoin.logoUrl,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = selectedCoin.descriptionTicker, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                    }
                    Icon(imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            },
            items = {
                coinList.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            expanded.value = false
                            selectedCoinId.value = option.coinId
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = option.logoUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option.descriptionTicker, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun CoinPlanCard(
    coin: Coin,
    depositPlan: DepositPlan,
    currency: Currency,
    maskBalances: Boolean,
    onPlanAction: (PlanActions) -> Unit
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp)
        ) {
            val expanded = remember { mutableStateOf(true) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val walletName = if (depositPlan.accountTypeId != 1) "#${depositPlan.walletName}" else ""
                Text("${depositPlan.accountName} $walletName", style = MaterialTheme.typography.titleMedium)
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { expanded.value = !expanded.value },
                    imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                    contentDescription = ""
                )
            }

            Spacer(Modifier.height(16.dp))

            DepositPlanButtons(depositPlan, coin.coinId, onPlanAction)

            Spacer(Modifier.height(16.dp))

            DepositPlanBalanceIndicator(
                title = stringResource(Res.string.balance),
                balance = depositPlan.balance,
                balanceCurrency = depositPlan.balanceCurrency,
                coinName = coin.binanceTicker,
                precision = coin.precision,
                currency = currency,
                maskBalance = maskBalances,
                largeBalance = true
            )

            if (depositPlan.accountTypeId == 1 && coin.depositPlans.any { !it.isVisible }) {
                Spacer(Modifier.height(16.dp))
                AddNewAccountDropDown(coin.depositPlans.filter { !it.isVisible }) {
                    onPlanAction(SetVisibility(it, true))
                }
            }

            AnimatedVisibility(expanded.value) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    DepositPlanBalanceIndicator(
                        title = stringResource(Res.string.interest_paid),
                        balance = depositPlan.paidInterest,
                        balanceCurrency = depositPlan.paidInterestCurrency,
                        coinName = coin.binanceTicker,
                        precision = coin.precision,
                        info = stringResource(Res.string.interest_paid_info),
                        currency = currency,
                        maskBalance = maskBalances,
                        largeBalance = false
                    )

                    Spacer(Modifier.height(16.dp))

                    HorizontalDivider()

                    Spacer(Modifier.height(16.dp))

                    DepositPlanBalanceIndicator(
                        title = stringResource(Res.string.accrued_interest),
                        balance = depositPlan.accruedInterest,
                        balanceCurrency = depositPlan.accruedInterestCurrency,
                        coinName = coin.binanceTicker,
                        precision = coin.precision,
                        info = stringResource(Res.string.accrued_interest_info),
                        currency = currency,
                        maskBalance = maskBalances,
                        largeBalance = false
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(Res.string.interest_payout_date), style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
                        Spacer(Modifier.width(8.dp))
                        InformationTooltip(stringResource(Res.string.interest_payout_date_info))
                    }
                    Text(text = depositPlan.payoutDate, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.background(Color(0xff0db46e), RoundedCornerShape(8.dp)).padding(12.dp),
                            text = "${depositPlan.apr}%",
                            style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Res.string.compund_interest_pediod, depositPlan.accountTypeId.periodDays), style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
                    }
                    TextButton(
                        onClick = {
                            openUrlInBrowser("https://coindepo.com/legal/earn-terms?_gl=1*1cv7r58*_gcl_au*MTY1MjM1MjE5Mi4xNzU2MTMwOTMz*_ga*OTUzNjY3NzI4LjE3NTYxMzA5MzQ.*_ga_D9J1SREE8G*czE3NTc3NzcxNjgkbzQ5JGcxJHQxNzU3Nzc3MTc1JGo1MyRsMCRoMTQ0MjA0MDQ4OQ..#pOpenCD")
                        }
                    ) {
                        Text(stringResource(Res.string.interest_account_rules))
                        Spacer(Modifier.width(8.dp))
                        Icon(modifier = Modifier.size(16.dp), imageVector = Icons.Filled.Info, contentDescription = "")
                    }
                }
            }
        }
    }
}

@Composable
fun AddNewAccountDropDown(
    depositPlans: Collection<DepositPlan>,
    setDepositPlanVisible: (DepositPlan) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    Text(stringResource(Res.string.add_new_account), style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
    Spacer(Modifier.height(8.dp))
    ExposedDropDownMenu(
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(8.dp)),
        expanded = expanded,
        buttonContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(Res.string.select_interest_account_type), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium), color = Color(0xFFa19ead))
                Icon(imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        },
        items = {
            depositPlans.forEachIndexed { index, plan ->
                DropdownMenuItem(
                    text = {
                        Column {
                            val period = stringResource(plan.accountTypeId.periodStringRes).uppercase()
                            Text(text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                                    append(stringResource(Res.string.period_placeholder, period))
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(stringResource(Res.string.apr_placeholder, plan.apr))
                                }
                            })
                            Text(stringResource(Res.string.compound_interest_payout_frequency, plan.accountTypeId.periodDays))
                            if (index != depositPlans.lastIndexOf(depositPlans.last())) HorizontalDivider()
                        }
                    },
                    onClick = {
                        expanded.value = false
                        setDepositPlanVisible(plan)
                    }
                )
            }
        }
    )
}

val Int.periodDays: Int
    get() = when(this) {
        1 -> 1
        2 -> 7
        3 -> 30
        4 -> 90
        5 -> 180
        6 -> 365
        else -> -1
    }

val Int.periodStringRes: StringResource
    get() = when(this) {
        1 -> Res.string.daily
        2 -> Res.string.weekly
        3 -> Res.string.monthly
        4 -> Res.string.quarterly
        5 -> Res.string.semi_annual
        6 -> Res.string.annual
        else -> throw IllegalArgumentException()
    }

@Composable
fun DepositPlanBalanceIndicator(
    title: String,
    balance: BigNum,
    balanceCurrency: BigNum,
    coinName: String,
    precision: Int,
    info: String? = null,
    currency: Currency,
    maskBalance: Boolean,
    largeBalance: Boolean
) {
    val balanceTextStyle = if (largeBalance) {
        MaterialTheme.typography.titleLarge
    } else {
        MaterialTheme.typography.titleMedium
    }.copy(fontWeight = FontWeight.SemiBold)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
        info?.let {
            Spacer(Modifier.width(8.dp))
            InformationTooltip(info)
        }
    }
    Spacer(Modifier.height(8.dp))
    Text(text = formatCrypto(balance, coinName, precision, maskBalance), style = balanceTextStyle)
    Text(text = formatCurrency(balanceCurrency, currency, maskBalance), style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
}

@Composable
fun DepositPlanButtons(
    depositPlan: DepositPlan,
    coinId: Int,
    onPlanAction: (PlanActions) -> Unit
) {
    val options = mutableListOf<PlanActions>().also {
        if (depositPlan.accountTypeId == 1) it.add(Deposit(depositPlan, coinId))
        it.add(Withdraw(depositPlan, coinId))
        it.add(Transfer(depositPlan))
        if (depositPlan.accountTypeId != 1) it.add(SetVisibility(depositPlan, false))
    }

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, action ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                    baseShape = RoundedCornerShape(8.dp)
                ),
                onClick = {
                    onPlanAction(action)
                },
                selected = false,
                label = {
                    Icon(
                        imageVector = when(action) {
                            is Deposit -> Icons.Outlined.FileDownload
                            is SetVisibility -> Icons.Outlined.Clear
                            is Transfer -> Icons.Outlined.SyncAlt
                            is Withdraw -> Icons.Outlined.FileUpload
                        },
                        contentDescription = ""
                    )
                },
                colors = SegmentedButtonDefaults.colors().copy(
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    activeBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }
    }
}

sealed interface PlanActions {
    val depositPlan: DepositPlan
}

data class Deposit(
    override val depositPlan: DepositPlan,
    val coinId: Int
): PlanActions

data class Withdraw(
    override val depositPlan: DepositPlan,
    val coinId: Int
): PlanActions

data class Transfer(
    override val depositPlan: DepositPlan
): PlanActions

data class SetVisibility(
    override val depositPlan: DepositPlan,
    val visible: Boolean
): PlanActions

@Composable
fun DashboardScreenContentPreview(
    darkMode: Boolean
) {
    CoinDepoTheme(
        darkTheme = darkMode
    ) {
        EarnScreenContent(
            null,
                AccountBalance(
                    10.0.asBigNum,
                    10.0.asBigNum,
                    5583.74.asBigNum,
                    10.0.asBigNum,
                    10.0.asBigNum,
                    10.0.asBigNum,
                    10.0.asBigNum,
                    10.0.asBigNum
                ),
                listOf(
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
                                canDelete = false,
                                isAllowedToTransfer = true,
                                apy = 0.0.asBigNum,
                                pendingBonus = false,
                                bonusList = "",
                                isVisible = false
                            )
                        ),
                        availableLoans = AvailableLoans(
                            listOf(),
                            "10.0"

                        ),
                        order = 0
                    )
                ),
            currency = Currency.USD,
            true,
            {}
        )
    }
}

@Preview
@Composable
fun DashboardScreenContentLightPreview() {
    DashboardScreenContentPreview(false)
}

@Preview
@Composable
fun DashboardScreenContentDarkPreview() {
    DashboardScreenContentPreview(true)
}