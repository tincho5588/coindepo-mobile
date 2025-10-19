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

package com.coindepo.app.feature.mainscreen.withdraw

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.email_verification_code
import coindepo.app.composeapp.generated.resources.tag_memo
import coindepo.app.composeapp.generated.resources.twofa
import com.coindepo.app.feature.common.CoinDepoElevatedCard
import com.coindepo.app.feature.common.InformationTooltip
import com.coindepo.app.feature.common.WarningBanner
import com.coindepo.app.feature.common.WarningBannerColor
import com.coindepo.app.feature.common.formatCrypto
import com.coindepo.app.feature.login.ResendVerificationCodeState
import com.coindepo.app.feature.mainscreen.AccountStatsViewModel
import com.coindepo.app.feature.mainscreen.CoinId
import com.coindepo.app.feature.mainscreen.DepositPlanId
import com.coindepo.app.feature.mainscreen.transfer.AccountSelectionDropDownMenu
import com.coindepo.app.feature.mainscreen.transfer.AmountInputText
import com.coindepo.app.feature.mainscreen.transfer.AmountInputTextHeader
import com.coindepo.app.feature.mainscreen.transfer.isValidAmount
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.NoNetworkException
import com.coindepo.domain.entities.OtherErrorException
import com.coindepo.domain.entities.stats.coin.AccountType
import com.coindepo.domain.entities.stats.coin.AvailableLoans
import com.coindepo.domain.entities.stats.coin.Coin
import com.coindepo.domain.entities.stats.coin.DepositPlan
import com.coindepo.domain.entities.stats.coin.supportsTag
import com.coindepo.domain.entities.utils.asBigNum
import com.coindepo.domain.entities.withdraw.WithdrawFee
import com.coindepo.domain.entities.withdraw.WithdrawRequest
import korlibs.bignumber.BigNum
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WithdrawScreen(
    accountStatsViewModel: AccountStatsViewModel,
    withdrawViewModel: WithdrawViewModel,
    coinId: CoinId,
    depositPlanId: DepositPlanId?,
    showSnackBar: (String, SnackbarDuration) -> Unit,
    onCancelled: () -> Unit
) {
    val coin = accountStatsViewModel.coins.collectAsState().value.first { it.coinId == coinId }
    WithdrawScreenContent(
        coin,
        depositPlanId,
        withdrawViewModel.withdrawProcessState.collectAsState().value,
        withdrawViewModel.resendVerificationCodeState.collectAsState().value,
        showSnackBar,
        {
            withdrawViewModel.sendEmailVerificationCode()
        },
        {
            withdrawViewModel.resetWithdrawProcessState(it)
        },
        { depositPlan, address, tag, amount ->
            withdrawViewModel.getWithdrawFeesEstimate(
                coin, depositPlan, amount, address, tag
            )
        },
        { depositPlan, address, tag, amount, requestId ->
            withdrawViewModel.requestWithdraw(coin,depositPlan, amount, address, tag, requestId)
        },
        onCancelled,
        { emailVerificationCode, twoFACode ->
            withdrawViewModel.confirmWithdraw(emailVerificationCode, twoFACode)
        }
    )
}

