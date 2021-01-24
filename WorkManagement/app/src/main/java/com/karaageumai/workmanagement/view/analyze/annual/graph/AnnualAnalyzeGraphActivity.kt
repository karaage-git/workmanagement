package com.karaageumai.workmanagement.view.analyze.annual.graph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.presenter.analyze.annual.graph.AnnualAnalyzeGraphPresenter
import com.karaageumai.workmanagement.presenter.analyze.annual.graph.IAnnualAnalyzeGraphPresenter
import com.karaageumai.workmanagement.util.Constants.MAX_DAYS_PER_MONTH

const val KEY_YEAR = "KEY_YEAR"
const val KEY_IS_WORK_YEAR_MODE = "KEY_IS_WORK_YEAR_MODE"

class AnnualAnalyzeGraphActivity : AppCompatActivity(), IAnnualAnalyzeGraph {
    // Presenter
    private lateinit var mPresenter: IAnnualAnalyzeGraphPresenter
    // 表示年
    private var mYear: Int = 0
    // 年or年度を示すフラグ
    private var mIsWorkYearMode: Boolean = false
    // グラフ表示用のView
    private lateinit var mScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_analyze_graph)
        mScrollView = findViewById(R.id.sv_graph)

        // 全画面から受けたデータを取得
        mYear = intent.getIntExtra(KEY_YEAR, 0)
        mIsWorkYearMode = intent.getBooleanExtra(KEY_IS_WORK_YEAR_MODE, false)
        Log.i("year : $mYear")
        Log.i("mode : $mIsWorkYearMode")
        if (mYear == 0) {
            // エラーとみなす
            finish()
        }

        mPresenter = AnnualAnalyzeGraphPresenter(this)

        showWorkingDayChart()

    }

    private fun showWorkingDayChart() {
        // Viewの取得
        val view = layoutInflater.inflate(R.layout.layout_bar_graph, mScrollView, false)
        // タイトル
        val title: TextView = view.findViewById(R.id.tv_description)
        // チャート表示部分
        val barChartView: BarChart = view.findViewById(R.id.bar_chart)


        // x軸の設定
        val xAxis = barChartView.xAxis
        // 底辺に表示
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // 格子線を消す
        xAxis.setDrawGridLines(false)
        // ラベル名を表示
        xAxis.setDrawLabels(true)

        // y軸（左）の設定
        val yAxisLeft = barChartView.axisLeft
        // 最小/最大値
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = MAX_DAYS_PER_MONTH.toFloat()

        // y軸（右）の設定
        val yAxisRight = barChartView.axisRight
        // ラベル名非表示
        yAxisRight.setDrawLabels(false)
        // 格子線非表示
        yAxisRight.setDrawGridLines(false)

        // Description
        barChartView.description.isEnabled = false
        // 凡例
        barChartView.legend.isEnabled = false

        // データ
        val x: List<Int>
        val y = mPresenter.getWorkingDayData()

        // 年or年度で分かれる処理
        if (mIsWorkYearMode) {
            x = AnnualAnalyzeGraphPresenter.mMonthListForWorkYear
            xAxis.valueFormatter = IndexAxisValueFormatter(AnnualAnalyzeGraphPresenter.mAxisListForWorkYear)
            title.text = getString(R.string.bar_chart_description_working_day_for_work_year, mYear)
        } else {
            x = AnnualAnalyzeGraphPresenter.mMonthList
            xAxis.valueFormatter = IndexAxisValueFormatter(AnnualAnalyzeGraphPresenter.mAxisList)
            title.text = getString(R.string.bar_chart_description_working_day, mYear)
        }

        val entryList = mutableListOf<BarEntry>()
        for (i in x.indices) {
            entryList.add(BarEntry(x[i].toFloat(), y[i].toFloat()))
        }

        val barDataSets = mutableListOf<IBarDataSet>()
        val barDataSet = BarDataSet(entryList, "label")
        barDataSet.color = getColor(R.color.work_status_chart)

        barDataSets.add(barDataSet)
        val barData = BarData(barDataSets)
        barChartView.data = barData
        mScrollView.addView(view)
    }

    override fun getYear(): Int {
        return mYear
    }

    override fun getIsWorkYearMode(): Boolean {
        return mIsWorkYearMode
    }

}