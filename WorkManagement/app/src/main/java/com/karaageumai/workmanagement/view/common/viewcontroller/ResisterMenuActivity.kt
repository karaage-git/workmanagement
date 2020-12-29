package com.karaageumai.workmanagement.view.common.viewcontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log

const val KEY_INPUT_MODE = "KEY_INPUT_MODE"
const val INPUT_MODE_SALARY = 0
const val INPUT_MODE_BONUS = 1
const val INPUT_MODE_ERROR = -1

class ResisterMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate()")

        setContentView(R.layout.activity_resister_menu)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_resistermenu)
        setSupportActionBar(toolbar)

        val salaryButton: Button = findViewById(R.id.btn_salary)
        salaryButton.setOnClickListener{
            val intent = Intent(this, CheckTargetYearMonthActivity::class.java)
            intent.putExtra(KEY_INPUT_MODE, INPUT_MODE_SALARY)
            startActivity(intent)
        }

        val bonusButton: Button = findViewById(R.id.btn_bonus)
        bonusButton.setOnClickListener{
            val intent = Intent(this, CheckTargetYearMonthActivity::class.java)
            intent.putExtra(KEY_INPUT_MODE, INPUT_MODE_BONUS)
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