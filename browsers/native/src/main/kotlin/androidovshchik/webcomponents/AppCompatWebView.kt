/*
 * AppCompatWebComponents (https://github.com/androidovshchik/AppCompatWebComponents)
 * Copyright (c) 2019. Vlad Kalyuzhnyu <vladkalyuzhnyu@gmail.com>
 */

package androidovshchik.webcomponents

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebView
import androidovshchik.webcomponents.extensions.checkThread
import androidovshchik.webcomponents.models.WebEngine
import androidovshchik.webcomponents.models.WebPage

@Suppress("LeakingThis", "unused", "UNUSED_PARAMETER")
open class AppCompatWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), IAppCompatWebView {

    override val engine: WebEngine
        get() {
            checkThread()
            return WebEngine.NATIVE
        }

    override val page: WebPage
        get() {
            return copyBackForwardList().currentItem?.let {
                WebPage(it.url, it.favicon, it.title)
            } ?: WebPage()
        }

    override val history: ArrayList<out WebPage>
        get() {
            val list = copyBackForwardList()
            val pages = arrayListOf<WebPage>()
            for (i in 0 until list.size) {
                list.getItemAtIndex(i)?.let {
                    pages.add(WebPage(it.url, it.favicon, it.title))
                }
            }
            return pages
        }

    override var listener: IWebViewListener? = null

    init {
        init(AppCompatWebView::class.java)
    }

    @SuppressLint("SetJavaScriptEnabled")
    open fun init(clss: Class<out IAppCompatWebView>) {
        if (isInEditMode) {
            return
        }
        settings.javaScriptEnabled = true
        webViewClient = NativeViewClient()
        webChromeClient = NativeChromeClient()
    }

    override fun onResume() {
        super.onResume()
        resumeTimers()
    }

    override fun loadUrl(url: String?) {
        super.loadUrl(url.toString())
    }

    override fun navigateBack(): Boolean {
        return if (canGoBack()) {
            goBack()
            true
        } else {
            false
        }
    }

    override fun navigateForward(): Boolean {
        return if (canGoForward()) {
            goForward()
            true
        } else {
            false
        }
    }

    override fun onPause() {
        pauseTimers()
        super.onPause()
    }

    override fun onDestroy() {
        checkThread()
        try {
            (parent as ViewGroup?)?.removeView(this)
        } catch (e: Exception) {
        }
        try {
            removeAllViews()
        } catch (e: Exception) {
        }
        destroy()
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}