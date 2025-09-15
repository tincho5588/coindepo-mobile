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

package com.coindepo.app.feature.mainscreen.userdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coindepo.app.composeapp.generated.resources.Res
import coindepo.app.composeapp.generated.resources.bonuses
import coindepo.app.composeapp.generated.resources.help_center
import coindepo.app.composeapp.generated.resources.log_out
import coindepo.app.composeapp.generated.resources.profile
import coindepo.app.composeapp.generated.resources.referrals
import coindepo.app.composeapp.generated.resources.security
import coindepo.app.composeapp.generated.resources.verification
import com.coindepo.app.browser.openUrlInBrowser
import com.coindepo.app.ui.theme.CoinDepoTheme
import com.coindepo.domain.entities.userdetails.UserDetails
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsTooltip(
    userDetailsViewModel: UserDetailsViewModel,
) {
    LaunchedEffect(Unit) {
        userDetailsViewModel.fetchUserDetails()
    }
    UserDetailsTooltip(
        userDetails = userDetailsViewModel.userDetails.collectAsState().value,
        onLogOut = userDetailsViewModel::logOut
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsTooltip(
    state: TooltipState = rememberTooltipState(isPersistent = true),
    userDetails: UserDetails?,
    onLogOut: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val avatarPainter = userDetails?.avatar?.takeIf { it.isNotEmpty() }?.toByteArray()?.decodeToImageBitmap()
        ?.let { remember(it) { BitmapPainter(it, filterQuality = DefaultFilterQuality) } }
        ?: rememberVectorPainter(image = Icons.Filled.AccountCircle)

    TooltipBox(
        TooltipDefaults.rememberRichTooltipPositionProvider(),
        {
            RichTooltip(
                modifier = Modifier.wrapContentSize(),
                caretSize = DpSize(24.dp, 16.dp),
                colors = TooltipDefaults.richTooltipColors().copy(containerColor = MaterialTheme.colorScheme.background)
            ) {
                UserDetailsTooltipContent(
                    avatarPainter,
                    userDetails?.firstName?.lowercase()?.capitalizeEachWord(),
                    userDetails?.lastName?.lowercase()?.capitalizeEachWord(),
                    userDetails?.email,
                    userDetails?.status,
                    { state.dismiss() },
                    onLogOut
                )
            }
        },
        state
    ) {
        BadgedBox(
            badge = {
                userDetails?.status?.let { status ->
                    Badge(
                        modifier = Modifier.padding(2.dp).size(14.dp).border(BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer), CircleShape),
                        containerColor = when(status) {
                            "confirmed" -> Color(0xFFFFA500)
                            "verified" -> Color(0xFF008000)
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        if (state.isVisible) {
                            state.dismiss()
                        } else {
                            state.show(MutatePriority.UserInput)
                        }
                    }
                }
            ) {
                AvatarImage(
                    painter = avatarPainter,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun UserDetailsTooltipContent(
    avatarPainter: Painter,
    firstName: String?,
    lastName: String?,
    email: String?,
    status: String?,
    closeTooltip: () -> Unit,
    onLogOut: () -> Unit
) {
    Column(
        modifier = Modifier.wrapContentSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarImage(
            painter = avatarPainter,
            modifier = Modifier.width(90.dp).height(90.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (firstName != null && lastName != null) Text(modifier = Modifier.wrapContentSize(), text = "$firstName $lastName", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        if (email != null) Text(modifier = Modifier.wrapContentSize(), text = email, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        OptionsMenuButton(
            icon = Icons.Filled.CheckCircle,
            text = stringResource(Res.string.verification),
            status = status,
            closeTooltip = closeTooltip,
            onClick = {}
        )

        OptionsMenuButton(
            icon = Icons.Filled.Person,
            text = stringResource(Res.string.profile),
            closeTooltip = closeTooltip,
            onClick = {}
        )

        OptionsMenuButton(
            icon = Icons.Filled.Shield,
            text = stringResource(Res.string.security),
            status = status,
            closeTooltip = closeTooltip,
            onClick = {}
        )

        OptionsMenuButton(
            icon = Icons.Filled.People,
            text = stringResource(Res.string.referrals),
            closeTooltip = closeTooltip,
            onClick = {}
        )

        OptionsMenuButton(
            icon = Icons.Filled.Star,
            text = stringResource(Res.string.bonuses),
            closeTooltip = closeTooltip,
            onClick = {}
        )

        OptionsMenuButton(
            icon = Icons.AutoMirrored.Outlined.Help,
            text = stringResource(Res.string.help_center),
            closeTooltip = closeTooltip,
            onClick = {
                openUrlInBrowser("https://coindepo.com/support/help-center")
            }
        )

        OptionsMenuButton(
            icon = Icons.AutoMirrored.Filled.Logout,
            text = stringResource(Res.string.log_out),
            closeTooltip = closeTooltip,
            onClick = onLogOut
        )
    }
}

@Composable
private fun AvatarImage(
    modifier: Modifier,
    painter: Painter
) {
    Image(
        painter = painter,
        contentDescription = "",
        contentScale = ContentScale.Fit,
        modifier = modifier.clip(CircleShape),
        colorFilter = if (painter is VectorPainter) ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer) else null
    )
}

@Composable
private fun OptionsMenuButton(
    icon: ImageVector,
    text: String,
    status: String? = null,
    closeTooltip: () -> Unit,
    onClick: () -> Unit
) {
    TextButton(
        onClick = {
            closeTooltip()
            onClick()
        }
    ) {
        Row(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                modifier = Modifier.width(24.dp).height(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.titleMedium)
            if (status != null) {
                Badge(
                    modifier = Modifier.padding(start = 8.dp).size(8.dp),
                    containerColor = when(status) {
                        "confirmed" -> Color(0xFFFFA500)
                        "verified" -> Color(0xFF008000)
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserDetailsTooltipPreview() {
    CoinDepoTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.TopCenter
        ) {
            UserDetailsTooltip(
                rememberTooltipState(initialIsVisible = true),
                UserDetails(
                    userId = 19880,
                    totp = true,
                    firstName = "Martin Leon",
                    lastName = "Bouchet",
                    email = "tinchoalternativeforsteam@gmail.com",
                    userType = "Individual",
                    status = "confirmed",
                    language = "en",
                    emailPromo = "1",
                    emailActivities = "1",
                    emailTrx = "1",
                    emailNews = "1",
                    emailSystem = "1",
                    creditLineActive = "disabled",
                    avatar = emptyList(),
                    isAffiliate = "0",
                    regCountry = "AR",
                    hasPassword = "1",
                    ipLock = "1"
                )
            ) {}
        }
    }
}

fun String.capitalizeEachWord(): String = split(" ").joinToString(" ") { it.capitalize() }