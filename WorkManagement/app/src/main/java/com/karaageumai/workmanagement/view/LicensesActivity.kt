package com.karaageumai.workmanagement.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R

class LicensesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)

        // ツールバー読み込み
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_licenses)
        setSupportActionBar(toolbar)

        val view: WebView = findViewById(R.id.wv_licenses)
        view.loadUrl("file:///android_asset/licenses.html")
    }

    // ツールバーのレイアウト反映
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("onCreateOptionsMenu()")
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_empty, menu)
        return true
    }
}