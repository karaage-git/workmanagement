package com.karaageumai.workmanagement.presenter.analyze.annual

import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.analyze.annual.IAnnualAnalyze
import com.karaageumai.workmanagement.presenter.analyze.annual.util.AnnualDataRow

class AnnualAnalyzePresenter(var mActivity: IAnnualAnalyze) : IAnnualAnalyzePresenter {
    private val mAnnualDataMap: MutableMap<Int, AnnualData> = mutableMapOf()

    init {
        // 初期表示用のデータを取得
        mAnnualDataMap[mActivity.getYear()] = getAnnualData(mActivity.getYear())
    }

    /**
     * 年間データの取得
     * データがロードされていなければ、DBから取得する
     */
    private fun getAnnualData(aYear: Int): AnnualData {
        return mAnnualDataMap[aYear] ?: AnnualData(ModelFacade.selectSalaryInfo(aYear), ModelFacade.selectBonusInfo(aYear))
    }

    data class AnnualData(
            val salaryInfoList: List<SalaryInfo>,
            val bonusInfoList: List<BonusInfo>
    )

    override fun loadData(aYear: Int) {
        val dataRowList: MutableList<AnnualDataRow> = mutableListOf()
        dataRowList.add(getSumWorkingDayDataRow(aYear))
        dataRowList.add(getSumWorkingTimeDataRow(aYear))
        dataRowList.add(getSumIncomeDataRow(aYear))
        dataRowList.add(getSumBaseIncome(aYear))
        mActivity.onLoadedData(dataRowList)
    }

    /**
     * 勤務日数の合計を算出する
     */
    private fun getSumWorkingDayDataRow(aYear: Int): AnnualDataRow {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0.0
        for(salary in salaryInfoList) {
            value += salary.workingDay
        }
        return AnnualDataRow(R.string.annual_analyze_row_working_day, value.toString(), R.string.annual_analyze_row_unit_day, false)
    }

    /**
     * 勤務時間の合計を算出する
     */
    private fun getSumWorkingTimeDataRow(aYear: Int): AnnualDataRow {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0.0
        for(salary in salaryInfoList) {
            // 通常の労働時間を加算
            value += salary.workingTime
            // 残業時間を加算
            value += salary.overtime
        }
        return AnnualDataRow(R.string.annual_analyze_row_working_time, value.toString(), R.string.annual_analyze_row_unit_time, false)
    }

    /**
     * 収入の合計を算出する
     */
    private fun getSumIncomeDataRow(aYear: Int): AnnualDataRow {
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
        return AnnualDataRow(R.string.annual_analyze_row_income, value.toString(), R.string.annual_analyze_row_unit_money, false)
    }

    /**
     * 基本給の合計を算出する
     */
    private fun getSumBaseIncome(aYear: Int): AnnualDataRow {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0
        for(salaryInfo in salaryInfoList){
            // 基本給
            value += salaryInfo.baseIncome
        }
        return AnnualDataRow(R.string.annual_analyze_row_base_income, value.toString(), R.string.annual_analyze_row_unit_money, true)
    }
}