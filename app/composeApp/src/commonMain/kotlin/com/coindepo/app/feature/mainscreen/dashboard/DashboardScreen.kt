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

package com.coindepo.app.feature.mainscreen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.available_credit_limit
import coindepo.app.composeapp.generated.resources.available_to_withdraw
import coindepo.app.composeapp.generated.resources.available_to_withdraw_info
import coindepo.app.composeapp.generated.resources.available_to_withdraw_tokens_info
import coindepo.app.composeapp.generated.resources.balance
import coindepo.app.composeapp.generated.resources.buy
import coindepo.app.composeapp.generated.resources.credit_line_limit
import coindepo.app.composeapp.generated.resources.credit_line_limit_info
import coindepo.app.composeapp.generated.resources.crypto
import coindepo.app.composeapp.generated.resources.gold
import coindepo.app.composeapp.generated.resources.hide_empty
import coindepo.app.composeapp.generated.resources.interest_paid
import coindepo.app.composeapp.generated.resources.portfolio_balance
import coindepo.app.composeapp.generated.resources.portfolio_balance_info
import coindepo.app.composeapp.generated.resources.price_24h_change
import coindepo.app.composeapp.generated.resources.show_empty
import coindepo.app.composeapp.generated.resources.stablecoins
import coindepo.app.composeapp.generated.resources.tokens
import coindepo.app.composeapp.generated.resources.tokens_portfolio_balance
import coindepo.app.composeapp.generated.resources.tokens_portfolio_balance_info
import coindepo.app.composeapp.generated.resources.total_interest_accrued
import coindepo.app.composeapp.generated.resources.total_interest_accrued_info
import coindepo.app.composeapp.generated.resources.total_interest_paid
import coindepo.app.composeapp.generated.resources.total_interest_paid_info
import coindepo.app.composeapp.generated.resources.total_rewards_accrued
import coindepo.app.composeapp.generated.resources.total_rewards_accrued_info
import coindepo.app.composeapp.generated.resources.total_rewards_paid
import coindepo.app.composeapp.generated.resources.total_rewards_paid_info
import coindepo.app.composeapp.generated.resources.total_value_of_your_assets
import com.coindepo.app.feature.common.BalanceIndicator
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.formatCrypto
import com.coindepo.app.feature.common.formatCurrency
import com.coindepo.app.feature.mainscreen.AccountStatsViewModel
import com.coindepo.app.feature.mainscreen.CoinId
import com.coindepo.app.feature.mainscreen.DepositPlanId
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.stats.balance.AccountBalance
import com.coindepo.domain.entities.stats.balance.BorrowBalance
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.utils.asBigNum
import korlibs.bignumber.BigNum
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    accountStatsViewModel: AccountStatsViewModel,
    isRefreshing: Boolean,
    maskBalances: Boolean,
    goToDepositScreen: (CoinId) -> Unit,
    goToWithdrawScreen: (CoinId, DepositPlanId?) -> Unit,
    goToEarnScreen: (CoinId) -> Unit,
    goToBuyToken: (CoinId) -> Unit,
    goToTokensScreen: (CoinId) -> Unit,
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
        DashboardScreenContent(
            accountStatsViewModel.accountBalance.collectAsState().value,
            accountStatsViewModel.borrowBalance.collectAsState().value,
            accountStatsViewModel.coins.collectAsState().value,
            accountStatsViewModel.tokens.collectAsState().value,
            accountStatsViewModel.currency.collectAsState().value,
            maskBalances,
            goToDepositScreen,
            goToWithdrawScreen,
            goToEarnScreen,
            goToBuyToken,
            goToTokensScreen
        )
    }
}

