package com.karaageumai.workmanagement.view.analyze.annual.chart

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LegendEntry
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
        // 月給（控除前）のチャート
        showIncomePerMonthBeforeDeductionChart()
        // ボーナス（控除前）のチャート
        showBonusPerMonthBeforeDeductionChart()
    }

    /**
     * 勤務日数のチャート
     */
    private fun showWorkingDayChart() {
        // データ
        val barData = createBarData(
                listOf(mPresenter.getWorkingDayData()),
                listOf(getColor(R.color.work_status_chart))
        )

        barData?.let {
            // チャート作成
            val chartView = createBasicBarChart(
                    0f,
                    MAX_DAYS_PER_MONTH.toFloat(),
                    getString(R.string.bar_chart_description_working_day, mYear),
                    getString(R.string.bar_chart_description_working_day_for_work_year, mYear),
                    barData
            ) {
                Log.i("working bar chart long touch")
                mPresenter.showWorkingDayDataDialog()
                true
            }

            mRoot.addView(chartView)
        }
    }

    /**
     * 有給休暇のチャート
     */
    private fun showPaidHolidayChart() {
        // データ
        val barData = createBarData(
                listOf(mPresenter.getPaidHolidayData()),
                listOf(getColor(R.color.work_status_chart))
        )

        barData?.let {
            // チャート作成
            val chartView = createBasicBarChart(
                    0f,
                    MAX_DAYS_PER_MONTH.toFloat(),
                    getString(R.string.bar_chart_description_paid_holiday, mYear),
                    getString(R.string.bar_chart_description_paid_holiday_for_work_year, mYear),
                    barData
            ) {
                Log.i("paidHoliday bar chart long touch")
                mPresenter.showPaidHolidayDataDialog()
                true
            }
            mRoot.addView(chartView)
        }
    }

    private fun showWorkingTimeChart() {
        // データ
        val barData = createBarData(
                listOf(
                        mPresenter.getWorkingBaseTimeData(),
                        mPresenter.getWorkingOverTimeData()
                ),
                listOf(
                        getColor(R.color.work_status_chart),
                        getColor(R.color.work_status_chart_another)
                )
        )

        barData?.let {
            // チャート作成
            val chartView = createBasicBarChart(
                    0f,
                    0f,
                    getString(R.string.bar_chart_description_working_time, mYear),
                    getString(R.string.bar_chart_description_working_time_for_work_year, mYear),
                    barData
            ) {
                Log.i("working time bar chart long touch")
                mPresenter.showWorkingTimeDataDialog()
                true
            }

            // 凡例を追加で設定
            chartView.findViewById<BarChart>(R.id.bar_chart).apply {
                legend.apply {
                    isEnabled = true
                    // 所定労働時間用のエントリー
                    val entry1 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_workstatus_workingtime_title)
                        formColor = getColor(R.color.work_status_chart)
                    }
                    // 残業時間用のエントリー
                    val entry2 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_workstatus_overtime_title)
                        formColor = getColor(R.color.work_status_chart_another)
                    }
                    setCustom(listOf(entry1, entry2))
                    setDrawInside(false)
                }
            }
            mRoot.addView(chartView)
        }
    }

    private fun showIncomePerMonthBeforeDeductionChart() {
        // データ
        val barData = createBarData(
                listOf(
                        mPresenter.getBaseIncomeData(),
                        mPresenter.getOvertimeIncomeData(),
                        mPresenter.getOtherIncomeData()
                ),
                listOf(
                        getColor(R.color.income_chart),
                        getColor(R.color.income_chart_another),
                        getColor(R.color.income_chart_other)
                )
        )

        barData?.let {
            // チャート作成
            val chartView = createBasicBarChart(
                    0f,
                    0f,
                    getString(R.string.bar_chart_description_salary, mYear),
                    getString(R.string.bar_chart_description_salary_for_work_year, mYear),
                    barData
            ) {
                Log.i("incomePerMonthBeforeDeduction bar chart long touch")
                mPresenter.showIncomePerMonthBeforeDeductionDataDialog()
                true
            }

            // 凡例を追加で設定
            chartView.findViewById<BarChart>(R.id.bar_chart).apply {
                legend.apply {
                    isEnabled = true
                    // 基本給のエントリー
                    val entry1 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_income_baseincome_title)
                        formColor = getColor(R.color.income_chart)
                    }
                    // 残業代のエントリー
                    val entry2 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_income_overtime_title)
                        formColor = getColor(R.color.income_chart_another)
                    }
                    // その他収入のエントリー
                    val entry3 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_income_other_title)
                        formColor = getColor(R.color.income_chart_other)
                    }
                    setCustom(listOf(entry1, entry2, entry3))
                    setDrawInside(false)
                }
            }

            mRoot.addView(chartView)
        }
    }

    private fun showBonusPerMonthBeforeDeductionChart() {
        // データ
        val barData = createBarData(
                listOf(
                        mPresenter.getBonusIncomeData(),
                        mPresenter.getOtherBonusIncomeData()
                ),
                listOf(
                        getColor(R.color.income_chart),
                        getColor(R.color.income_chart_other)
                )
        )

        barData?.let {
            // チャート作成
            val chartView = createBasicBarChart(
                    0f,
                    0f,
                    getString(R.string.bar_chart_description_bonus, mYear),
                    getString(R.string.bar_chart_description_bonus_for_work_year, mYear),
                    barData
            ) {
                Log.i("bonusPerMonthBeforeDeduction bar chart long touch")
                //mPresenter.showBonusPerMonthBeforeDeductionDataDialog()
                true
            }

            // 凡例を追加で設定
            chartView.findViewById<BarChart>(R.id.bar_chart).apply {
                legend.apply {
                    isEnabled = true
                    // 基本支給のエントリー
                    val entry1 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_income_baseincome_title)
                        formColor = getColor(R.color.income_chart)
                    }
                    // その他収入のエントリー
                    val entry2 = LegendEntry().apply {
                        label = getString(R.string.layoutitem_income_other_title)
                        formColor = getColor(R.color.income_chart_other)
                    }
                    setCustom(listOf(entry1, entry2))
                    setDrawInside(false)
                }
            }
            mRoot.addView(chartView)
        }
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

    private fun createBarData(aDataList: List<List<Any>>, aColorList: List<Int>): BarData? {
        // 引数チェック
        if ((aDataList.isEmpty()) or (aColorList.isEmpty())) return null

        // BarEntryの作成
        val entryList = mutableListOf<BarEntry>()
        for (i in mDataOrder.indices) {
            val dataParMonth: MutableList<Float> = mutableListOf()
            for (targetDataList in aDataList) {
                val value = targetDataList[i]
                if (value is Int) {
                    // Intの場合はFloatに変換
                    dataParMonth.add(value.toFloat())
                } else if(value is Double) {
                    // Doubleの場合はFloatに変換
                    dataParMonth.add(value.toFloat())
                } else {
                    // 値がInt or Doubleでなかったら終了
                    return null
                }
            }
            entryList.add(BarEntry(mDataOrder[i].toFloat(), dataParMonth.toFloatArray()))
        }

        val barDataSets = mutableListOf<IBarDataSet>()
        val barDataSet = BarDataSet(entryList, "label")
        barDataSet.colors = aColorList
        barDataSet.setDrawValues(false)
        barDataSets.add(barDataSet)

        return BarData(barDataSets)
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