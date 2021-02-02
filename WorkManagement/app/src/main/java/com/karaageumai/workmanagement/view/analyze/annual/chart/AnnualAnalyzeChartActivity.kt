package com.karaageumai.workmanagement.view.analyze.annual.chart

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
import com.karaageumai.workmanagement.presenter.analyze.annual.chart.AnnualAnalyzeChartPresenter
import com.karaageumai.workmanagement.presenter.analyze.annual.chart.IAnnualAnalyzeChartPresenter
import com.karaageumai.workmanagement.util.Constants.MAX_DAYS_PER_MONTH

const val KEY_YEAR = "KEY_YEAR"
const val KEY_IS_WORK_YEAR_MODE = "KEY_IS_WORK_YEAR_MODE"

class AnnualAnalyzeChartActivity : AppCompatActivity(), IAnnualAnalyzeChart {
    // Presenter
    private lateinit var mPresenter: IAnnualAnalyzeChartPresenter
    // 表示年
    private var mYear: Int = 0
    // 年or年度を示すフラグ
    private var mIsWorkYearMode: Boolean = false
    // グラフ表示用のView
    private lateinit var mRoot: LinearLayout
    // データの順序を示す
    private val mDataOrder: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annual_analyze_chart)
        mRoot = findViewById(R.id.ll_root)

        // 全画面から受けたデータを取得
        mYear = intent.getIntExtra(KEY_YEAR, 0)
        mIsWorkYearMode = intent.getBooleanExtra(KEY_IS_WORK_YEAR_MODE, false)
        Log.i("year : $mYear")
        Log.i("mode : $mIsWorkYearMode")
        if (mYear == 0) {
            // エラーとみなす
            finish()
        }

        mPresenter = AnnualAnalyzeChartPresenter(this)

        // 勤務日数のチャート
        showWorkingDayChart()
        // 有給休暇のチャート
        showPaidHolidayChart()
        // 労働時間のチャート
        showWorkingTimeChart()
    }

    /**
     * 勤務日数のチャート
     */
    private fun showWorkingDayChart() {
        // データ
        val y = mPresenter.getWorkingDayData()
        Log.i(y.toString())

        val entryList = mutableListOf<BarEntry>()
        for (i in mDataOrder.indices) {
            entryList.add(BarEntry(mDataOrder[i].toFloat(), y[i].toFloat()))
        }

        val barDataSets = mutableListOf<IBarDataSet>()
        val barDataSet = BarDataSet(entryList, "label")
        barDataSet.color = getColor(R.color.work_status_chart)
        barDataSets.add(barDataSet)

        // チャート作成
        val chartView = createBasicBarChart(
                0f,
                MAX_DAYS_PER_MONTH.toFloat(),
                getString(R.string.bar_chart_description_working_day, mYear),
                getString(R.string.bar_chart_description_working_day_for_work_year, mYear),
                BarData(barDataSets)
        ) {
            Log.i("working bar chart long touch")
            mPresenter.showWorkingDayDataDialog()
            true
        }

        mRoot.addView(chartView)
    }

    /**
     * 有給休暇のチャート
     */
    private fun showPaidHolidayChart() {
        // データ
        val y = mPresenter.getPaidHolidayData()
        Log.i(y.toString())

        val entryList = mutableListOf<BarEntry>()
        for (i in mDataOrder.indices) {
            entryList.add(BarEntry(mDataOrder[i].toFloat(), y[i].toFloat()))
        }

        val barDataSets = mutableListOf<IBarDataSet>()
        val barDataSet = BarDataSet(entryList, "label")
        barDataSet.color = getColor(R.color.work_status_chart)
        barDataSets.add(barDataSet)

        // チャート作成
        val chartView = createBasicBarChart(
                0f,
                MAX_DAYS_PER_MONTH.toFloat(),
                getString(R.string.bar_chart_description_paid_holiday, mYear),
                getString(R.string.bar_chart_description_paid_holiday_for_work_year, mYear),
                BarData(barDataSets)
        ) {
            Log.i("paidHoliday bar chart long touch")
            mPresenter.showPaidHolidayDataDialog()
            true
        }

        mRoot.addView(chartView)
    }

    private fun showWorkingTimeChart() {
        // データ
        val y1: List<Double> = mPresenter.getWorkingBaseTime()
        val y2: List<Double> = mPresenter.getWorkingOverTime()

        val entryList = mutableListOf<BarEntry>()
        for (i in mDataOrder.indices) {
            entryList.add(BarEntry(mDataOrder[i].toFloat(), floatArrayOf(y1[i].toFloat(), y2[i].toFloat())))
        }

        val barDataSets = mutableListOf<IBarDataSet>()
        val barDataSet = BarDataSet(entryList, "label")
        barDataSet.colors = listOf(getColor(R.color.work_status_chart), getColor(R.color.work_status_chart_another))
        barDataSets.add(barDataSet)

        // チャート作成
        val chartView = createBasicBarChart(
                0f,
                0f,
                getString(R.string.bar_chart_description_working_time, mYear),
                getString(R.string.bar_chart_description_working_time_for_work_year, mYear),
                BarData(barDataSets)
        ) {
            Log.i("working time bar chart long touch")
            mPresenter.showWorkingTimeDataDialog()
            true
        }

        mRoot.addView(chartView)
    }

    private fun createBasicBarChart(
            aMinValue: Float,
            aMaxValue: Float,
            aTitle: String,
            aTitleForWorkYear: String,
            aData: BarData,
            aLongClickListener: View.OnLongClickListener
    ): View {
        // Viewの取得
        val view = layoutInflater.inflate(R.layout.layout_bar_chart, mRoot, false)
        // タイトル
        val title: TextView = view.findViewById(R.id.tv_description)
        // BarChart
        val barChartView: BarChart = view.findViewById(R.id.bar_chart)

        // x軸設定
        val xAxis = barChartView.xAxis.apply {
            // 底辺に表示
            position = XAxis.XAxisPosition.BOTTOM
            // 格子線を消す
            setDrawGridLines(false)
            // ラベル名を表示
            setDrawLabels(true)
        }

        // y軸（左）の設定
        barChartView.axisLeft.apply {
            // 最小/最大値
            axisMinimum = aMinValue
            if ((aMaxValue > 0.0) and (aMaxValue > aMinValue)) {
                axisMaximum = aMaxValue
            }
        }

        // y軸（右）の設定
        barChartView.axisRight.apply {
            // ラベル名非表示
            setDrawLabels(false)
            // 格子線非表示
            setDrawGridLines(false)
        }

        barChartView.apply {
            // Description
            description.isEnabled = false
            // 凡例
            legend.isEnabled = false
            // 拡縮無効
            setScaleEnabled(false)
            // データをセット
            data = aData
        }

        // 年or年度で分かれる処理
        if (mIsWorkYearMode) {
            xAxis.valueFormatter = IndexAxisValueFormatter(AnnualAnalyzeChartPresenter.mAxisListForWorkYear)
            title.text = aTitleForWorkYear
        } else {
            xAxis.valueFormatter = IndexAxisValueFormatter(AnnualAnalyzeChartPresenter.mAxisList)
            title.text = aTitle
        }

        // リスナーセット
        barChartView.setOnLongClickListener(aLongClickListener)

        return view
    }

    override fun getYear(): Int {
        return mYear
    }

    override fun getIsWorkYearMode(): Boolean {
        return mIsWorkYearMode
    }

    override fun getActivityContext(): Context {
        return this
    }

}