@Composable
fun DashboardScreenContent(
    accountBalance: AccountBalance?,
    borrowBalance: BorrowBalance?,
    coins: Collection<Coin>,
    tokens: Collection<Coin>,
    currency: Currency,
    maskBalances: Boolean,
    goToDepositScreen: (CoinId) -> Unit,
    goToWithdrawScreen: (CoinId, DepositPlanId?) -> Unit,
    goToEarnScreen: (CoinId) -> Unit,
    goToBuyToken: (CoinId) -> Unit,
    goToTokensScreen: (CoinId) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .verticalScroll(state = scrollState)
    ) {
        Spacer(Modifier.height(16.dp))
        PortfolioBalancesCard(accountBalance, currency, maskBalances)
        Spacer(Modifier.height(16.dp))
        if (tokens.isNotEmpty()) {
            TokenBalancesCard(accountBalance, currency, maskBalances)
            Spacer(Modifier.height(16.dp))
        }
        CreditLineCard(borrowBalance, currency, maskBalances)
        Spacer(Modifier.height(16.dp))
        if (tokens.isNotEmpty()) {
            CoinBalancesSection(stringResource(Res.string.tokens), tokens, currency, maskBalances, goToDepositScreen, goToWithdrawScreen, goToEarnScreen, goToBuyToken, goToTokensScreen)
            Spacer(Modifier.height(16.dp))
        }
        CoinBalancesSection(stringResource(Res.string.stablecoins), coins.filter { it.isStableCoin && !it.description.lowercase().contains("gold") }, currency, maskBalances, goToDepositScreen, goToWithdrawScreen, goToEarnScreen, goToBuyToken, goToTokensScreen)
        Spacer(Modifier.height(16.dp))
        CoinBalancesSection(stringResource(Res.string.gold), coins.filter { it.description.lowercase().contains("gold") }, currency, maskBalances, goToDepositScreen, goToWithdrawScreen, goToEarnScreen, goToBuyToken, goToTokensScreen)
        Spacer(Modifier.height(16.dp))
        CoinBalancesSection(stringResource(Res.string.crypto), coins.filter { !it.isStableCoin && !it.description.lowercase().contains("gold") }, currency, maskBalances, goToDepositScreen, goToWithdrawScreen, goToEarnScreen, goToBuyToken, goToTokensScreen)
        Spacer(Modifier.height(16.dp))
        TotalValueCard(accountBalance, currency)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun PortfolioBalancesCard(
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
            BalanceIndicator(
                title = stringResource(Res.string.portfolio_balance),
                balance = accountBalance?.totalBalance ?: BigNum.ZERO,
                currency = currency,
                balanceTextColor = Color(0xff0db46e),
                info = stringResource(Res.string.portfolio_balance_info),
                maskBalance = maskBalances,
                big = true
            )
            BalanceIndicator(
                title = stringResource(Res.string.available_to_withdraw),
                balance = accountBalance?.totalAvailableBalance ?: BigNum.ZERO,
                currency = currency,
                balanceTextColor = Color(0xff1b8a5c),
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
fun TokenBalancesCard(
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
            BalanceIndicator(
                title = stringResource(Res.string.tokens_portfolio_balance),
                balance = accountBalance?.totalTokensBalanceCurrency ?: BigNum.ZERO,
                currency = currency,
                balanceTextColor = Color(0xff247bfa),
                info = stringResource(Res.string.tokens_portfolio_balance_info),
                maskBalance = maskBalances,
                big = true
            )
            BalanceIndicator(
                title = stringResource(Res.string.available_to_withdraw),
                balance = accountBalance?.totalTokensBalanceAvailable ?: BigNum.ZERO,
                currency = currency,
                balanceTextColor = Color(0xff0b3fc2),
                info = stringResource(Res.string.available_to_withdraw_tokens_info),
                maskBalance = maskBalances,
            )
            BalanceIndicator(
                title = stringResource(Res.string.total_rewards_paid),
                balance = accountBalance?.totalTokensInterestPaidCurrency ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.total_rewards_paid_info),
                maskBalance = maskBalances,
            )
            BalanceIndicator(
                title = stringResource(Res.string.total_rewards_accrued),
                balance = accountBalance?.totalTokensAccruedInterestCurrency ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.total_rewards_accrued_info),
                maskBalance = maskBalances,
            )
        }
    }
}

