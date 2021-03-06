/*
 * AppCompatWebComponents (https://github.com/androidovshchik/AppCompatWebComponents)
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

@file:Suppress("unused")

package androidovshchik.webcomponents.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidovshchik.webcomponents.exceptions.AppNotFoundException

fun Context.openChrome(url: CharSequence?): Throwable? {
    return openBrowser(url, "com.android.chrome", "com.android.beta", "com.android.dev",
        "com.android.canary")
}

fun Context.openFirefox(url: CharSequence?): Throwable? {
    return openBrowser(url, "org.mozilla.firefox", "org.mozilla.firefox_beta", "org.mozilla.fennec_aurora",
        "org.mozilla.focus", "org.mozilla.vrbrowser")
}

fun Context.openOpera(url: CharSequence?): Throwable? {
    return openBrowser(url, "com.opera.browser", "com.opera.mini.native", "com.opera.browser.beta",
        "com.opera.mini.native.beta", "com.opera.touch")
}

fun Context.openEdge(url: CharSequence?): Throwable? {
    return openBrowser(url, "com.microsoft.emmx")
}

fun Context.openYandexBrowser(url: CharSequence?): Throwable? {
    return openBrowser(url, "com.yandex.browser", "com.yandex.browser.beta", "com.yandex.browser.alpha",
        "com.yandex.browser.lite")
}

fun Context.openBrowser(url: CharSequence?, vararg packages: String): Throwable? {
    if (url == null) {
        return NullPointerException()
    }
    Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (packages.isEmpty()) {
            return tryStartActivity(this)
        }
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            if (!app.enabled) {
                continue
            }
            // todo intent filter browser
            if (app.packageName in packages) {
                setPackage(app.packageName)
                return tryStartActivity(this)
            }
        }
        return AppNotFoundException()
    }
}

internal fun Context.tryStartActivity(intent: Intent): Throwable? {
    return try {
        startActivity(intent)
        null
    } catch (e: Throwable) {
        e
    }
}

internal fun Context.showToast(message: CharSequence?, delay: Int = Toast.LENGTH_SHORT) = Toast
    .makeText(applicationContext, message.toString(), delay)
    .show()