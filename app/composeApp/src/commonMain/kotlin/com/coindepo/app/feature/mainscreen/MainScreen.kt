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

package com.coindepo.app.feature.mainscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.GeneratingTokens
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.borrow
import coindepo.app.composeapp.generated.resources.dashboard
import coindepo.app.composeapp.generated.resources.earn
import coindepo.app.composeapp.generated.resources.ic_coin_depo_logo_36
import coindepo.app.composeapp.generated.resources.tokens
import coindepo.app.composeapp.generated.resources.transactions
import com.coindepo.app.feature.mainscreen.dashboard.DashboardScreen
import com.coindepo.app.feature.mainscreen.deposit.DepositScreen
import com.coindepo.app.feature.mainscreen.deposit.WalletDataViewModel
import com.coindepo.app.feature.mainscreen.earn.EarnScreen
import com.coindepo.app.feature.mainscreen.transactions.TransactionsScreen
import com.coindepo.app.feature.mainscreen.transactions.TransactionsViewModel
import com.coindepo.app.feature.mainscreen.transfer.TransferScreen
import com.coindepo.app.feature.mainscreen.transfer.TransferViewModel
import com.coindepo.app.feature.mainscreen.userdetails.UserDetailsTooltip
import com.coindepo.app.feature.mainscreen.userdetails.UserDetailsViewModel
import com.coindepo.app.feature.mainscreen.withdraw.WithdrawScreen
import com.coindepo.app.feature.mainscreen.withdraw.WithdrawViewModel
import com.coindepo.domain.entities.currency.Currency
import com.coindepo.domain.entities.login.LoggedInLoginState
import com.coindepo.domain.entities.login.LoginState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    loginState: LoginState,
    onLoggedOut: () -> Unit
) {
    if (loginState !is LoggedInLoginState) {
        onLoggedOut()
    } else {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route

        val accountStatsViewModel = koinViewModel<AccountStatsViewModel>()
        val userDetailsViewModel = koinViewModel<UserDetailsViewModel>()

        val isRefreshing =
            accountStatsViewModel.isRefreshing.combine(userDetailsViewModel.isRefreshing) { refreshingAccountStats, refreshingUserDetails ->
                refreshingAccountStats || refreshingUserDetails
            }.collectAsState(false)

        val refresh: () -> Unit = {
            userDetailsViewModel.fetchUserDetails(true)
            accountStatsViewModel.fetchAccountStats(true)
        }
        val balancesVisible = rememberSaveable { mutableStateOf(true) }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { balancesVisible.value = !balancesVisible.value }) {
                            Icon(imageVector = if (balancesVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        CurrencySelector(selectedCurrency = accountStatsViewModel.currency.collectAsState().value) {
                            if (it != accountStatsViewModel.currency.value) {
                                accountStatsViewModel.changeCurrency(it)
                            }
                        }
                        UserDetailsTooltip(userDetailsViewModel)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate(startDestination, true)
                        }) {
                            Image(
                                painter = painterResource(Res.drawable.ic_coin_depo_logo_36),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(2.dp)
                            )
                        }
                    },
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.shadow(8.dp),
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    bottomBarNavigationRoutes(accountStatsViewModel.tokens.collectAsState().value.isNotEmpty()).forEach { route: MainScreenNavigationRoute ->
                        NavigationBarItem(
                            icon = {
                                Icon(imageVector = route.icon, contentDescription = stringResource(route.nameResource))
                            },
                            label = {
                                Text(
                                    stringResource(route.nameResource),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = route::class.matches(currentDestination),
                            onClick = {
                                // Avoid multiple copies of the same destination
                                if (route::class.matches(currentDestination)) return@NavigationBarItem

                                navController.navigate(route, true)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = startDestination,
                Modifier.padding(innerPadding),
            ) {
                composable<DashboardScreenNavigationRoute> {
                    DashboardScreen(
                        accountStatsViewModel = accountStatsViewModel,
                        isRefreshing = isRefreshing.value,
                        maskBalances = !balancesVisible.value,
                        goToDepositScreen = { coinId ->
                            navController.navigate(DepositNavigationRoute(coinId), false)
                        },
                        goToWithdrawScreen = { coinId, depositPlanId ->
                            // TODO: Show a dialog stating you can not withdraw without 2FA
                            if (userDetailsViewModel.userDetails.value?.totp ?: false) {
                                navController.navigate(
                                    WithdrawNavigationRoute(coinId, depositPlanId),
                                    false
                                )
                            }
                        },
                        goToEarnScreen = {
                            navController.navigate(EarnScreenNavigationRoute(it), true)
                        },
                        goToBuyToken = {},
                        goToTokensScreen = {
                            navController.navigate(TokensScreenNavigationRoute(it), true)
                        },
                        onRefresh = refresh
                    )
                }
                composable<EarnScreenNavigationRoute> {
                    val route = it.toRoute<EarnScreenNavigationRoute>()
                    EarnScreen(
                        accountStatsViewModel = accountStatsViewModel,
                        initialCoinId = route.initialCoinId,
                        isRefreshing = isRefreshing.value,
                        maskBalances = !balancesVisible.value,
                        goToDepositScreen = {
                            navController.navigate(DepositNavigationRoute(it), false)
                        },
                        goToWithdrawScreen = { coinId, depositPlanId ->
                            // TODO: Show a dialog stating you can not withdraw without 2FA
                            if (userDetailsViewModel.userDetails.value?.totp ?: false) {
                                navController.navigate(
                                    WithdrawNavigationRoute(coinId, depositPlanId),
                                    false
                                )
                            }
                        },
                        goToTransferScreen = {
                            navController.navigate(TransferNavigationRoute(it), false)
                        },
                        onRefresh = refresh
                    )
                }
                composable<BorrowScreenNavigationRoute> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Coming soon",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                composable<TokensScreenNavigationRoute> {
                    val route = it.toRoute<TokensScreenNavigationRoute>()
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "Coming soon, coin_id: ${route.initialCoinId}",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                composable<TransactionsScreenNavigationRoute> {
                    TransactionsScreen(
                        koinViewModel<TransactionsViewModel>(),
                        { message, duration, actionLabel, action ->
                            scope.launch {
                                if (snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = duration,
                                    withDismissAction = actionLabel != null,
                                    actionLabel = actionLabel
                                ) == SnackbarResult.ActionPerformed) {
                                    action()
                                }
                            }
                        }
                    )
                }
                composable<DepositNavigationRoute> {
                    val route = it.toRoute<DepositNavigationRoute>()
                    DepositScreen(
                        accountStatsViewModel,
                        koinViewModel<WalletDataViewModel>(),
                        route.coinId
                    )
                }
                composable<TransferNavigationRoute> {
                    val route = it.toRoute<TransferNavigationRoute>()
                    TransferScreen(
                        accountStatsViewModel,
                        koinViewModel<TransferViewModel>(),
                        route.fromPlanId,
                        { message, duration ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message = message, duration = duration)
                            }
                        }
                    ) {
                        navController.popBackStack()
                    }
                }
                composable<WithdrawNavigationRoute> {
                    val route = it.toRoute<WithdrawNavigationRoute>()
                    WithdrawScreen(
                        accountStatsViewModel,
                        koinViewModel<WithdrawViewModel>(),
                        route.coinId,
                        route.fromPlanId,
                        { message, duration ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message = message, duration = duration)
                            }
                        }
                    ) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    Column {
        val expanded = remember { mutableStateOf(false) }
        TextButton(
            onClick = { expanded.value = true },
            colors = ButtonDefaults.textButtonColors().copy(contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedCurrency.name)
                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(
            modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(8.dp),
            expanded = expanded.value,
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = { expanded.value = false }
        ) {
            Currency.entries.forEach { currency ->
                TextButton(
                    onClick = {
                        onCurrencySelected(currency)
                        expanded.value = false
                    },
                    colors = ButtonDefaults.textButtonColors().copy(contentColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text(text = currency.name)
                }
            }
        }
    }
}

@Serializable
sealed interface MainScreenNavigationRoute

@Serializable
private data object DashboardScreenNavigationRoute : MainScreenNavigationRoute

@Serializable
private data class EarnScreenNavigationRoute(
    val initialCoinId: CoinId? = null
) : MainScreenNavigationRoute

@Serializable
private data object BorrowScreenNavigationRoute : MainScreenNavigationRoute

@Serializable
private data class TokensScreenNavigationRoute(
    val initialCoinId: CoinId? = null
) : MainScreenNavigationRoute

@Serializable
private data object TransactionsScreenNavigationRoute : MainScreenNavigationRoute

@Serializable
private data class DepositNavigationRoute(
    val coinId: CoinId
) : MainScreenNavigationRoute

@Serializable
private data class TransferNavigationRoute(
    val fromPlanId: DepositPlanId
) : MainScreenNavigationRoute

@Serializable
private data class WithdrawNavigationRoute(
    val coinId: CoinId,
    val fromPlanId: DepositPlanId?
) : MainScreenNavigationRoute

fun bottomBarNavigationRoutes(
    tokensEnabled: Boolean
): List<MainScreenNavigationRoute> = if (tokensEnabled) listOf(
    DashboardScreenNavigationRoute,
    EarnScreenNavigationRoute(),
    BorrowScreenNavigationRoute,
    TokensScreenNavigationRoute(),
    TransactionsScreenNavigationRoute
) else {
    listOf(
        DashboardScreenNavigationRoute,
        EarnScreenNavigationRoute(),
        BorrowScreenNavigationRoute,
        TransactionsScreenNavigationRoute
    )
}

private val startDestination = DashboardScreenNavigationRoute

val MainScreenNavigationRoute.icon: ImageVector
    get() = when (this) {
        is BorrowScreenNavigationRoute -> Icons.Outlined.MonetizationOn
        is DashboardScreenNavigationRoute -> Icons.Outlined.Speed
        is EarnScreenNavigationRoute -> Icons.Outlined.Percent
        is TokensScreenNavigationRoute -> Icons.Outlined.GeneratingTokens
        is TransactionsScreenNavigationRoute -> Icons.AutoMirrored.Outlined.FormatListBulleted
        else -> throw IllegalArgumentException()
    }

val MainScreenNavigationRoute.nameResource: StringResource
    get() = when (this) {
        is BorrowScreenNavigationRoute -> Res.string.borrow
        is DashboardScreenNavigationRoute -> Res.string.dashboard
        is EarnScreenNavigationRoute -> Res.string.earn
        is TokensScreenNavigationRoute -> Res.string.tokens
        is TransactionsScreenNavigationRoute -> Res.string.transactions
        else -> throw IllegalArgumentException()
    }

fun <T : MainScreenNavigationRoute> KClass<T>.matches(route: String?): Boolean =
    route?.startsWith(this.qualifiedName ?: "") == true

fun NavController.navigate(
    route: MainScreenNavigationRoute,
    popUpToStart: Boolean
) {
    val popped = popBackStack(route, false)

    if (!popped) {
        navigate(route, navOptions {
            if (popUpToStart) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(DashboardScreenNavigationRoute) {
                    inclusive = false
                }
            }

            // Restore state when reselecting a previously selected item
            restoreState = true
        })
    }
}

typealias CoinId = Int
typealias DepositPlanId = String