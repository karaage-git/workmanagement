package com.karaageumai.workmanagement.presenter.analyze.annual

import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.analyze.annual.top.IAnnualAnalyze
import com.karaageumai.workmanagement.presenter.analyze.annual.util.AnnualDataRow
import com.karaageumai.workmanagement.util.Constants.WORK_YEAR_END_MONTH
import com.karaageumai.workmanagement.util.Constants.WORK_YEAR_START_MONTH
import java.lang.ref.WeakReference

class AnnualAnalyzePresenter(aActivity: IAnnualAnalyze) : IAnnualAnalyzePresenter {
    // Activity
    private val mActivity: WeakReference<IAnnualAnalyze> = WeakReference(aActivity)
    // 年モード用のデータ管理マップ
    private val mAnnualDataMapForYear: MutableMap<Int, AnnualData> = mutableMapOf()
    // 年度モード用のデータ管理マップ
    private val mAnnualDataMapForWorkYear:  MutableMap<Int, AnnualData> = mutableMapOf()
    // 年or年度を示すフラグ
    private var mIsWorkYearMode = mActivity.get()?.getIsWorkYearMode() ?: false

    init {
        // 初期表示用のデータを取得
        val activity = mActivity.get()
        if (activity != null) {
            if(mIsWorkYearMode) {
                mAnnualDataMapForWorkYear[activity.getYear()] = getAnnualData(activity.getYear())
            } else {
                mAnnualDataMapForYear[activity.getYear()] = getAnnualData(activity.getYear())
            }
        }
    }

    /**
     * 年間データの取得
     * データがロードされていなければ、DBから取得する
     */
    private fun getAnnualData(aYear: Int): AnnualData {
        Log.i("mIsWorkYearMode : $mIsWorkYearMode")
        if(mIsWorkYearMode) {
            return mAnnualDataMapForWorkYear[aYear].let {
                if(it != null){
                    it
                } else {
                    val newData = AnnualData(
                            ModelFacade.selectSalaryInfo(
                                    aYear,
                                    WORK_YEAR_START_MONTH,
                                    aYear + 1,
                                    WORK_YEAR_END_MONTH
                            ),
                            ModelFacade.selectBonusInfo(
                                    aYear,
                                    WORK_YEAR_START_MONTH,
                                    aYear + 1,
                                    WORK_YEAR_END_MONTH
                            )
                    )
                    mAnnualDataMapForWorkYear[aYear] = newData
                    newData
                }
            }
        } else {
            return mAnnualDataMapForYear[aYear].let {
                if(it != null){
                    it
                } else {
                    val newData = AnnualData(
                            ModelFacade.selectSalaryInfo(aYear),
                            ModelFacade.selectBonusInfo(aYear)
                    )
                    mAnnualDataMapForYear[aYear] = newData
                    newData
                }
            }
        }
    }

    data class AnnualData(
            val salaryInfoList: List<SalaryInfo>,
            val bonusInfoList: List<BonusInfo>
    )

    override fun loadData(aYear: Int) {
        val dataRowList: MutableList<AnnualDataRow> = mutableListOf()
        dataRowList.add(createSumWorkingDayDataRow(aYear))
        dataRowList.add(createSumWorkingTimeDataRow(aYear))
        dataRowList.add(createSumBaseWorkingTimeDataRow(aYear))
        dataRowList.add(createSumOverWorkingTimeDataRow(aYear))
        dataRowList.add(createSumIncomeDataRow(aYear))
        dataRowList.add(createSumBaseIncomeDataRow(aYear))
        dataRowList.add(createSumOverTimeIncomeDataRow(aYear))
        dataRowList.add(createSumOtherIncomeDataRow(aYear))
        dataRowList.add(createSumBonusDataRow(aYear))
        dataRowList.add(createSumBonusOtherDataRow(aYear))
        dataRowList.add(createSumDeductionDataRow(aYear))
        dataRowList.add(createSumHealthInsuranceFeeDataRow(aYear))
        dataRowList.add(createSumLongTermCareInsuranceFeeDataRow(aYear))
        dataRowList.add(createSumPensionInsuranceFeeDataRow(aYear))
        dataRowList.add(createSumEmploymentInsuranceFeeDataRow(aYear))
        dataRowList.add(createSumIncomeTaxDataRow(aYear))
        dataRowList.add(createSumResidentTaxDataRow(aYear))
        dataRowList.add(createSumOtherDeductionDataRow(aYear))
        dataRowList.add(createSumAfterTaxIncomeDataRow(aYear))
        mActivity.get()?.onLoadedData(dataRowList)
    }

