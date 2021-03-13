package com.karaageumai.workmanagement.presenter.analyze.annual.chart

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
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
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_working_day)
                ))
                // データ行
                for (data in mSalaryInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 勤務日数
                            (data.workingDay / 10.0).toString()
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
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
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showPaidHolidayDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_paid_holiday)
                ))
                // データ行
                for (data in mSalaryInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 有給取得数
                            (data.paidHolidays / 10.0).toString()
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
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
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showWorkingTimeDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_working_time),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_overtime),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_sum_time_per_month)
                ))
                // データ行
                for (data in mSalaryInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 基本労働時間
                            (data.workingTime / 10.0).toString(),
                            // 残業時間
                            (data.overtime / 10.0).toString(),
                            // 月別の合計労働時間
                            ((data.workingTime + data.overtime) / 10.0).toString()
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
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
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showIncomePerMonthBeforeDeductionDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_base_income
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_overtime_income
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_other
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_sum_money_per_month
                        )
                ))
                // データ行
                for (data in mSalaryInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 基本給
                            NumberFormatUtil.separateThousand(data.baseIncome.toString()),
                            // 残業手当
                            NumberFormatUtil.separateThousand(data.overtimeIncome.toString()),
                            // その他
                            NumberFormatUtil.separateThousand(data.otherIncome.toString()),
                            // 合計
                            NumberFormatUtil.separateThousand(getSumIncome(data).toString())
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
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
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showBonusPerMonthBeforeDeductionDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_base_income
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_other
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_sum_money_per_month
                        )
                ))
                // データ行
                for (data in mBonusInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 基本支給
                            NumberFormatUtil.separateThousand(data.baseIncome.toString()),
                            // その他
                            NumberFormatUtil.separateThousand(data.otherIncome.toString()),
                            // 合計
                            NumberFormatUtil.separateThousand(getSumIncome(data).toString())
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
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
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showDeductionDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_health_insurance
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_long_term_care_insurance
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_pension
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_employment_insurance
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_income_tax
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_resident_tax
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_other
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_sum_money_per_month
                        )
                ))
                // データ行
                for (data in mSalaryInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 健康保険
                            NumberFormatUtil.separateThousand(data.healthInsuranceFee.toString()),
                            // 介護保険
                            NumberFormatUtil.separateThousand(data.longTermCareInsuranceFee.toString()),
                            // 年金保険
                            NumberFormatUtil.separateThousand(data.pensionFee.toString()),
                            // 雇用保険
                            NumberFormatUtil.separateThousand(data.employmentInsuranceFee.toString()),
                            // 所得税
                            NumberFormatUtil.separateThousand(data.incomeTax.toString()),
                            // 住民税
                            NumberFormatUtil.separateThousand(data.residentTax.toString()),
                            // その他
                            NumberFormatUtil.separateThousand(data.otherDeduction.toString()),
                            // 合計
                            NumberFormatUtil.separateThousand(getSumDeduction(data).toString())
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
                    true,
                    R.color.deduction_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_deduction,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_deduction_for_work_year,
                            mYear
                    ),
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showBonusDeductionDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_health_insurance
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_long_term_care_insurance
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_pension
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_employment_insurance
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_income_tax
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_other
                        ),
                        activity.getActivityContext().getString(
                                R.string.chart_dialog_title_sum_money_per_month
                        )
                ))
                // データ行
                for (data in mBonusInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 健康保険
                            NumberFormatUtil.separateThousand(data.healthInsuranceFee.toString()),
                            // 介護保険
                            NumberFormatUtil.separateThousand(data.longTermCareInsuranceFee.toString()),
                            // 年金保険
                            NumberFormatUtil.separateThousand(data.pensionFee.toString()),
                            // 雇用保険
                            NumberFormatUtil.separateThousand(data.employmentInsuranceFee.toString()),
                            // 所得税
                            NumberFormatUtil.separateThousand(data.incomeTax.toString()),
                            // その他
                            NumberFormatUtil.separateThousand(data.otherDeduction.toString()),
                            // 合計
                            NumberFormatUtil.separateThousand(getSumDeduction(data).toString())
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
                    true,
                    R.color.deduction_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_bonus_deduction,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_bonus_deduction_for_work_year,
                            mYear
                    ),
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showAfterTaxDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_after_tax)
                ))
                // データ行
                for (data in mSalaryInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 手取り
                            NumberFormatUtil.separateThousand(
                                    (getSumIncome(data) - getSumDeduction(data)).toString()
                            )
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
                    false,
                    R.color.income_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_after_tax,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_after_tax_for_work_year,
                            mYear
                    ),
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    override fun showBonusAfterTaxDataDialog() {
        mActivity.get()?.let { activity ->
            val dataList: MutableList<List<String>> = mutableListOf()
            dataList.apply {
                // タイトル行
                add(listOf(
                        activity.getActivityContext().getString(R.string.empty),
                        activity.getActivityContext().getString(R.string.chart_dialog_title_after_tax)
                ))
                // データ行
                for (data in mBonusInfoList) {
                    add(listOf(
                            // 月
                            activity.getActivityContext().getString(
                                    R.string.chart_dialog_value_month,
                                    data.month
                            ),
                            // 手取り
                            NumberFormatUtil.separateThousand(
                                    (getSumIncome(data) - getSumDeduction(data)).toString()
                            )
                    ))
                }
            }

            // ダイアログ用のViewにデータをセット
            val view = createDataDetailsView(
                    activity.getActivityContext(),
                    false,
                    R.color.income_basic,
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_bonus_after_tax,
                            mYear
                    ),
                    activity.getActivityContext().getString(
                            R.string.bar_chart_description_bonus_after_tax_for_work_year,
                            mYear
                    ),
                    dataList
            )
            showDialog(activity.getActivityContext(), view)
        }
    }

    private fun createDataDetailsView(
            aContext: Context,
            aIsScrollable: Boolean,
            aSeparateColor: Int,
            aTitle: String,
            aTitleForWorkYear: String,
            aDataList: List<List<String>>
    ): View {
        // ダイアログ用のViewを取得
        val view = if (aIsScrollable) {
            View.inflate(aContext, R.layout.layout_dialog_chart_data_scrollable, null)
        } else {
            View.inflate(aContext, R.layout.layout_dialog_chart_data, null)
        }

        // タイトルをセット
        view.findViewById<TextView>(R.id.tv_title).text =
                if (mIsWorkYearMode) {
                    aTitleForWorkYear
                } else {
                    aTitle
                }
        // 行を追加するためのテーブル
        val tableLayout: TableLayout = view.findViewById(R.id.table_data)

        // データをセット
        for ((count, rowData) in aDataList.withIndex()) {
            val tableRowLayout = View.inflate(
                    aContext,
                    R.layout.layout_dialog_chart_table_row,
                    null
            )
            val tableRow: TableRow = tableRowLayout.findViewById(R.id.tr_chart)
            for (data in rowData) {
                val column = View.inflate(aContext, R.layout.layout_dialog_chart_table_column, null)
                val textView: TextView = column.findViewById(R.id.tv_column)
                textView.text = data

                // タイトル行（最上段の行）は中央寄せ
                if (count == 0) {
                    textView.gravity = Gravity.CENTER
                }

                tableRow.addView(column)
            }
            if ((count % 2) == 1) {
                tableRow.setBackgroundColor(aContext.getColor(aSeparateColor))
            }

            tableLayout.addView(tableRow)
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

    override fun getBaseIncomeData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.baseIncome)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.baseIncome)
            }
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

    override fun getOtherIncomeData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.otherIncome)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.otherIncome)
            }
        }
        return retList
    }

    override fun getHealthInsuranceFeeData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.healthInsuranceFee)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.healthInsuranceFee)
            }
        }
        return retList
    }

    override fun getLongTermCareInsuranceFeeData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.longTermCareInsuranceFee)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.longTermCareInsuranceFee)
            }
        }
        return retList
    }

    override fun getPensionFeeData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.pensionFee)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.pensionFee)
            }
        }
        return retList
    }

    override fun getEmploymentInsuranceFeeData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.employmentInsuranceFee)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.employmentInsuranceFee)
            }
        }
        return retList
    }

    override fun getIncomeTaxData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.incomeTax)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.incomeTax)
            }
        }
        return retList
    }

    override fun getResidentTaxData(): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        for (data in mSalaryInfoList) {
            retList.add(data.residentTax)
        }
        return retList
    }

    override fun getOtherDeductionData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                retList.add(data.otherDeduction)
            }
        } else {
            for (data in mSalaryInfoList) {
                retList.add(data.otherDeduction)
            }
        }
        return retList
    }

    override fun getAfterTaxData(aIsBonus: Boolean): List<Int> {
        val retList: MutableList<Int> = mutableListOf()
        if (aIsBonus) {
            for (data in mBonusInfoList) {
                val income = getSumIncome(data)
                val deduction = getSumDeduction(data)
                retList.add(income - deduction)
            }
        } else {
            for (data in mSalaryInfoList) {
                val income = getSumIncome(data)
                val deduction = getSumDeduction(data)
                retList.add(income - deduction)
            }
        }
        return retList
    }

    private fun getSumIncome(aData: SalaryInfo): Int {
        return aData.baseIncome + aData.overtimeIncome + aData.otherIncome
    }

    private fun getSumIncome(aData: BonusInfo): Int {
        return aData.baseIncome + aData.otherIncome
    }

    private fun getSumDeduction(aData: SalaryInfo): Int {
        return aData.healthInsuranceFee +
                aData.longTermCareInsuranceFee +
                aData.pensionFee +
                aData.employmentInsuranceFee +
                aData.incomeTax +
                aData.residentTax +
                aData.otherDeduction
    }

    private fun getSumDeduction(aData: BonusInfo): Int {
        return aData.healthInsuranceFee +
                aData.longTermCareInsuranceFee +
                aData.pensionFee +
                aData.employmentInsuranceFee +
                aData.incomeTax +
                aData.otherDeduction
    }
}