@Composable
fun WithdrawScreenContent(
    coin: Coin,
    initialFromPlanId: DepositPlanId?,
    withdrawProcessState: WithdrawProcessState,
    resendVerificationCodeState: ResendVerificationCodeState?,
    showSnackBar: (String, SnackbarDuration) -> Unit,
    sendEmailVerificationCode: () -> Unit,
    resetWithdrawState: (WithdrawProcessState) -> Unit,
    onCalculateFeeEstimate: (DepositPlan, String, String?, BigNum) -> Unit,
    onRequestWithdraw: (DepositPlan, String, String?, BigNum, String) -> Unit,
    onCancelled: () -> Unit,
    onConfirm: (String, String?) -> Unit
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
                val selectedFromPlanId = remember { mutableStateOf(initialFromPlanId ?: coin.depositPlans.first().id) }
                val selectedFromPlan =
                    coin.depositPlans.first { it.id == selectedFromPlanId.value }
                val amount = remember { mutableStateOf<String?>(null) }
                val withdrawalAddress = remember { mutableStateOf<String?>(null) }
                val tag = remember { mutableStateOf<String?>(null) }

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
                        text = "Withdraw ${coin.description}*",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "*All withdrawals are subjected to a one business day security hold and will be processed no later than 19:00 UTC on the next business day.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                AccountSelectionDropDownMenu(
                    "From ${coin.binanceTicker} Account",
                    coin.binanceTicker,
                    coin.precision,
                    selectedFromPlan,
                    coin.depositPlans.filter { it.isVisible },
                    withdrawProcessState !is WithdrawProcessState.LoadingWithdrawRequest && withdrawProcessState !is WithdrawProcessState.LoadingFeeEstimate
                ) { newPlanId ->
                    resetWithdrawState(WithdrawProcessState.NotStarted)
                    selectedFromPlanId.value = newPlanId

                    amount.value = null
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Withdrawal Address",
                    style = MaterialTheme.typography.titleMedium.copy(
                        Color(0xFFa19ead),
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = withdrawalAddress.value ?: "",
                    onValueChange = {
                        resetWithdrawState(WithdrawProcessState.NotStarted)
                        withdrawalAddress.value = it
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        unfocusedIndicatorColor = Color(0xFFa19ead)
                    ),
                    enabled = withdrawProcessState !is WithdrawProcessState.LoadingWithdrawRequest && withdrawProcessState !is WithdrawProcessState.LoadingFeeEstimate
                )

                if (coin.supportsTag) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(Res.string.tag_memo),
                        style = MaterialTheme.typography.titleMedium.copy(
                            Color(0xFFa19ead),
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = tag.value ?: "",
                        onValueChange = {
                            tag.value = it
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            unfocusedIndicatorColor = Color(0xFFa19ead)
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    WarningBanner(
                        false,
                        "WARNING! Please confirm if the Withdrawal Address requires a Destination Tag.",
                        null,
                        WarningBannerColor.RED
                    )
                }
                Spacer(Modifier.height(16.dp))
                AmountInputTextHeader("Amount", coin.binanceTicker, coin.precision, selectedFromPlan.balanceAvailable, "The amount available for withdrawal may differ from the account balance in three cases:\n" +
                        "1) You have already initiated a partial withdrawal of funds from this account and it is in the \"Pending\" status. Until processing is completed, this amount is displayed on the account balance;\n" +
                        "and/or\n" +
                        "2) The account balance includes bonuses that are not available for withdrawal during a certain period, based on the terms of the promotion under which they were received;\n" +
                        "and/or\n" +
                        "3) The funds in this account, in whole or in part, are not available for withdrawal, as they are used as collateral to maintain the level of 50% LTV (Loan-to-Value) ratio for loans you have already received, along with accrued interest for using the loan.",
                    withdrawProcessState !is WithdrawProcessState.LoadingWithdrawRequest && withdrawProcessState !is WithdrawProcessState.LoadingFeeEstimate) { amount.value = it.toString() }
                AmountInputText(amount.value, coin, coin.withdrawLimit, minOf(selectedFromPlan.balanceAvailable, coin.limit24h), "${coin.binanceTicker} | Minimum ${coin.withdrawLimit}", withdrawProcessState !is WithdrawProcessState.LoadingWithdrawRequest && withdrawProcessState !is WithdrawProcessState.LoadingFeeEstimate) {
                    resetWithdrawState(WithdrawProcessState.NotStarted)
                    amount.value = it
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (coin.limit24h == BigNum("99999999999")) {
                            "No limit"
                        } else {
                            formatCrypto(coin.limit24h, coin.binanceTicker, coin.precision)
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            Color(0xFFa19ead),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    InformationTooltip("The daily withdrawal limit depends on your verification level. Once your identity verification is complete, you have no withdrawal limit.")
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (withdrawProcessState !is WithdrawProcessState.LoadingFeeEstimate) {
                            onCalculateFeeEstimate(
                                selectedFromPlan, withdrawalAddress.value!!, tag.value, amount.value!!.asBigNum
                            )
                        }
                    },
                    enabled = withdrawalAddress.value != null &&
                            amount.value != null &&
                            amount.value!!.asBigNum.isValidAmount(minOf(selectedFromPlan.balanceAvailable, coin.limit24h), coin.withdrawLimit) &&
                            withdrawProcessState !is WithdrawProcessState.LoadingWithdrawRequest
                ) {
                    when(withdrawProcessState) {
                        WithdrawProcessState.LoadingFeeEstimate ->
                            CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        else -> Text("Confirm")
                    }
                }
                Spacer(Modifier.height(16.dp))
                FeeIndicator(
                    "CoinDepo Fees",
                    MaterialTheme.typography.titleMedium.copy(
                        Color(0xFFa19ead),
                        fontWeight = FontWeight.Bold
                    ),
                    "CoinDepo does not charge any withdrawal fees.",
                    "NO FEES",
                    MaterialTheme.typography.titleMedium.copy(
                        Color(0xFF0db46e),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(16.dp))

                val fee = when(withdrawProcessState) {
                    is WithdrawProcessState.FeeEstimationSuccess -> withdrawProcessState.withdrawFees.networkFee
                    is WithdrawProcessState.LoadingWithdrawRequest -> withdrawProcessState.withdrawFees.networkFee
                    is WithdrawProcessState.WithdrawRequestSuccess -> withdrawProcessState.withdrawFees.networkFee
                    else -> null
                }
                FeeIndicator(
                    "Network Fees",
                    MaterialTheme.typography.titleMedium.copy(
                        Color(0xFFa19ead),
                        fontWeight = FontWeight.Bold
                    ),
                    "Network Fee is required to confirm your transaction by the mining network and fluctuates depending on the network or volume of transactions.",
                    fee?.let { formatCrypto(it, coin.binanceTicker, coin.precision) } ?: "Market fees", // TODO: ADD ESTIMATED LABEL HERE
                    MaterialTheme.typography.titleMedium.copy(
                        Color(0xFFa19ead),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(16.dp))
                FeeIndicator(
                    "Receive Amount",
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    "The amount received may vary slightly, depending on network fees at the time the transaction is processed on the network.",
                    formatCrypto((amount.value?.asBigNum ?: BigNum.ZERO) - (fee ?: BigNum.ZERO), coin.binanceTicker, coin.precision),
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.height(16.dp))
                WarningBanner(
                    true,
                    "Withdraw ${coin.depName} using only the ${coin.wthName} network.",
                    "Please ensure that the ${coin.depName} Withdrawal Address is correct. CoinDepo is not responsible for any assets sent to the wrong address.",
                    WarningBannerColor.BLUE
                )
                Spacer(Modifier.height(16.dp))
                WarningBanner(
                    true,
                    "Please note that if you withdraw funds in full from any Interest Account other than the Current Account on the Payout Date but before 14:00 UTC, you will lose the Accrued Interest. On the Payout Date, withdraw funds in full only after 14:00 UTC once the Accrued Interest has been added to your Account Balance. After the full withdrawal of funds, the Interest Account (except for the Current Account) is automatically closed.",
                    null,
                    WarningBannerColor.BLUE
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = onCancelled,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = withdrawProcessState is WithdrawProcessState.FeeEstimationSuccess || withdrawProcessState is WithdrawProcessState.LoadingWithdrawRequest,
                        onClick = {
                            if (withdrawProcessState is WithdrawProcessState.FeeEstimationSuccess) {
                                onRequestWithdraw(selectedFromPlan, withdrawalAddress.value!!, tag.value, amount.value!!.asBigNum, withdrawProcessState.withdrawFees.requestId)
                            }
                        }
                    ) {
                        if (withdrawProcessState is WithdrawProcessState.LoadingWithdrawRequest) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text("Withdraw")
                        }
                    }
                }
            }
        }
    }

    WithdrawStateHandler(coin, withdrawProcessState, resendVerificationCodeState, showSnackBar, sendEmailVerificationCode,onCancelled, resetWithdrawState, onConfirm)
}

