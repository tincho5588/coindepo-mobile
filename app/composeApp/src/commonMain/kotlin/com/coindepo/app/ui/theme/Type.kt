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

package com.coindepo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import coindepo.app.composeapp.generated.resources.Gilroy_Black
import coindepo.app.composeapp.generated.resources.Gilroy_BlackItalic
import coindepo.app.composeapp.generated.resources.Gilroy_Bold
import coindepo.app.composeapp.generated.resources.Gilroy_BoldItalic
import coindepo.app.composeapp.generated.resources.Gilroy_ExtraBold
import coindepo.app.composeapp.generated.resources.Gilroy_ExtraBoldItalic
import coindepo.app.composeapp.generated.resources.Gilroy_Light
import coindepo.app.composeapp.generated.resources.Gilroy_LightItalic
import coindepo.app.composeapp.generated.resources.Gilroy_Medium
import coindepo.app.composeapp.generated.resources.Gilroy_MediumItalic
import coindepo.app.composeapp.generated.resources.Gilroy_Regular
import coindepo.app.composeapp.generated.resources.Gilroy_RegularItalic
import coindepo.app.composeapp.generated.resources.Gilroy_SemiBold
import coindepo.app.composeapp.generated.resources.Gilroy_SemiBoldItalic
import coindepo.app.composeapp.generated.resources.Gilroy_Thin
import coindepo.app.composeapp.generated.resources.Gilroy_ThinItalic
import coindepo.app.composeapp.generated.resources.Gilroy_UltraLight
import coindepo.app.composeapp.generated.resources.Gilroy_UltraLightItalic
import coindepo.app.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun gilroyTypography(): Typography {
    val displayFontFamily = FontFamily(
        Font(
            resource = Res.font.Gilroy_Thin,
            weight = FontWeight.Thin,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_ThinItalic,
            weight = FontWeight.Thin,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_UltraLight,
            weight = FontWeight.ExtraLight,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_UltraLightItalic,
            weight = FontWeight.ExtraLight,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_Light,
            weight = FontWeight.Light,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_LightItalic,
            weight = FontWeight.Light,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_Regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_RegularItalic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_Medium,
            weight = FontWeight.Medium,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_MediumItalic,
            weight = FontWeight.Medium,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_SemiBold,
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_SemiBoldItalic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_Bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_BoldItalic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_ExtraBold,
            weight = FontWeight.ExtraBold,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_ExtraBoldItalic,
            weight = FontWeight.ExtraBold,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_Black,
            weight = FontWeight.Black,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings()
        ),
        Font(
            resource = Res.font.Gilroy_BlackItalic,
            weight = FontWeight.Black,
            style = FontStyle.Italic,
            variationSettings = FontVariation.Settings()
        )
    )

    return with(MaterialTheme.typography) {
        Typography(
            displayLarge = displayLarge.copy(fontFamily = displayFontFamily),
            displayMedium = displayMedium.copy(fontFamily = displayFontFamily),
            displaySmall = displaySmall.copy(fontFamily = displayFontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = displayFontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = displayFontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = displayFontFamily),
            titleLarge = titleLarge.copy(fontFamily = displayFontFamily),
            titleMedium = titleMedium.copy(fontFamily = displayFontFamily),
            titleSmall = titleSmall.copy(fontFamily = displayFontFamily),
            bodyLarge = bodyLarge.copy(fontFamily = displayFontFamily),
            bodyMedium = bodyMedium.copy(fontFamily = displayFontFamily),
            bodySmall = bodySmall.copy(fontFamily = displayFontFamily),
            labelLarge = labelLarge.copy(fontFamily = displayFontFamily),
            labelMedium = labelMedium.copy(fontFamily = displayFontFamily),
            labelSmall = labelSmall.copy(fontFamily = displayFontFamily),
        )
    }
}

