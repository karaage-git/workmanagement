package com.karaageumai.workmanagement.view.resister.salary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade

class CheckTargetYearMonthActivity : AppCompatActivity(), TextWatcher {

    private lateinit var mEditText: EditText
    private lateinit var mTextView: TextView
    private val mModelFacade: ModelFacade = ModelFacade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("onCreate()")

        setContentView(R.layout.activity_check_target_year_month_menu)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.resister_normal_menu)
        setSupportActionBar(toolbar)

        // 説明文
        mTextView = findViewById(R.id.tv_normal)

        // 入力エリア
        /*
            自身以外にフォーカスが移った際に、キーボードを消す
         */
        mEditText = findViewById(R.id.et_target)
        mEditText.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
        mEditText.addTextChangedListener(this)


        val startButton: Button = findViewById(R.id.btn_start)
        startButton.setOnClickListener {

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
            val result = mModelFacade.checkYearMonth(s.toString())
            Log.i(result.toString())
        }
    }
}