@Composable
fun FeeIndicator(
    label: String,
    labelTextStyle: TextStyle,
    information: String,
    fee: String,
    feeTextStyle: TextStyle
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = labelTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            InformationTooltip(information)
        }
        Text(fee, style = feeTextStyle)
    }
}

@Composable
fun WithdrawStateHandler(
    coin: Coin,
    withdrawProcessState: WithdrawProcessState,
    resendVerificationCodeState: ResendVerificationCodeState?,
    showSnackBar: (String, SnackbarDuration) -> Unit,
    sendEmailVerificationCode: () -> Unit,
    onGoBack: () -> Unit,
    resetWithdrawState: (WithdrawProcessState) -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    when (withdrawProcessState) {
        is WithdrawProcessState.FeeEstimationFailure -> {
            when(withdrawProcessState.e) {
                is NoNetworkException -> {
                    showSnackBar("Fee estimation failed. Check your internet connection and try again.", SnackbarDuration.Short)
                    resetWithdrawState(WithdrawProcessState.NotStarted)
                }
                is OtherErrorException -> {
                    showSnackBar("Fee estimation failed.", SnackbarDuration.Short)
                    resetWithdrawState(WithdrawProcessState.NotStarted)
                }
                else -> {}
            }
        }
        is WithdrawProcessState.WithdrawRequestFailure -> {
            when(withdrawProcessState.e) {
                is NoNetworkException -> {
                    showSnackBar("Withdraw request failed. Check your internet connection and try again.", SnackbarDuration.Short)
                    resetWithdrawState(WithdrawProcessState.NotStarted)
                }
                is OtherErrorException -> {
                    showSnackBar("Withdraw request failed.", SnackbarDuration.Short)
                    resetWithdrawState(WithdrawProcessState.NotStarted)
                }
                else -> {}
            }
        }
        is WithdrawProcessState.WithdrawRequestSuccess,
        is WithdrawProcessState.LoadingWithdrawConfirmation -> {
            WithdrawConfirmationBottomSheet(
                coin,
                withdrawProcessState,
                resendVerificationCodeState,
                sendEmailVerificationCode,
                resetWithdrawState,
                onConfirm
            )
        }
        is WithdrawProcessState.WithdrawConfirmationSuccess -> {
            showSnackBar("Withdraw request was successfully processed.", SnackbarDuration.Short)
            onGoBack()
        }
        is WithdrawProcessState.WithdrawConfirmationFailure -> {
            showSnackBar("Failed to process withdraw request.", SnackbarDuration.Short)
            onGoBack()
        }
        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawConfirmationBottomSheet(
    coin: Coin,
    withdrawProcessState: WithdrawProcessState,
    resendVerificationCodeState: ResendVerificationCodeState?,
    sendEmailVerificationCode: () -> Unit,
    resetWithdrawState: (WithdrawProcessState) -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    require(
        withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess ||
        withdrawProcessState is WithdrawProcessState.LoadingWithdrawConfirmation
    )
    val amount = (withdrawProcessState as? WithdrawProcessState.WithdrawRequestSuccess)?.amount ?: (withdrawProcessState as WithdrawProcessState.LoadingWithdrawConfirmation).amount
    val address = (withdrawProcessState as? WithdrawProcessState.WithdrawRequestSuccess)?.address ?: (withdrawProcessState as WithdrawProcessState.LoadingWithdrawConfirmation).address

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden } // Prevent dismissal by swipe or tap outside
    )
    val emailVerificationCode = remember { mutableStateOf("") }
    val twoFACode = remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        onDismissRequest = { }) {
        Column(
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(24.dp)
        ) {
            Text(
                text = "Security Verification",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Withdrawal",
                style = MaterialTheme.typography.titleMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = coin.logoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = formatCrypto(amount, coin.binanceTicker, coin.precision), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Address",
                style = MaterialTheme.typography.titleMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = address, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Network",
                style = MaterialTheme.typography.titleMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = coin.wthName, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "E-mail Verification Code",
                    style = MaterialTheme.typography.titleMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.clickable(
                            enabled = withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess && resendVerificationCodeState?.enabled != false,
                            onClick = {
                                sendEmailVerificationCode()
                            }
                        ),
                        text = when (resendVerificationCodeState?.enabled) {
                            true -> "Resend Code"
                            false -> "Code Sent"
                            else -> "Send Code"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(color = if (resendVerificationCodeState?.enabled == false) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.primary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (resendVerificationCodeState?.enabled == false) {
                        Spacer(Modifier.width(8.dp))
                        InformationTooltip(
                            "Haven't received code? Request new code in ${resendVerificationCodeState.timeRemaining} seconds. The code will expire after 30 minutes."
                        )
                    }
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailVerificationCode.value,
                onValueChange = {
                    if (it.length <= 8) emailVerificationCode.value = it
                },
                isError = false,
                supportingText = {},
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                label = { Text(stringResource(Res.string.email_verification_code)) },
                enabled = withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "2FA Verification Code",
                style = MaterialTheme.typography.titleMedium.copy(Color(0xFFa19ead), fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = twoFACode.value ?: "",
                onValueChange = {
                    if (it.length <= 6) twoFACode.value = it
                },
                singleLine = true,
                isError = false,
                supportingText = {},
                shape = RoundedCornerShape(16.dp),
                label = {
                    Text(
                        stringResource(Res.string.twofa),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                enabled = withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess
            )
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth()
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    onClick = {
                        resetWithdrawState(WithdrawProcessState.NotStarted)
                    },
                    enabled = withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess) onConfirm(emailVerificationCode.value, twoFACode.value)
                    },
                    enabled = emailVerificationCode.value.length == 8 && twoFACode.value?.length == 6
                ) {
                    if (withdrawProcessState is WithdrawProcessState.WithdrawRequestSuccess) {
                        Text("Submit")
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
    }
}

@Preview
@Composable
fun WithdrawScreenPreview() {
    CoinDepoTheme {
        WithdrawScreenContent(
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
                        accountType = AccountType.DAILY,
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
                        accountType = AccountType.WEEKLY,
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
            WithdrawProcessState.NotStarted,
            ResendVerificationCodeState(true, 0),
            {_,_ ->},
            {},
            {},
            {_,_,_,_ -> },
            {_,_,_,_,_ ->},
            {},
            {_,_ ->}
        )
    }
}

@Preview
@Composable
fun WithdrawStateModalPreview() {
    CoinDepoTheme {
        Box(
            Modifier.fillMaxSize()
        ) {
            WithdrawStateHandler(
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
                            accountType = AccountType.DAILY,
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
                            accountType = AccountType.WEEKLY,
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
                WithdrawProcessState.WithdrawRequestSuccess(
                    BigNum("30"),
                    "0x64e72c6ccbedf7088c35ae5876bac4049da7550d",
                    WithdrawRequest(
                        "some_request_id",
                        "some_request_code"
                    ),
                    WithdrawFee(
                        "some_request_id",
                        BigNum("1.23")
                    )
                ),
                null,
                {_,_ ->},
                {},
                {},
                {},
            ) {_,_ -> }
        }
    }
}

fun minOf(a: BigNum, b: BigNum) = if (a < b) a else b