    override fun changeMode(aIsWorkYearMode: Boolean) {
        mIsWorkYearMode = aIsWorkYearMode
    }

    /**
     * 勤務日数の合計を表示するためのデータを作成する
     */
    private fun createSumWorkingDayDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_working_day,
                (getSumWorkingDay(aYear) / 10.0).toString(),
                R.string.annual_analyze_row_unit_day,
                false)
    }

    /**
     * 勤務時間の合計を表示するためのデータを作成する
     */
    private fun createSumWorkingTimeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_working_time,
                (getSumWorkingTime(aYear) / 10.0).toString(),
                R.string.annual_analyze_row_unit_time,
                false)
    }

    /**
     * 所定労働時間の合計を表示するためのデータを作成する
     */
    private fun createSumBaseWorkingTimeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_base_working_time,
                (getSumBaseWorkingTime(aYear) / 10.0).toString(), R.string.annual_analyze_row_unit_time, true)
    }

    /**
     * 残業時間の合計を表示するためのデータを作成する
     */
    private fun createSumOverWorkingTimeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_over_working_time,
                (getSumOverWorkingTime(aYear) / 10.0).toString(), R.string.annual_analyze_row_unit_time,
                true)
    }

    /**
     * 収入の合計を表示するためのデータを作成する
     */
    private fun createSumIncomeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_income,
                getSumIncome(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                false)
    }

    /**
     * 基本給の合計を表示するためのデータを作成する
     */
    private fun createSumBaseIncomeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_base_income,
                getSumBaseIncome(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 残業手当の合計を表示するためのデータを作成する
     */
    private fun createSumOverTimeIncomeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_overtime_income,
                getSumOverTimeIncome(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * その他収入の合計を表示するためのデータを作成する
     */
    private fun createSumOtherIncomeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_other_income,
                getSumOtherIncome(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * ボーナスの合計を表示するためのデータを作成する
     */
    private fun createSumBonusDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_bonus,
                getSumBonus(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * ボーナスのその他合計を表示するためのデータを作成する
     */
    private fun createSumBonusOtherDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_bonus_other,
                getSumBonusOther(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 控除の合計を表示するためのデータを作成する
     */
    private fun createSumDeductionDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction,
                getSumDeduction(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                false)
    }

    /**
     * 健康保険の合計を表示するためのデータを作成する
     */
    private fun createSumHealthInsuranceFeeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_health,
                getSumHealthInsuranceFee(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 介護保険の合計を表示するためのデータを作成する
     */
    private fun createSumLongTermCareInsuranceFeeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_long_term,
                getSumLongTermCareInsuranceFee(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 年金保険の合計を表示するためのデータを作成する
     */
    private fun createSumPensionInsuranceFeeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_pension,
                getSumPensionFee(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 雇用保険の合計を表示するためのデータを作成する
     */
    private fun createSumEmploymentInsuranceFeeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_employment,
                getSumEmploymentInsuranceFee(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 所得税の合計を表示するためのデータを作成する
     */
    private fun createSumIncomeTaxDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_income,
                getSumIncomeTax(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 住民税の合計を表示するためのデータを作成する
     */
    private fun createSumResidentTaxDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_resident,
                getSumResidentTax(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * その他控除の合計を表示するためのデータを作成する
     */
    private fun createSumOtherDeductionDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_deduction_other,
                getSumOtherDeduction(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                true)
    }

    /**
     * 手取りの合計を表示するためのデータを作成する
     */
    private fun createSumAfterTaxIncomeDataRow(aYear: Int): AnnualDataRow {
        return AnnualDataRow(R.string.annual_analyze_row_after_tax_income,
                getSumAfterTaxIncome(aYear).toString(),
                R.string.annual_analyze_row_unit_money,
                false)
    }



    /**
     * 勤務日数の合計を算出する
     */
    private fun getSumWorkingDay(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salary in salaryInfoList) {
            value += salary.workingDay
        }
        return value
    }

    /**
     * 勤務時間の合計を算出する
     */
    private fun getSumWorkingTime(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salary in salaryInfoList) {
            // 通常の労働時間を加算
            value += salary.workingTime
            // 残業時間を加算
            value += salary.overtime
        }
        return value
    }

    /**
     * 所定労働時間の合計を算出する
     */
    private fun getSumBaseWorkingTime(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salary in salaryInfoList) {
            // 通常の労働時間を加算
            value += salary.workingTime
        }
        return value
    }

    /**
     * 残業時間の合計を算出する
     */
    private fun getSumOverWorkingTime(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salary in salaryInfoList) {
            // 通常の残業時間を加算
            value += salary.overtime
        }
        return value
    }

    /**
     * 収入の合計を算出する
     */
    private fun getSumIncome(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 基本給
            value += salaryInfo.baseIncome
            // 残業代
            value += salaryInfo.overtimeIncome
            // その他
            value += salaryInfo.otherIncome
        }
        for(bonusInfo in bonusInfoList) {
            // 基本支給
            value += bonusInfo.baseIncome
            // その他
            value += bonusInfo.otherIncome
        }
        return value
    }

    /**
     * 基本給の合計を算出する
     */
    private fun getSumBaseIncome(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 基本給
            value += salaryInfo.baseIncome
        }
        return value
    }

    /**
     * 残業代の合計を算出する
     */
    private fun getSumOverTimeIncome(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 残業代
            value += salaryInfo.overtimeIncome
        }
        return value
    }

    /**
     * その他収入の合計を算出する
     */
    private fun getSumOtherIncome(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // その他
            value += salaryInfo.otherIncome
        }
        return value
    }

    /**
     * ボーナスの合計を算出する
     */
    private fun getSumBonus(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(bonusInfo in bonusInfoList) {
            // 基本支給
            value += bonusInfo.baseIncome
        }
        return value
    }

    private fun getSumBonusOther(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(bonusInfo in bonusInfoList) {
            // その他
            value += bonusInfo.otherIncome
        }
        return value
    }

    /**
     * 控除額の合計を算出する
     */
    private fun getSumDeduction(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 健康保険
            value += salaryInfo.healthInsuranceFee
            // 介護保険
            value += salaryInfo.longTermCareInsuranceFee
            // 年金
            value += salaryInfo.pensionFee
            // 雇用保険
            value += salaryInfo.employmentInsuranceFee
            // 所得税
            value += salaryInfo.incomeTax
            // 住民税
            value += salaryInfo.residentTax
            // その他控除
            value += salaryInfo.otherDeduction
        }
        for(bonusInfo in bonusInfoList) {
            // 健康保険
            value += bonusInfo.healthInsuranceFee
            // 介護保険
            value += bonusInfo.longTermCareInsuranceFee
            // 年金
            value += bonusInfo.pensionFee
            // 雇用保険
            value += bonusInfo.employmentInsuranceFee
            // 所得税
            value += bonusInfo.incomeTax
            // その他控除
            value += bonusInfo.otherDeduction
        }
        return value
    }

    /**
     * 健康保険の合計を算出する
     */
    private fun getSumHealthInsuranceFee(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 健康保険
            value += salaryInfo.healthInsuranceFee
        }
        for(bonusInfo in bonusInfoList) {
            // 健康保険
            value += bonusInfo.healthInsuranceFee
        }
        return value
    }

    /**
     * 介護保険の合計を算出する
     */
    private fun getSumLongTermCareInsuranceFee(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 介護保険
            value += salaryInfo.longTermCareInsuranceFee
        }
        for(bonusInfo in bonusInfoList) {
            // 介護保険
            value += bonusInfo.longTermCareInsuranceFee
        }
        return value
    }

    /**
     * 年金保険の合計を算出する
     */
    private fun getSumPensionFee(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 年金
            value += salaryInfo.pensionFee
        }
        for(bonusInfo in bonusInfoList) {
            // 年金
            value += bonusInfo.pensionFee
        }
        return value
    }

    /**
     * 雇用保険の合計を算出する
     */
    private fun getSumEmploymentInsuranceFee(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 雇用保険
            value += salaryInfo.employmentInsuranceFee
        }
        for(bonusInfo in bonusInfoList) {
            // 雇用保険
            value += bonusInfo.employmentInsuranceFee
        }
        return value
    }

    /**
     * 所得税の合計を算出する
     */
    private fun getSumIncomeTax(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 所得税
            value += salaryInfo.incomeTax
        }
        for(bonusInfo in bonusInfoList) {
            // 所得税
            value += bonusInfo.incomeTax
        }
        return value
    }

    /**
     * 住民税の合計を算出する
     */
    private fun getSumResidentTax(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 住民税
            value += salaryInfo.residentTax
        }
        return value
    }

    /**
     * その他の控除の合計を算出する
     */
    private fun getSumOtherDeduction(aYear: Int): Int {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        val bonusInfoList = data.bonusInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // その他
            value += salaryInfo.otherDeduction
        }
        for(bonusInfo in bonusInfoList) {
            // その他
            value += bonusInfo.otherDeduction
        }
        return value
    }

    /**
     * 手取り額を取得する
     */
    private fun getSumAfterTaxIncome(aYear: Int): Int {
        return getSumIncome(aYear) - getSumDeduction(aYear)
    }

}