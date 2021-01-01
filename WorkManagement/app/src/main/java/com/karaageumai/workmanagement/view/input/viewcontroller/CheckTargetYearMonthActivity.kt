package com.karaageumai.workmanagement.view.input.viewcontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.IntDef
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.view.*
import com.karaageumai.workmanagement.view.input.viewcontroller.*
import java.util.*

const val KEY_ENTRY_MODE = "KEY_ENTRY_MODE"
const val KEY_YEAR = "KEY_YEAR"
const val KEY_MONTH = "KEY_MONTH"

@IntDef(ENTRY_MODE_NEW, ENTRY_MODE_ALREADY_EXIST, ENTRY_MODE_ERROR)
@Retention(AnnotationRetention.SOURCE)
annotation class EntryMode
const val ENTRY_MODE_NEW = 0
const val ENTRY_MODE_ALREADY_EXIST = 1
const val ENTRY_MODE_ERROR = -1


class CheckTargetYearMonthActivity : AppCompatActivity() {
    // 各View
    private lateinit var mSpinner: Spinner
    private lateinit var mTextView: TextView
    private lateinit var mResultTextView: TextView
    private lateinit var mStartButton: Button

    // 入力されるYYYYmmを保持するプロパティ
    private var mYear: Int = 0
    private var mMonth: Int = 0
    // 新規or更新を判定するフラグ
    private var mIsNewEntry = true
    // 給料orボーナスを判定するフラグ
    private var mMode = INPUT_MODE_SALARY
    // 年と月のPairを管理するためのリスト
    private lateinit var mYearMonthList: List<Pair<Int, Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("onCreate()")
        setContentView(R.layout.activity_check_target_year_month_menu)

        // 給与orボーナスの判定
        mMode = intent.getIntExtra(KEY_INPUT_MODE, INPUT_MODE_ERROR)
        // ツールバー
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // 説明文
        mTextView = findViewById(R.id.tv_normal)
        // スピナー
        mSpinner = findViewById(R.id.sp_target)
        // 入力のチェック結果を出力するTextView
        mResultTextView = findViewById(R.id.tv_message)
        // スタートボタン
        mStartButton = findViewById(R.id.btn_start)
        // 年月リスト
        mYearMonthList = createYearMonthList()

        // モード別の初期化処理
        if (mMode == INPUT_MODE_ERROR) {
            // エラーとみなす
            Log.i("mMode : ERROR")
            val intent = Intent(this, TopMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        val adapter = YearMonthSpinnerAdapter(this, mYearMonthList, mMode)
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 何もしない
            }

        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        Log.i("current : $year/$month")
        val position = mYearMonthList.indexOf(Pair(year, month))
        if(position >= 0) {
            mSpinner.setSelection(position)
        }

        mStartButton.setOnClickListener {
            val intent = when(mMode) {
                INPUT_MODE_SALARY -> {
                    Intent(this, SalaryActivity::class.java).let {
                        if(mIsNewEntry){
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_NEW)
                        } else {
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_ALREADY_EXIST)
                        }
                    }
                }

                INPUT_MODE_BONUS -> {
                    Intent(this, BonusActivity::class.java).let {
                        if(mIsNewEntry){
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_NEW)
                        } else {
                            it.putExtra(KEY_ENTRY_MODE, ENTRY_MODE_ALREADY_EXIST)
                        }
                    }
                }

                else -> return@setOnClickListener
            }
            intent.putExtra(KEY_YEAR, mYear)
            intent.putExtra(KEY_MONTH, mMonth)
            startActivity(intent)
            // 戻ってこられないようにしておく
            finish()
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
        for(year in 2000..2050) {
            for(month in 1..12) {
                ret.add(Pair(year, month))
            }
        }
        return ret
    }

}