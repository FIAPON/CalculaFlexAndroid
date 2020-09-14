package br.com.heiderlopes.calculaflex.ui.terms

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import br.com.heiderlopes.calculaflex.R
import br.com.heiderlopes.calculaflex.ui.base.BaseFragment
import br.com.heiderlopes.calculaflex.utils.firebase.RemoteConfigKeys
import br.com.heiderlopes.calculaflex.utils.firebase.RemoteConfigUtils

class TermsFragment : BaseFragment() {
    override val layout = R.layout.fragment_terms

    override val screeName = "Terms"

    private lateinit var wvTerms: WebView
    private lateinit var ivBack: ImageView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wvTerms = view.findViewById(R.id.wvTerms)
        ivBack = view.findViewById(R.id.ivBack)

        ivBack.setOnClickListener {
            activity?.onBackPressed()
        }

        val termsUrl = RemoteConfigUtils.getFirebaseRemoteConfig()
            .getString(RemoteConfigKeys.TERMS_URL)

        wvTerms.webViewClient = CalculaFlexWebViewClients(this)
        wvTerms.loadUrl(termsUrl)
    }

    class CalculaFlexWebViewClients(private val baseFragment: BaseFragment) : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(
            view: WebView,
            url: String
        ) {
            super.onPageFinished(view, url)
            baseFragment.hideLoading()
        }

        init {
            baseFragment.showLoading()
        }
    }
}
