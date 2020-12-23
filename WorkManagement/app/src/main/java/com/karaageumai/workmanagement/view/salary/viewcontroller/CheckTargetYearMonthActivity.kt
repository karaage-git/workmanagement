package com.karaageumai.workmanagement.view.salary.viewcontroller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.util.CalendarUtil

class CheckTargetYearMonthActivity : AppCompatActivity(), TextWatcher {


    companion object {
        const val KEY_CHECK_RESULT: String = "KEY_CHECK_RESULT"
        const val KEY_YEAR_MONTH = "KEY_YEAR_MONTH"
    }

    // 各View
    private lateinit var mEditText: EditText
    private lateinit var mTextView: TextView
    private lateinit var mResultTextView: TextView
    private lateinit var mStartButton: Button

    // ModelFacade
    private val mModelFacade: ModelFacade = ModelFacade

    private lateinit var mYearMonth: String
    private lateinit var mResultStatus: CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate()")

        setContentView(R.layout.activity_check_target_year_month_menu)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_checktargetyearmonth)
        setSupportActionBar(toolbar)

        // 説明文
        mTextView = findViewById(R.id.tv_normal)
        // 入力エリア
        mEditText = findViewById(R.id.et_target)
        // 入力のチェック結果を出力するTextView
        mResultTextView = findViewById(R.id.tv_message)
        // スタートボタン
        mStartButton = findViewById(R.id.btn_start)

        /*
            自身以外にフォーカスが移った際に、キーボードを消す
         */
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
            val intent = Intent(this, SalaryActivity::class.java)
            intent.putExtra(KEY_CHECK_RESULT, mResultStatus)
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

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if(s?.length == 6) {

            mYearMonth = s.toString()
            mResultStatus = mModelFacade.checkYearMonth(mYearMonth)

            // 入力文字が6桁のみのときに判定を行う
            when(mResultStatus) {
                // 新しいエントリー
                CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_NEW_ENTRY -> {
                    mResultTextView.text = getString(R.string.message_yyyymm_result_ok_new)
                    mStartButton.visibility = View.VISIBLE
                }
                // すでに存在する場合
                CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_OK_ALREADY_EXIST -> {
                    mResultTextView.text = getString(R.string.message_yyyymm_result_ok_already)
                    mStartButton.visibility = View.VISIBLE
                }
                // 形式が異なる場合
                CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT -> {
                    mResultTextView.text = getString(R.string.message_yyyymm_illegal)
                    mStartButton.visibility = View.INVISIBLE
                }
                // 範囲外の数値が来た場合
                CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE.RESULT_NG_OUT_OF_RANGE -> {
                    mResultTextView.text = getString(R.string.message_yyyymm_out_range)
                    mStartButton.visibility = View.INVISIBLE
                }
            }
        } else {
            // 結果をクリア
            mResultTextView.text = ""
            mStartButton.visibility = View.INVISIBLE
        }
    }
}