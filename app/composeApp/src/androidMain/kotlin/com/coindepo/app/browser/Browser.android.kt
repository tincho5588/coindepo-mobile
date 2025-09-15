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

package com.coindepo.app.browser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.coindepo.app.appStateNotifier


actual fun openUrlInBrowser(url: String) {
    appStateNotifier?.topMostActivity?.let {
        openUrlInBrowser(it, url)
    }
}

fun openUrlInBrowser(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().apply {
        setShowTitle(true)
    }.build()


    try {
        customTabsIntent.launchUrl(context, url.toUri())
    } catch (e: ActivityNotFoundException) {
        openInBrowserFallback(context, url)
    }
}

private fun openInBrowserFallback(context: Context, url: String) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "You don't have a browser to open this link.", Toast.LENGTH_SHORT).show()
    }
}