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

// iosMain/kotlin/YourFileName.kt
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication

actual fun openUrlInBrowser(url: String) {
    val nsUrl = platform.Foundation.NSURL.URLWithString(url) ?: return
    val safariVC = SFSafariViewController(nsUrl)

    // You need to find the currently presented UIViewController to present SFSafariViewController
    // This often involves accessing the root view controller of the application's window
    // or finding the topmost presented view controller.
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootViewController?.presentViewController(safariVC, true, null)
}