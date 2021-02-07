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
import com.karaageumai.workmanagement.util.NumberFormatUtil
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
            val view = createTwoColumnRowDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    false,
                    R.color.work_status_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_day,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_day_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.empty),
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
            val view = createTwoColumnRowDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    false,
                    R.color.work_status_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_paid_holiday,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_paid_holiday_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.empty),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_paid_holiday),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showWorkingTimeDataDialog() {
        mActivity.get()?.let { activity ->
            val rowDataList: MutableList<FourColumnData> = mutableListOf()
            for (data in mSalaryInfoList) {
                // データ用の行
                val rowData = FourColumnData(
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_month,
                                data.month
                        ),
                        (data.workingTime / 10.0).toString(),
                        (data.overtime / 10.0).toString(),
                        ((data.workingTime + data.overtime) / 10.0).toString()
                )
                rowDataList.add(rowData)
            }

            // ダイアログ用のViewを取得
            val view = createFourColumnDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    true,
                    R.color.work_status_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_time,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_working_time_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.empty),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_working_time),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_overtime),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_sum_time_per_month),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showIncomePerMonthBeforeDeductionDataDialog() {
        mActivity.get()?.let { activity ->
            val rowDataList: MutableList<FiveColumnData> = mutableListOf()
            for (data in mSalaryInfoList) {
                // データ用の行
                val rowData = FiveColumnData(
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_month,
                                data.month
                        ),
                        NumberFormatUtil.separateThousand(data.baseIncome.toString()),
                        NumberFormatUtil.separateThousand(data.overtimeIncome.toString()),
                        NumberFormatUtil.separateThousand(data.otherIncome.toString()),
                        NumberFormatUtil.separateThousand(
                                (data.baseIncome + data.overtimeIncome + data.otherIncome).toString()
                        )
                )
                rowDataList.add(rowData)
            }

            // ダイアログ用のViewを取得
            val view = createFiveColumnDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    true,
                    R.color.income_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_salary,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_salary_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.empty),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_base_income),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_overtime_income),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_other_income),
                    activity.getActivityContext().getString(R.string.chart_dialog_title_sum_money_per_month),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showBonusPerMonthBeforeDeductionDataDialog() {
        mActivity.get()?.let { activity ->
            val rowDataList: MutableList<FourColumnData> = mutableListOf()
            for (data in mBonusInfoList) {
                // データ用の行
                val rowData = FourColumnData(
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_value_month,
                                data.month
                        ),
                        NumberFormatUtil.separateThousand(data.baseIncome.toString()),
                        NumberFormatUtil.separateThousand(data.otherIncome.toString()),
                        NumberFormatUtil.separateThousand(
                                (data.baseIncome + data.otherIncome).toString()
                        )
                )
                rowDataList.add(rowData)
            }

            // ダイアログ用のViewを取得
            val view = createFourColumnDataView(
                    activity.getActivityContext(),
                    activity.getLayoutInflater(),
                    true,
                    R.color.income_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_bonus,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_bonus_for_work_year,
                            mYear
                    ),
                    activity.getActivityContext().getString(R.string.empty),
                    activity.getActivityContext().getString(
                            R.string.chart_dialog_title_base_income
                    ),
                    activity.getActivityContext().getString(
                            R.string.chart_dialog_title_other_income
                    ),
                    activity.getActivityContext().getString(
                            R.string.chart_dialog_title_sum_money_per_month
                    ),
                    rowDataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    /**
     * 2列データ表示用のView作成メソッド
     *
     * @param aContext
     * @param aLayoutInflater
     * @param aIsScrollable
     * @param aSeparateColor 表の偶数行に使用する背景色
     * @param aTitle 表のタイトル
     * @param aTitleForWorkYear 表のタイトル（年度用）
     * @param aFirstColumnTitle 1列目のColumn名
     * @param aSecondColumnTitle  2列目のColumn名
     * @param aDataList データリスト
     */
    private fun createTwoColumnRowDataView(
            aContext: Context,
            aLayoutInflater: LayoutInflater,
            aIsScrollable: Boolean,
            aSeparateColor: Int,
            aTitle: String,
            aTitleForWorkYear: String,
            aFirstColumnTitle: String,
            aSecondColumnTitle: String,
            aDataList: List<TwoColumnData>
    ): View {
        // ダイアログ用のViewを取得
        val view = createDialogView(aContext, aIsScrollable)

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
                    setBackgroundColor(aContext.getColor(aSeparateColor))
                }
            }
            tableLayout.addView(dataRow)
        }
        return view
    }

    /**
     * 3列データ表示用のView作成メソッド
     *
     * @param aContext
     * @param aLayoutInflater
     * @param aIsScrollable
     * @param aSeparateColor 表の偶数行に使用する背景色
     * @param aTitle 表のタイトル
     * @param aTitleForWorkYear 表のタイトル（年度用）
     * @param aFirstColumnTitle 1列目のColumn名
     * @param aSecondColumnTitle  2列目のColumn名
     * @param aThirdColumnTitle  3列目のColumn名
     * @param aDataList データリスト
     */
    private fun createThreeColumnDataView(
            aContext: Context,
            aLayoutInflater: LayoutInflater,
            aIsScrollable: Boolean,
            aSeparateColor: Int,
            aTitle: String,
            aTitleForWorkYear: String,
            aFirstColumnTitle: String,
            aSecondColumnTitle: String,
            aThirdColumnTitle: String,
            aDataList: List<ThreeColumnData>
    ): View {
        // ダイアログ用のViewを取得
        val view = createDialogView(aContext, aIsScrollable)

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
                    setBackgroundColor(aContext.getColor(aSeparateColor))
                }
            }
            tableLayout.addView(dataRow)
        }
        return view
    }

    /**
     * 4列データ表示用のView作成メソッド
     *
     * @param aContext
     * @param aLayoutInflater
     * @param aIsScrollable
     * @param aSeparateColor 表の偶数行に使用する背景色
     * @param aTitle 表のタイトル
     * @param aTitleForWorkYear 表のタイトル（年度用）
     * @param aFirstColumnTitle 1列目のColumn名
     * @param aSecondColumnTitle  2列目のColumn名
     * @param aThirdColumnTitle  3列目のColumn名
     * @param aFourthColumnTitle  4列目のColumn名
     * @param aDataList データリスト
     */
    private fun createFourColumnDataView(
            aContext: Context,
            aLayoutInflater: LayoutInflater,
            aIsScrollable: Boolean,
            aSeparateColor: Int,
            aTitle: String,
            aTitleForWorkYear: String,
            aFirstColumnTitle: String,
            aSecondColumnTitle: String,
            aThirdColumnTitle: String,
            aFourthColumnTitle: String,
            aDataList: List<FourColumnData>
    ): View {
        // ダイアログ用のViewを取得
        val view = createDialogView(aContext, aIsScrollable)

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
                R.layout.layout_dialog_chart_four_column,
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
            findViewById<TextView>(R.id.tv_row_fourth).apply {
                text = aFourthColumnTitle
            }
        }
        // タイトル追加
        tableLayout.addView(titleRow)

        // データをセット
        for ((count, data) in aDataList.withIndex()) {
            // データ用の行
            val dataRow = aLayoutInflater.inflate(
                    R.layout.layout_dialog_chart_four_column,
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
                findViewById<TextView>(R.id.tv_row_fourth).apply {
                    text = data.fourthColumnValue
                }
                if ((count % 2) == 0) {
                    setBackgroundColor(aContext.getColor(aSeparateColor))
                }
            }
            tableLayout.addView(dataRow)
        }
        return view
    }

    /**
     * 5列データ表示用のView作成メソッド
     *
     * @param aContext
     * @param aLayoutInflater
     * @param aIsScrollable
     * @param aSeparateColor 表の偶数行に使用する背景色
     * @param aTitle 表のタイトル
     * @param aTitleForWorkYear 表のタイトル（年度用）
     * @param aFirstColumnTitle 1列目のColumn名
     * @param aSecondColumnTitle  2列目のColumn名
     * @param aThirdColumnTitle  3列目のColumn名
     * @param aFourthColumnTitle  4列目のColumn名
     * @param aFifthColumnTitle 5列目のColumn名
     * @param aDataList データリスト
     */
    private fun createFiveColumnDataView(
            aContext: Context,
            aLayoutInflater: LayoutInflater,
            aIsScrollable: Boolean,
            aSeparateColor: Int,
            aTitle: String,
            aTitleForWorkYear: String,
            aFirstColumnTitle: String,
            aSecondColumnTitle: String,
            aThirdColumnTitle: String,
            aFourthColumnTitle: String,
            aFifthColumnTitle: String,
            aDataList: List<FiveColumnData>
    ): View {
        // ダイアログ用のViewを取得
        val view = createDialogView(aContext, aIsScrollable)

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
                R.layout.layout_dialog_chart_five_column,
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
            findViewById<TextView>(R.id.tv_row_fourth).apply {
                text = aFourthColumnTitle
            }
            findViewById<TextView>(R.id.tv_row_fifth).apply {
                text = aFifthColumnTitle
            }
        }
        // タイトル追加
        tableLayout.addView(titleRow)

        // データをセット
        for ((count, data) in aDataList.withIndex()) {
            // データ用の行
            val dataRow = aLayoutInflater.inflate(
                    R.layout.layout_dialog_chart_five_column,
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
                findViewById<TextView>(R.id.tv_row_fourth).apply {
                    text = data.fourthColumnValue
                }
                findViewById<TextView>(R.id.tv_row_fifth).apply {
                    text = data.fifthColumnValue
                }
                if ((count % 2) == 0) {
                    setBackgroundColor(aContext.getColor(aSeparateColor))
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

    override fun getWorkingBaseTimeData(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.workingTime / 10.0)
        }
        return retList
    }

    override fun getWorkingOverTimeData(): List<Double> {
        val retList: MutableList<Double> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.overtime / 10.0)
        }
        return retList
    }

    override fun getBaseIncomeData(): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.baseIncome)
        }
        return retList
    }

    override fun getOvertimeIncomeData(): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.overtimeIncome)
        }
        return retList
    }

    override fun getOtherIncomeData(): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.otherIncome)
        }
        return retList
    }

    override fun getBonusIncomeData(): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        for (data in mBonusInfoList) {
            retList.add(data.baseIncome)
        }
        return retList
    }

    override fun getOtherBonusIncomeData(): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        for (data in mBonusInfoList) {
            retList.add(data.otherIncome)
        }
        return retList
    }

    /**
     * 引数で与えられたフラグに基づいて、ダイアログ用のViewを作成する
     */
    private fun createDialogView(aContext: Context, aIsScrollable: Boolean): View{
        return if (aIsScrollable) {
            View.inflate(aContext, R.layout.layout_dialog_chart_data_scrollable, null)
        } else {
            View.inflate(aContext, R.layout.layout_dialog_chart_data, null)
        }
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

    /**
     * 4列で表せるデータを扱うためのクラス
     */
    data class FourColumnData(
            val firstColumnValue: String,
            val secondColumnValue: String,
            val thirdColumnValue: String,
            val fourthColumnValue: String
    )

    /**
     * 5列で表せるデータを扱うためのクラス
     */
    data class FiveColumnData(
            val firstColumnValue: String,
            val secondColumnValue: String,
            val thirdColumnValue: String,
            val fourthColumnValue: String,
            val fifthColumnValue: String
    )
}