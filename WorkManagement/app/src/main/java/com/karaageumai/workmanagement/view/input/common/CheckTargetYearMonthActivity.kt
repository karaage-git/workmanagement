package com.karaageumai.workmanagement.view.input.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.util.CalendarUtil
import com.karaageumai.workmanagement.util.Constants.MAX_YEAR
import com.karaageumai.workmanagement.util.Constants.MIN_YEAR
import com.karaageumai.workmanagement.util.Constants.YEAR_END_MONTH
import com.karaageumai.workmanagement.util.Constants.YEAR_START_MONTH
import com.karaageumai.workmanagement.view.*
import com.karaageumai.workmanagement.view.input.bonus.BonusActivity
import com.karaageumai.workmanagement.view.input.salary.SalaryActivity
import java.util.*

const val KEY_ENTRY_MODE = "KEY_ENTRY_MODE"
const val KEY_YEAR = "KEY_YEAR"
const val KEY_MONTH = "KEY_MONTH"

const val ENTRY_MODE_NEW = 0
const val ENTRY_MODE_ALREADY_EXIST = 1

class CheckTargetYearMonthActivity : AppCompatActivity() {
    // 各View
    private lateinit var mSpinner: Spinner
    private lateinit var mStartButton: Button

    // 入力されるYYYYmmを保持するプロパティ
    private var mYear: Int = 0
    private var mMonth: Int = 0
    // 給料orボーナスを判定するフラグ
    private var mMode = INPUT_MODE_SALARY
    // 年と月のPairを管理するためのリスト
    private lateinit var mYearMonthList: List<Pair<Int, Int>>
    // DBに存在するデータの年月リスト
    private lateinit var mDBYearMonthList: List<Pair<Int, Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("onCreate()")
        setContentView(R.layout.activity_check_target_year_month_menu)

        // 給与orボーナスの判定
        mMode = intent.getIntExtra(KEY_INPUT_MODE, INPUT_MODE_ERROR)
        // ツールバー
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_check_target_year_month)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // スピナー
        mSpinner = findViewById(R.id.sp_target)
        // スタートボタン
        mStartButton = findViewById(R.id.btn_start)
        // 年月リスト
        mYearMonthList = createYearMonthList()

        when(mMode) {
            INPUT_MODE_SALARY -> {
                mDBYearMonthList = ModelFacade.selectSalaryYearMonthList()
            }

            INPUT_MODE_BONUS -> {
                mDBYearMonthList = ModelFacade.selectBonusYearMonthList()
            }

            INPUT_MODE_ERROR -> {
                // エラーとみなす
                Log.i("mMode : ERROR")
                val intent = Intent(this, TopMenuActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }

        val adapter = YearMonthSpinnerAdapter(this, mYearMonthList, mDBYearMonthList, mMode)
        mSpinner.adapter = adapter

        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mYear = mYearMonthList[position].first
                mMonth = mYearMonthList[position].second

                mStartButton.text = if (isExistData()) {
                    getString(R.string.button_check_target_year_month_exist)
                } else {
                    getString(R.string.button_check_target_year_month_new)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 何もしない
            }

        }

        val yearMonthPair = CalendarUtil.getCurrentYearMonth()
        val year = yearMonthPair.first
        val month = yearMonthPair.second
        Log.i("current : $year/$month")
        val position = mYearMonthList.indexOf(Pair(year, month))
        if(position >= 0) {
            mSpinner.setSelection(position)
        }

        mStartButton.setOnClickListener {
            val intent = when(mMode) {
                INPUT_MODE_SALARY -> {
                    Intent(this, SalaryActivity::class.java).let {
                        if(isExistData()){
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_ALREADY_EXIST)
                        } else {
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_NEW)
                        }
                    }
                }

                INPUT_MODE_BONUS -> {
                    Intent(this, BonusActivity::class.java).let {
                        if(isExistData()){
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_ALREADY_EXIST)
                        } else {
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_NEW)
                        }
                    }
                }

                else -> return@setOnClickListener
            }
            intent.putExtra(KEY_YEAR, mYear)
            intent.putExtra(KEY_MONTH, mMonth)
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

    /**
     * 2000/01 〜 2050/12の年月の組み合わせを作成する
     */
    private fun createYearMonthList(): List<Pair<Int, Int>> {
        val ret: MutableList<Pair<Int, Int>> = mutableListOf()
        for(year in MIN_YEAR..MAX_YEAR) {
            for(month in YEAR_START_MONTH..YEAR_END_MONTH) {
                ret.add(Pair(year, month))
            }
        }
        return ret
    }

    /**
     * DBに同年月のデータが存在するかチェックし、新規or更新を判定する
     *
     * @return true:存在する, false:存在しない
     */
    private fun isExistData(): Boolean {
        val position = mDBYearMonthList.indexOf(Pair(mYear, mMonth))
        return position >= 0
    }

}