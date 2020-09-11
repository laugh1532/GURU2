package com.example.googlelogin.ui.jobkorea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.googlelogin.JobKorea.JobKorea
import com.example.googlelogin.R


class JobKoreaFragment : Fragment() {

//    private lateinit var jobKoreaViewModel: JobKoreaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var root = inflater.inflate(R.layout.fragment_board_list, container, false)

        fragmentManager!!.beginTransaction()
            .replace(R.id.nav_host_fragment, JobKorea())
            .addToBackStack(null)
            .commit()

        return root

//        jobKoreaViewModel =
//            ViewModelProviders.of(this).get(JobKoreaViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_jobkorea, container, false)
////        val textView: TextView = root.findViewById(R.id.text_dashboard)
//////        jobKoreaViewModel.text.observe(this, Observer {
//////            textView.text = it
//////        })
//
//        val textView: TextView = root.findViewById(R.id.textView)
//
//        jobKoreaViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
//        val webview: WebView = root.findViewById(R.id.webview)
//        webview.webViewClient = WebViewClient() //클릭 시 새 창 안 뜨게
//
//        var mWebSettings = webview.settings //세부 세팅 등록
//        mWebSettings.javaScriptEnabled = true //웹페이지 자바스크립트 허용
//        mWebSettings.setSupportMultipleWindows(false) //새창 띄우기 X
//        mWebSettings.javaScriptCanOpenWindowsAutomatically = false //자바스크립트 새 창 X
//        mWebSettings.loadWithOverviewMode = true //메타태그 허용 여부
//        mWebSettings.useWideViewPort = true //화면 사이즈 맞추기
//        mWebSettings.setSupportZoom(true) //화면 줌
//        mWebSettings.builtInZoomControls = true //화면 확대 축소
//        mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL //컨텐츠 사이즈
//        mWebSettings.cacheMode = WebSettings.LOAD_NO_CACHE //브라우저 캐시 허용 여부
//        mWebSettings.domStorageEnabled = true //로컬 저장소 여부
//
//        webview.loadUrl("file:///android_asset/www/html.html")
//
//        return root
    }
}