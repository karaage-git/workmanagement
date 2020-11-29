package com.karaageumai.workmanagement.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.util.Log
import com.karaageumai.workmanagement.view.resister.ResisterMenuActivity

class TopMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate()")

        setContentView(R.layout.activity_top_menu)

        // Todo:全アクティビティで共通処理となるため、BaseActivity的なものを作成してもいいかも
        // ツールバー読み込み
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.top_menu)
        setSupportActionBar(toolbar)

        // 情報登録画面へ遷移
        val resisterButton: Button = findViewById(R.id.btn_register_info)
        resisterButton.setOnClickListener {
            Log.i("go resister")
            val intent = Intent(this, ResisterMenuActivity::class.java)
            startActivity(intent)
        }

    }

    // ツールバーのレイアウト反映
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("onCreateOptionsMenu()")
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_empty, menu)
        return true
    }


}