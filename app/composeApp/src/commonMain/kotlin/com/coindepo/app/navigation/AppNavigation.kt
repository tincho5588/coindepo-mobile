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

package com.coindepo.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.coindepo.app.feature.login.LoginScreen
import com.coindepo.app.feature.login.LoginViewModel
import com.coindepo.app.feature.mainscreen.MainScreen
import com.coindepo.app.feature.resetpassword.ResetPasswordScreen
import com.coindepo.app.feature.resetpassword.ResetPasswordViewModel
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.app.ui.theme.Contrast
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    CoinDepoTheme(contrast = Contrast.NORMAL) {
        val navController = rememberNavController()
        val loginViewModel = koinViewModel<LoginViewModel>()

        NavHost(
            navController = navController,
            startDestination = LoginScreenRoute
        ) {
            composable<LoginScreenRoute> {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    onResetPassword = { navController.navigate(ResetPasswordScreenRoute) },
                    onLoginDone = {
                        navController.navigate(
                            MainScreenRoute,
                            navOptions {
                                popUpTo(LoginScreenRoute, popUpToBuilder = { inclusive = true })
                            }
                        )
                    }
                )
            }

            composable<ResetPasswordScreenRoute> {
                ResetPasswordScreen(koinViewModel<ResetPasswordViewModel>())
            }

            composable<MainScreenRoute> {
                MainScreen(
                    loginViewModel.loginState.collectAsState().value,
                    {
                        navController.navigate(
                            LoginScreenRoute,
                            navOptions {
                                popUpTo(MainScreenRoute, popUpToBuilder = { inclusive = true })
                            }
                        )
                    }
                )
            }
        }
    }
}

@Serializable
private sealed interface NavigationRoute

@Serializable
private data object LoginScreenRoute : NavigationRoute

@Serializable
private data object ResetPasswordScreenRoute : NavigationRoute

@Serializable
private data object MainScreenRoute: NavigationRoute