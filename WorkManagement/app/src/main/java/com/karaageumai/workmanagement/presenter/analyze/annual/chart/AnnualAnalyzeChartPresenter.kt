package com.karaageumai.workmanagement.presenter.analyze.annual.chart

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
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
            val rowDataList: MutableList<TwoColumnData> = mutableListOf()
            for (data in mSalaryInfoList) {
                // データ用の行
                val rowData = TwoColumnData(
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_month,
                                data.month
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_day,
                                (data.workingDay / 10.0).toString()
                        )
                )
                rowDataList.add(rowData)
            }

            // ダイアログ用のViewを取得
            val view = createTwoRowDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_day,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_day_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_month),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_working_day),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showPaidHolidayDataDialog() {
        mActivity.get()?.let { activity ->
            val rowDataList: MutableList<TwoColumnData> = mutableListOf()
            for (data in mSalaryInfoList) {
                // データ用の行
                val rowData = TwoColumnData(
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_month,
                                data.month
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_day,
                                (data.paidHolidays / 10.0).toString()
                        )
                )
                rowDataList.add(rowData)
            }

            // ダイアログ用のViewを取得
            val view = createTwoRowDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_paid_holiday,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_paid_holiday_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_month),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_paid_holiday),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showWorkingTimeDataDialog() {
        mActivity.get()?.let { activity ->
            val rowDataList: MutableList<ThreeColumnData> = mutableListOf()
            for (data in mSalaryInfoList) {
                // データ用の行
                val rowData = ThreeColumnData(
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_month,
                                data.month
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_time,
                                (data.workingTime / 10.0).toString()
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_time,
                                (data.overtime / 10.0).toString()
                        )
                )
                rowDataList.add(rowData)
            }

            // ダイアログ用のViewを取得
            val view = createThreeRowDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_time,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_time_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_month),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_working_time),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_overtime),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    private fun createTwoRowDataView(
            aContext: Context,
            aLayoutInflater: LayoutInflater,
            aTitle: String,
            aTitleForWorkYear: String,
            aFirstColumnTitle: String,
            aSecondColumnTitle: String,
            aDataList: List<TwoColumnData>
    ): View {
        // ダイアログ用のViewを取得
        val view = View.inflate(aContext, R.layout.layout_dialog_chart_data, null)

        // タイトルをセット
        view.findViewById<TextView>(R.id.tv_title).text =
                if (mIsWorkYearMode) {
                    aTitleForWorkYear
                } else {
                    aTitle
                }
        // 行を追加するためのテーブル
        val tableLayout: TableLayout = view.findViewById(R.id.table_data)
        // タイトル行
        val titleRow = aLayoutInflater.inflate(
                R.layout.layout_dialog_chart_two_column,
                tableLayout,
                false
        ).apply {
            findViewById<TextView>(R.id.tv_row_first).apply {
                text = aFirstColumnTitle
            }
            findViewById<TextView>(R.id.tv_row_second).apply {
                text = aSecondColumnTitle
            }
        }
        // タイトル追加
        tableLayout.addView(titleRow)

        // データをセット
        for ((count, data) in aDataList.withIndex()) {
            // データ用の行
            val dataRow = aLayoutInflater.inflate(
                    R.layout.layout_dialog_chart_two_column,
                    tableLayout,
                    false
            ).apply {
                findViewById<TextView>(R.id.tv_row_first).apply {
                    text = data.firstColumnValue
                }
                findViewById<TextView>(R.id.tv_row_second).apply {
                    text = data.secondColumnValue
                }
                if ((count % 2) == 0) {
                    setBackgroundColor(aContext.getColor(R.color.work_status_basic))
                }
            }
            tableLayout.addView(dataRow)
        }
        return view
    }

    private fun createThreeRowDataView(
            aContext: Context,
            aLayoutInflater: LayoutInflater,
            aTitle: String,
            aTitleForWorkYear: String,
            aFirstColumnTitle: String,
            aSecondColumnTitle: String,
            aThirdColumnTitle: String,
            aDataList: List<ThreeColumnData>
    ): View {
        // ダイアログ用のViewを取得
        val view = View.inflate(aContext, R.layout.layout_dialog_chart_data, null)

        // タイトルをセット
        view.findViewById<TextView>(R.id.tv_title).text =
                if (mIsWorkYearMode) {
                    aTitleForWorkYear
                } else {
                    aTitle
                }
        // 行を追加するためのテーブル
        val tableLayout: TableLayout = view.findViewById(R.id.table_data)
        // タイトル行
        val titleRow = aLayoutInflater.inflate(
                R.layout.layout_dialog_chart_three_column,
                tableLayout,
                false
        ).apply {
            findViewById<TextView>(R.id.tv_row_first).apply {
                text = aFirstColumnTitle
            }
            findViewById<TextView>(R.id.tv_row_second).apply {
                text = aSecondColumnTitle
            }
            findViewById<TextView>(R.id.tv_row_third).apply {
                text = aThirdColumnTitle
            }
        }
        // タイトル追加
        tableLayout.addView(titleRow)

        // データをセット
        for ((count, data) in aDataList.withIndex()) {
            // データ用の行
            val dataRow = aLayoutInflater.inflate(
                    R.layout.layout_dialog_chart_three_column,
                    tableLayout,
                    false
            ).apply {
                findViewById<TextView>(R.id.tv_row_first).apply {
                    text = data.firstColumnValue
                }
                findViewById<TextView>(R.id.tv_row_second).apply {
                    text = data.secondColumnValue
                }
                findViewById<TextView>(R.id.tv_row_third).apply {
                    text = data.thirdColumnValue
                }
                if ((count % 2) == 0) {
                    setBackgroundColor(aContext.getColor(R.color.work_status_basic))
                }
            }
            tableLayout.addView(dataRow)
        }
        return view
    }

    /**
     * Dialog表示
     */
    private fun showDialog(aContext: Context, aView: View) {
        AlertDialog.Builder(aContext)
                .setView(aView)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    override fun getWorkingDayData(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.workingDay / 10.0)
        }
        return retList
    }

    override fun getPaidHolidayData(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.paidHolidays / 10.0)
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

    /**
     * 2列で表せるデータを扱うためのクラス
     */
    data class TwoColumnData(
            val firstColumnValue: String,
            val secondColumnValue: String
    )

    /**
     * 3列で表せるデータを扱うためのクラス
     */
    data class ThreeColumnData(
            val firstColumnValue: String,
            val secondColumnValue: String,
            val thirdColumnValue: String
    )
}