package com.karaageumai.workmanagement.presenter.analyze.annual.chart

import android.app.AlertDialog
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.Constants.WORK_YEAR_END_MONTH
import com.karaageumai.workmanagement.util.Constants.WORK_YEAR_START_MONTH
import com.karaageumai.workmanagement.view.analyze.annual.chart.IAnnualAnalyzeChart
import java.lang.ref.WeakReference

class AnnualAnalyzeChartPresenter(aActivity: IAnnualAnalyzeChart) : IAnnualAnalyzeChartPresenter {
    // Activity
    private val mActivity: WeakReference<IAnnualAnalyzeChart> = WeakReference(aActivity)
    // SalaryInfoのリスト
    private val mSalaryInfoList: List<SalaryInfo>
    // BonusInfoのリスト
    private val mBonusInfoList: List<BonusInfo>
    // 表示する年
    private val mYear: Int
    // 年or年度を表すフラグ
    private val mIsWorkYearMode: Boolean

    companion object {
        // 年の順番で月を保持するリスト
        val mMonthList: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        // 年用グラフのx軸
        val mAxisList: List<String> =
                listOf("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        // 年度の順番で月を保持するリスト
        val mMonthListForWorkYear: List<Int> = listOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3)
        // 年度用グラフのx軸
        val mAxisListForWorkYear: List<String> =
                listOf("", "4", "5", "6", "7", "8", "9", "10", "11", "12", "1", "2", "3")
    }

    init {
        val activity = mActivity.get()
        if(activity != null) {
            mYear = activity.getYear()
            mIsWorkYearMode = activity.getIsWorkYearMode()
            mSalaryInfoList = createSalaryInfoList()
            mBonusInfoList = createBonusInfoList()
        } else {
            mYear = 0
            mIsWorkYearMode = false
            mSalaryInfoList = listOf()
            mBonusInfoList = listOf()
        }
    }

    /**
     * DBからSalaryInfoのリストを取得し、12ヶ月分のデータのリストに整形する
     * （データが無い場合は、当該年月分データは空データで埋める）
     */
    private fun createSalaryInfoList(): List<SalaryInfo> {
        val retList: MutableList<SalaryInfo> = mutableListOf()

        if (mIsWorkYearMode) {
            val dataList = ModelFacade.selectSalaryInfo(
                    mYear,
                    WORK_YEAR_START_MONTH,
                    mYear + 1,
                    WORK_YEAR_END_MONTH
            )
            loop@ for (month in mMonthListForWorkYear) {
                val year = if (month >= WORK_YEAR_START_MONTH) {
                    mYear
                } else {
                    mYear + 1
                }
                for (data in dataList) {
                    if ((data.year == year) and (data.month == month)) {
                        retList.add(data)
                        continue@loop
                    }
                }
                retList.add(SalaryInfo(0, year, month))
            }

        } else {
            val dataList = ModelFacade.selectSalaryInfo(mYear)
            loop@ for (month in mMonthList) {
                for (data in dataList) {
                    if ((data.year == mYear) and (data.month == month)) {
                        retList.add(data)
                        continue@loop
                    }
                }
                retList.add(SalaryInfo(0, mYear, month))
            }
        }
        return retList
    }

    /**
     * DBからBonusInfoのリストを取得し、12ヶ月分のデータのリストに整形する
     * （データが無い場合は、当該年月分データは空データで埋める）
     */
    private fun createBonusInfoList(): List<BonusInfo> {
        val retList: MutableList<BonusInfo> = mutableListOf()
        if (mIsWorkYearMode) {
            val dataList = ModelFacade.selectBonusInfo(
                    mYear,
                    WORK_YEAR_START_MONTH,
                    mYear + 1,
                    WORK_YEAR_END_MONTH
            )
            loop@ for (month in mMonthListForWorkYear) {
                val year = if (month >= WORK_YEAR_START_MONTH) {
                    mYear
                } else {
                    mYear + 1
                }
                for (data in dataList) {
                    if ((data.year == year) and (data.month == month)) {
                        retList.add(data)
                        continue@loop
                    }
                }
                retList.add(BonusInfo(0, year, month))
            }

        } else {
            val dataList = ModelFacade.selectBonusInfo(mYear)
            loop@ for (month in mMonthList) {
                for (data in dataList) {
                    if ((data.year == mYear) and (data.month == month)) {
                        retList.add(data)
                        continue@loop
                    }
                }
                retList.add(BonusInfo(0, mYear, month))
            }
        }
        return retList
    }

    override fun showWorkingDayDataDialog() {
        mActivity.get()?.let { activity ->
            // ダイアログ用のViewを取得
            val view = View.inflate(
                    activity.getActivityContext(),
                    R.layout.layout_dialog_chart_data,
                    null
            )

            // 説明文をセット
            view.findViewById<TextView>(R.id.tv_title).text =
                    activity.getActivityContext().getString(
                            R.string.chart_dialog_description_working_day,
                            mYear
                    )

            // 行を追加するためのテーブル
            val tableLayout: TableLayout = view.findViewById(R.id.table_data)
            // タイトル行
            val titleRow = activity.getLayoutInflater().inflate(
                    R.layout.layout_dialog_chart_working_time_row,
                    tableLayout,
                    false
            ).apply {
                val month: TextView = findViewById(R.id.tv_row_month)
                month.setText(R.string.chart_dialog_title_month)
                val value: TextView = findViewById(R.id.tv_row_value)
                value.setText(R.string.chart_dialog_title_working_day)
            }
            // タイトル追加
            tableLayout.addView(titleRow)

            // 行の色切り替え用のカウンタ
            for ((count, data) in mSalaryInfoList.withIndex()) {
                // データ用の行
                val dataRow = activity.getLayoutInflater().inflate(
                        R.layout.layout_dialog_chart_working_time_row,
                        tableLayout,
                        false
                ).apply {
                    val month: TextView = findViewById(R.id.tv_row_month)
                    month.text = activity.getActivityContext().getString(
                            R.string.chart_dialog_value_month,
                            data.month
                    )
                    val value: TextView = findViewById(R.id.tv_row_value)
                    value.text = activity.getActivityContext().getString(
                            R.string.chart_dialog_value_working_day,
                            (data.workingDay / 10.0).toString()
                    )
                    if ((count % 2) == 0) {
                        setBackgroundColor(activity.getActivityContext().getColor(R.color.work_status_basic))
                    }
                }
                tableLayout.addView(dataRow)
            }

            AlertDialog.Builder(activity.getActivityContext())
                    .setView(view)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun getWorkingDayData(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.workingDay / 10.0)
        }
        return retList
    }

    override fun getWorkingBaseTime(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.workingTime / 10.0)
        }
        return retList
    }

    override fun getWorkingOverTime(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.overtime / 10.0)
        }
        return retList
    }

}