@Composable
fun CreditLineCard(
    borrowBalance: BorrowBalance?,
    currency: Currency,
    maskBalances: Boolean
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(16.dp)
        ) {
            BalanceIndicator(
                title = stringResource(Res.string.credit_line_limit),
                balance = borrowBalance?.creditLineLimitCurrency ?: BigNum.ZERO,
                currency = currency,
                info = stringResource(Res.string.credit_line_limit_info),
                maskBalance = maskBalances,
            )
            BalanceIndicator(
                title = stringResource(Res.string.available_credit_limit),
                balance = borrowBalance?.availableCreditLimitCurrency ?: BigNum.ZERO,
                currency = currency,
                balanceTextColor = Color(0xff1b8a5c),
                info = stringResource(Res.string.available_to_withdraw_info),
                maskBalance = maskBalances,
            )
        }
    }
}

@Composable
fun CoinBalancesSection(
    title: String,
    coinsList: Collection<Coin>,
    currency: Currency,
    maskBalances: Boolean,
    goToDepositScreen: (CoinId) -> Unit,
    goToWithdrawScreen: (CoinId, DepositPlanId?) -> Unit,
    goToEarnScreen: (CoinId) -> Unit,
    goToBuyToken: (CoinId) -> Unit,
    goToTokensScreen: (CoinId) -> Unit
) {
    val expanded = rememberSaveable { mutableStateOf(true) }
    val hideEmptyBalances = rememberSaveable { mutableStateOf(false) }

    Column {
        CoinDepoElevatedCard(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
                )
                Row {
                    TextButton(
                        onClick = {
                            hideEmptyBalances.value = !hideEmptyBalances.value
                        }
                    ) {
                        Text(
                            if (hideEmptyBalances.value) {
                                stringResource(Res.string.show_empty)
                            } else {
                                stringResource(Res.string.hide_empty)
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            expanded.value = !expanded.value
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    CircleShape
                                ).padding(8.dp),
                            imageVector = if (expanded.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = ""
                        )
                    }
                }
            }
        }

        with(
            when {
                !expanded.value -> emptyList()
                hideEmptyBalances.value -> coinsList.filter { it.coinBalance?.balance?.compareTo(BigNum.ZERO) != 0 }.sortedBy { it.coinBalance?.displayOrder }
                else -> coinsList.sortedBy { it.coinBalance?.displayOrder }
            }
        ) {
            forEach {
                if (it == first()) Spacer(Modifier.height(8.dp))
                CoinBalanceCard(
                    it,
                    currency,
                    maskBalances,
                    goToDepositScreen,
                    goToWithdrawScreen,
                    goToEarnScreen,
                    goToBuyToken,
                    goToTokensScreen
                )
                if (it != last()) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CoinBalanceCard(
    coin: Coin,
    currency: Currency,
    maskBalance: Boolean,
    goToDepositScreen: (CoinId) -> Unit,
    goToWithdrawScreen: (CoinId, DepositPlanId?) -> Unit,
    goToEarnScreen: (CoinId) -> Unit,
    goToBuyToken: (CoinId) -> Unit,
    goToTokensScreen: (CoinId) -> Unit
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier.size(32.dp),
                        model = coin.logoUrl,
                        contentDescription = null,
                    )
                    Text(
                        text = coin.description,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (coin.isToken) {
                    TokenDetailsCardButtons(coin.coinId, goToBuyToken, goToTokensScreen)
                } else {
                    CoinDetailsCardButtons(coin.coinId, goToDepositScreen, goToWithdrawScreen, goToEarnScreen)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(text = stringResource(Res.string.balance), style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
            Text(text = formatCrypto(coin.coinBalance?.balance ?: BigNum.ZERO, coin.binanceTicker, coin.precision, maskBalance), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            Text(text = formatCurrency(coin.coinBalance?.balanceCurrency ?: BigNum.ZERO, currency, maskBalance), style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))

            Spacer(Modifier.height(8.dp))

            Text(text = stringResource(Res.string.price_24h_change), style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
            Text(text = formatCurrency(coin.coinBalance?.binance24hCurrency ?: BigNum.ZERO, currency), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            Text(text = "${coin.coinBalance?.binance24h}%", style = MaterialTheme.typography.bodyMedium.copy(color = Color(
                if ((coin.coinBalance?.binance24h ?: BigNum.ZERO) < BigNum.ZERO) 0xFFeb0000 else 0xFF0db46e
            ), fontWeight = FontWeight.Medium))

            Spacer(Modifier.height(8.dp))

            Text(text = stringResource(Res.string.interest_paid), style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))
            Text(text = formatCrypto(coin.coinBalance?.interest ?: BigNum.ZERO, coin.binanceTicker, coin.precision, maskBalance), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
            Text(text = formatCurrency(coin.coinBalance?.interestCurrency ?: BigNum.ZERO, currency, maskBalance), style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFa19ead), fontWeight = FontWeight.Medium))

            Spacer(Modifier.height(8.dp))

            Text(modifier = Modifier.fillMaxWidth().background(Color(0xFFdfffec), shape = CircleShape), text = coin.aprRangeLabel, style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1e7e34), fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center) // TODO: CHECK OUT THE COLOR HERE
        }
    }
}

@Composable
fun TokenDetailsCardButtons(
    coinId: Int,
    goToBuyToken: (Int) -> Unit,
    goToTokensScreen: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { goToBuyToken(coinId) }
        ) {
            Text(text = stringResource(Res.string.buy))
        }
        IconButton(
            onClick = {
                goToTokensScreen(coinId)
            }
        ) {
            Icon(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    ).padding(8.dp),
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun CoinDetailsCardButtons(
    coinId: Int,
    goToDepositScreen: (CoinId) -> Unit,
    goToWithdrawScreen: (CoinId, DepositPlanId?) -> Unit,
    goToEarnScreen: (CoinId) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                goToDepositScreen(coinId)
            }
        ) {
            Icon(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    ).padding(8.dp),
                imageVector = Icons.Outlined.FileDownload,
                contentDescription = ""
            )
        }
        IconButton(
            onClick = {
                goToWithdrawScreen(coinId, null)
            }
        ) {
            Icon(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    ).padding(8.dp),
                imageVector = Icons.Outlined.FileUpload,
                contentDescription = ""
            )
        }
        IconButton(
            onClick = {
                goToEarnScreen(coinId)
            }
        ) {
            Icon(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    ).padding(8.dp),
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun TotalValueCard(
    accountBalance: AccountBalance?,
    currency: Currency
) {
    CoinDepoElevatedCard(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(Res.string.total_value_of_your_assets), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium))
            Text(text = formatCurrency(
                (accountBalance?.totalBalance ?: BigNum.ZERO) + (accountBalance?.totalTokensBalanceCurrency ?: BigNum.ZERO),
                currency
            ), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}


@Preview
@Composable
fun DashboardScreenContentPreview() {
    CoinDepoTheme(
        darkTheme = true
    ) {
        DashboardScreenContent(
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
                BorrowBalance(
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    listOf(),
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
                        depositPlans = listOf(),
                        availableLoans = AvailableLoans(
                            listOf(),
                            "10.0"

                        ),
                        order = 0
                    )
                ),
                listOf(),
            currency = Currency.USD,
            false,
            {},
            {_,_ -> },
            {},
            {},
            {}
        )
    }
}

@Preview
@Composable
fun DashboardScreenContentLightPreview() {
    CoinDepoTheme(
        darkTheme = false
    ) {
        DashboardScreenContent(
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
                BorrowBalance(
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    "10.0".asBigNum,
                    listOf(),
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
                        depositPlans = listOf(),
                        availableLoans = AvailableLoans(
                            listOf(),
                            "10.0"

                        ),
                        order = 0
                    )
                ),
                listOf(),
            currency = Currency.USD,
            false,
            {},
            {_,_ -> },
            {},
            {},
            {}
        )
    }
}

private val Coin.aprRangeLabel: String
    get() = if (isToken) {
        "${depositPlans.maxByOrNull { it.apy }?.apy}%"
    } else {
        val minApr = depositPlans.minByOrNull { it.apr }?.apr.toString()
        val maxApr = depositPlans.maxByOrNull { it.apr }?.apr.toString()
        if (minApr == maxApr) {
            "$minApr%"
        } else {
            "$minApr% - $maxApr%"
        }
    }