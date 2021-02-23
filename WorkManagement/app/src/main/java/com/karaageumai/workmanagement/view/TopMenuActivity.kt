package com.karaageumai.workmanagement.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.BuildConfig
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.util.CalendarUtil
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

        // ライセンスボタン
        val licensesButton: Button = findViewById(R.id.btn_licenses)
        licensesButton.setOnClickListener {
            Log.i("Licenses")
            val intent = Intent(this, LicensesActivity::class.java)
            startActivity(intent)
        }

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

        // デバッグビルド時はボタン長押しでテストデータを登録する
        if (BuildConfig.DEBUG) {
            val currentYear = CalendarUtil.getCurrentYear()
            val lastYear = currentYear - 1
            val nextYear = currentYear + 1
            salaryButton.setOnLongClickListener{
                ModelFacade.crateTestData(lastYear)
                Toast.makeText(this, "create test data at $lastYear", Toast.LENGTH_SHORT).show()
                true
            }

            bonusButton.setOnLongClickListener{
                ModelFacade.crateTestData(currentYear)
                Toast.makeText(this, "create test data at $currentYear", Toast.LENGTH_SHORT).show()
                true
            }

            displayAnnualDataButton.setOnLongClickListener{
                ModelFacade.crateTestData(nextYear)
                Toast.makeText(this, "create test data at $nextYear", Toast.LENGTH_SHORT).show()
                true
            }
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