package com.karaageumai.workmanagement.view.common.viewcontroller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.util.CalendarUtil
import com.karaageumai.workmanagement.view.TopMenuActivity
import com.karaageumai.workmanagement.view.bonus.viewcontroller.BonusActivity
import com.karaageumai.workmanagement.view.salary.viewcontroller.SalaryActivity

const val KEY_YEAR_MONTH = "KEY_YEAR_MONTH"
const val KEY_ENTRY_MODE = "KEY_ENTRY_MODE"

@IntDef(ENTRY_MODE_NEW, ENTRY_MODE_ALREADY_EXIST, ENTRY_MODE_ERROR)
@Retention(AnnotationRetention.SOURCE)
annotation class EntryMode
const val ENTRY_MODE_NEW = 0
const val ENTRY_MODE_ALREADY_EXIST = 1
const val ENTRY_MODE_ERROR = -1


class CheckTargetYearMonthActivity : AppCompatActivity(), TextWatcher {
    // 各View
    private lateinit var mEditText: EditText
    private lateinit var mTextView: TextView
    private lateinit var mResultTextView: TextView
    private lateinit var mStartButton: Button

    // 入力されるYYYYmmを保持するプロパティ
    private var mYearMonth: String = ""
    // データのチェック結果
    private lateinit var mResultStatus: CalendarUtil.CheckFormatResultCode
    // 新規or更新を判定するフラグ
    private var mIsNewEntry = true
    //
    private var mMode = INPUT_MODE_SALARY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate()")

        setContentView(R.layout.activity_check_target_year_month_menu)

        // 給与orボーナスの判定
        mMode = intent.getIntExtra(KEY_INPUT_MODE, INPUT_MODE_ERROR)
        if(mMode == INPUT_MODE_ERROR){
            // エラーだった場合はトップへ戻る
            Log.i("mMode : ERROR")
            val intent = Intent(this, TopMenuActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // ツールバーのタイトルセット
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = when (mMode) {
            INPUT_MODE_SALARY -> getString(R.string.toolbar_title_check_target_year_month_salary)
            INPUT_MODE_BONUS -> getString(R.string.toolbar_title_check_target_year_month_bonus)
            else -> ""
        }
        setSupportActionBar(toolbar)

        // 説明文
        mTextView = findViewById(R.id.tv_normal)
        // 入力エリア
        mEditText = findViewById(R.id.et_target)
        // 入力のチェック結果を出力するTextView
        mResultTextView = findViewById(R.id.tv_message)
        // スタートボタン
        mStartButton = findViewById(R.id.btn_start)

        // 自身以外にフォーカスが移った際に、キーボードを消す
        mEditText.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        mEditText.addTextChangedListener(this)

        // 初期表示は非表示しておく
        mStartButton.visibility = View.INVISIBLE
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
            intent.putExtra(KEY_YEAR_MONTH, mYearMonth)
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 画面がタッチされた際に、強制的に説明文にフォーカスを移す
        // 結果的に、mEditTextのフォーカスが外れ、キーボードが消える
        mTextView.requestFocus()
        return super.dispatchTouchEvent(ev)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // 何もしない
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // 何もしない
    }

    override fun afterTextChanged(s: Editable?) {
        // 入力文字が6桁のみのときに判定を行う
        Log.i("User input : $s")
        if(s?.length == 6) {
            // 入力値を取得
            mYearMonth = s.toString()
            // 入力値チェック
            mResultStatus = CalendarUtil.checkFormat(mYearMonth)
            when(mResultStatus) {
                // YYYYmmが正しい入力の場合
                CalendarUtil.CheckFormatResultCode.RESULT_OK -> {
                    // YYYYmmをスプリット
                    val pair = CalendarUtil.splitYearMonth(mYearMonth)
                    Log.i("year : ${pair.first}, month : ${pair.second}")

                    isExistData(pair.first, pair.second)?.let{
                        if (it) {
                            // 更新：DBにデータが存在する場合
                            Log.i("DB has data at $mYearMonth")
                            mIsNewEntry = false
                            mResultTextView.text = getString(R.string.message_yyyymm_result_ok_already)
                        } else {
                            // 新規：DBにデータが存在しない場合
                            Log.i("DB does not have data at $mYearMonth")
                            mIsNewEntry = true
                            mResultTextView.text = getString(R.string.message_yyyymm_result_ok_new)
                        }
                        mStartButton.visibility = View.VISIBLE
                    }
                }
                // 範囲外の数値が来た場合
                CalendarUtil.CheckFormatResultCode.RESULT_NG_OUT_OF_RANGE -> {
                    Log.i(CalendarUtil.CheckFormatResultCode.RESULT_NG_OUT_OF_RANGE.toString())
                    mResultTextView.text = getString(R.string.message_yyyymm_out_range)
                    mStartButton.visibility = View.INVISIBLE
                }
                // 形式が異なる場合
                else -> {
                    Log.i("format is not match")
                    mResultTextView.text = getString(R.string.message_yyyy_mm_illegal)
                    mStartButton.visibility = View.INVISIBLE
                }
            }
        } else {
            Log.i("length : ${s?.length}")
            // 結果をクリア
            mResultTextView.text = ""
            mStartButton.visibility = View.INVISIBLE
        }
    }

    private fun isExistData(aYear: Int, aMonth: Int): Boolean? {
        return when(mMode) {
            INPUT_MODE_SALARY -> {
                Log.i("INPUT_MODE_SALARY")
                ModelFacade.isExistSalaryInfo(aYear, aMonth)
            }

            INPUT_MODE_BONUS -> {
                Log.i("INPUT_MODE_BONUS")
                ModelFacade.isExistBonusInfo(aYear, aMonth)
            }

            else -> null
        }
    }
}