package com.karaageumai.workmanagement.view.analyze.annual.top

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.presenter.analyze.annual.AnnualAnalyzePresenter
import com.karaageumai.workmanagement.presenter.analyze.annual.IAnnualAnalyzePresenter
import com.karaageumai.workmanagement.presenter.analyze.annual.util.AnnualDataRow
import com.karaageumai.workmanagement.util.Constants.MAX_YEAR
import com.karaageumai.workmanagement.util.Constants.MIN_YEAR
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.view.analyze.annual.chart.AnnualAnalyzeChartActivity
import com.karaageumai.workmanagement.view.analyze.annual.chart.KEY_IS_WORK_YEAR_MODE
import com.karaageumai.workmanagement.view.analyze.annual.chart.KEY_YEAR
import java.util.*

class AnnualAnalyzeActivity : AppCompatActivity(), IAnnualAnalyze {
    // 年選択用のスピナー
    private lateinit var mSpinner: Spinner
    // データ表示用のテーブル
    private lateinit var mTableLayout: TableLayout
    // Presenter
    private lateinit var mPresenter: IAnnualAnalyzePresenter
    // 現在選択されている年（初期値は実行日の年）
    private var mYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    // 年or年度を選択するトグル
    private lateinit var mToggle: ToggleButton
    // 年or年度を示すフラグ
    private var mIsWorkYearMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_analyze)

        // ツールバー
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = getString(R.string.toolbar_title_annual_analyze)
        setSupportActionBar(toolbar)

        // Presenter初期化
        mPresenter = AnnualAnalyzePresenter(this)

        // レイアウト読み込み
        mSpinner = findViewById(R.id.sp_year)
        mTableLayout = findViewById(R.id.table_annual_data)
        mToggle = findViewById(R.id.toggle_year)

        // トグルの初期化、リスナーセット
        mToggle.isChecked = mIsWorkYearMode
        mToggle.setOnCheckedChangeListener { _, isChecked ->
            Log.i("toggle : $isChecked")
            mIsWorkYearMode = isChecked
            // モードを更新して、データをリロード
            mPresenter.changeMode(mIsWorkYearMode)
            mPresenter.loadData(mYear)
        }

        // スピナー用のリストを作る
        val yearList: MutableList<Int> = mutableListOf()
        for (year in MIN_YEAR..MAX_YEAR) {
            yearList.add(year)
        }
        // スピナー用のアダプター
        val adapter: ArrayAdapter<Int> = ArrayAdapter(this, R.layout.layout_spinner_annual_analyze, yearList)
        mSpinner.adapter = adapter
        // スピナーの初期位置設定
        val initPosition = yearList.indexOf(mYear)
        if(initPosition >= 0) {
            Log.i(initPosition.toString())
            mSpinner.setSelection(initPosition)
        }
        // セレクトリスナー
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 年を更新して、データをリロードする
                mYear = yearList[position]
                mPresenter.loadData(mYear)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 何もしない
            }
        }
        mPresenter.loadData(mYear)

        val comparisonButton: Button = findViewById(R.id.btn_comparison)
        comparisonButton.setOnClickListener {
            val intent = Intent(this, AnnualAnalyzeChartActivity::class.java)
            intent.putExtra(KEY_YEAR, mYear)
            intent.putExtra(KEY_IS_WORK_YEAR_MODE, mIsWorkYearMode)
            startActivity(intent)
        }
    }

    override fun getYear(): Int {
        return mYear
    }

    override fun getIsWorkYearMode(): Boolean {
        return mIsWorkYearMode
    }

    override fun onLoadedData(aLoadDataList: List<AnnualDataRow>) {
        Log.i("onLoadedData()")
        // テーブル初期化
        mTableLayout.removeAllViews()

        // 背景切り替え用のカウンタ
        var countForBackground = 0
        // データを元にテーブルの行を作成
        for (data in aLoadDataList) {
            val tableRow: View = layoutInflater.inflate(R.layout.layout_annual_analyze_row, mTableLayout, false)
            // タイトル
            val title: TextView = tableRow.findViewById(R.id.tv_row_title)
            val titleStr = if(data.isSubItem){
                // 内訳用の行だった場合はタイトル先頭にインデントを挿入する
                getString(R.string.annual_analyze_row_indent) + getString(data.dataNameResId)
            } else {
                getString(data.dataNameResId)
            }
            title.text = titleStr

            // 値
            val value: TextView = tableRow.findViewById(R.id.tv_row_value)
            value.text = NumberFormatUtil.separateThousand(data.dataValue)
            // 単位
            val unit: TextView = tableRow.findViewById(R.id.tv_row_unit)
            unit.setText(data.unitResId)

            if(!data.isSubItem){
                if (countForBackground % 2 == 0) {
                    tableRow.background = ContextCompat.getDrawable(this, R.color.annual_analyze_row_0)
                } else {
                    tableRow.background = ContextCompat.getDrawable(this, R.color.annual_analyze_row_1)
                }
                countForBackground++
            }
            // 親Viewに追加
            mTableLayout.addView(tableRow)
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