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
        mActivity.onLoadedData(dataRowList)
    }

    private fun getSumWorkingDayDataRow(aYear: Int): AnnualDataRow {
        val data = getAnnualData(aYear)
        val salaryInfoList = data.salaryInfoList
        var value = 0.0
        for(salary in salaryInfoList) {
            value += salary.workingDay
        }
        return AnnualDataRow(R.string.annual_analyze_row_working_day, value.toString())
    }
}