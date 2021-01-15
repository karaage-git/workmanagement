package com.karaageumai.workmanagement.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.view.analyze.annual.top.AnnualAnalyzeActivity
import com.karaageumai.workmanagement.view.input.common.CheckTargetYearMonthActivity

const val KEY_INPUT_MODE = "KEY_INPUT_MODE"
const val INPUT_MODE_SALARY = 0
const val INPUT_MODE_BONUS = 1
const val INPUT_MODE_ERROR = -1

class TopMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate()")

        setContentView(R.layout.activity_top_menu)

        // ツールバー読み込み
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_topmenu)
        setSupportActionBar(toolbar)

        // 給与情報の登録
        val salaryButton: Button = findViewById(R.id.btn_input_salary)
        salaryButton.setOnClickListener {
            Log.i("Input SalaryInfo")
            val intent = Intent(this, CheckTargetYearMonthActivity::class.java)
            intent.putExtra(KEY_INPUT_MODE, INPUT_MODE_SALARY)
            startActivity(intent)
        }

        // ボーナス情報の登録
        val bonusButton: Button = findViewById(R.id.btn_input_bonus)
        bonusButton.setOnClickListener {
            Log.i("Input BonusInfo")
            val intent = Intent(this, CheckTargetYearMonthActivity::class.java)
            intent.putExtra(KEY_INPUT_MODE, INPUT_MODE_BONUS)
            startActivity(intent)
        }

        // 年間データの表示
        val displayAnnualDataButton: Button = findViewById(R.id.btn_annual_data)
        displayAnnualDataButton.setOnClickListener {
            Log.i("Display annual data")
            val intent = Intent(this, AnnualAnalyzeActivity::class.java)
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