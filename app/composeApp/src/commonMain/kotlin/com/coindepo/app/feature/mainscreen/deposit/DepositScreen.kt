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

package com.coindepo.app.feature.mainscreen.deposit

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.coin_deposit
import coindepo.app.composeapp.generated.resources.current_interest_account_balance
import coindepo.app.composeapp.generated.resources.deposit_address
import coindepo.app.composeapp.generated.resources.deposit_network_warning_part_1
import coindepo.app.composeapp.generated.resources.deposit_network_warning_part_2
import coindepo.app.composeapp.generated.resources.deposit_note_part_1
import coindepo.app.composeapp.generated.resources.deposit_note_part_2
import coindepo.app.composeapp.generated.resources.deposit_note_part_3
import coindepo.app.composeapp.generated.resources.deposit_rules
import coindepo.app.composeapp.generated.resources.tag_memo
import coindepo.app.composeapp.generated.resources.tag_memo_warning
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.LoadingSpinner
import com.coindepo.app.feature.common.WarningBanner
import com.coindepo.app.feature.common.WarningBannerColor
import com.coindepo.app.feature.common.formatCrypto
import com.coindepo.app.feature.mainscreen.AccountStatsViewModel
import com.coindepo.app.feature.mainscreen.CoinId
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.entities.stats.coin.supportsTag
import com.coindepo.domain.entities.utils.asBigNum
import com.coindepo.domain.entities.wallet.Wallet
import korlibs.bignumber.BigNum
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import qrgenerator.QRCodeImage

@Composable
fun DepositScreen(
    accountStatsViewModel: AccountStatsViewModel,
    walletDataViewModel: WalletDataViewModel,
    coinId: CoinId
) {
    val coin = accountStatsViewModel.coins.collectAsState().value.find { it.coinId == coinId }!!
    LaunchedEffect(Unit) {
        walletDataViewModel.getWalletData(coin.coinName)
    }

    DepositScreenContent(
        coin,
        walletDataViewModel.walletData.collectAsState().value
    )
}

@Composable
fun DepositScreenContent(
    coin: Coin,
    walletData: Wallet?,
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
                Text(
                    text = stringResource(Res.string.coin_deposit, coin.description),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.current_interest_account_balance),
                    style = MaterialTheme.typography.titleMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = coin.logoUrl,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${formatCrypto(coin.depositPlans.find { it.accountTypeId == 1 }?.balance ?: BigNum.ZERO, coin.binanceTicker, coin.precision)}*", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append(stringResource(Res.string.deposit_note_part_1, coin.binanceTicker))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(Res.string.deposit_note_part_2))
                        }
                        append(stringResource(Res.string.deposit_note_part_3))
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Medium),
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CoinDepoElevatedCard(
                        modifier = Modifier.size(200.dp)
                    ) {
                        if (walletData == null) {
                            LoadingSpinner(
                                backgroundColor = MaterialTheme.colorScheme.background,
                                spinnerColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            QRCodeImage(
                                url = walletData.address,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                if (coin.supportsTag || !walletData?.tag.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))

                    WarningBanner(
                        false,
                        stringResource(Res.string.tag_memo_warning, coin.binanceTicker),
                        null,
                        WarningBannerColor.RED
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = walletData?.address ?: "",
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    label = { Text(stringResource(Res.string.deposit_address)) }
                )

                if (coin.supportsTag || !walletData?.tag.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = walletData?.tag ?: "",
                        onValueChange = {},
                        readOnly = true,
                        shape = RoundedCornerShape(16.dp),
                        label = { Text(stringResource(Res.string.tag_memo)) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = { }
                ) {
                    Text(stringResource(Res.string.deposit_rules))
                    Spacer(Modifier.width(8.dp))
                    Icon(modifier = Modifier.size(16.dp), imageVector = Icons.Filled.Info, contentDescription = "")
                }

                WarningBanner(
                    true,
                    stringResource(Res.string.deposit_network_warning_part_1, coin.depName, coin.wthName),
                    stringResource(Res.string.deposit_network_warning_part_2),
                    WarningBannerColor.BLUE
                )
            }
        }
    }
}

@Preview
@Composable
fun DepositScreenPreview() {
    CoinDepoTheme {
        DepositScreenContent(
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
                    )
                ),
                availableLoans = AvailableLoans(
                    listOf(),
                    "10.0"

                ),
                order = 0
            ),
            null
        )

    }
}
