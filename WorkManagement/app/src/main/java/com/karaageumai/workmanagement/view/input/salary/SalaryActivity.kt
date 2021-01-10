package com.karaageumai.workmanagement.view.input.salary

import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.presenter.input.salary.ISalaryPresenter
import com.karaageumai.workmanagement.presenter.input.salary.SalaryPresenter
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.BaseInputActivity
import com.karaageumai.workmanagement.view.input.common.KEY_MONTH
import com.karaageumai.workmanagement.view.input.common.KEY_YEAR
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.view.input.viewdata.SumViewTag

class SalaryActivity : BaseInputActivity() {
    // 給与情報のPresenter
    private lateinit var mSalaryInfoPresenter: ISalaryPresenter
    // 年
    private var mYear = 0
    // 月
    private var mMonth = 0

    override fun init() {
        mYear = intent.getIntExtra(KEY_YEAR, 0)
        mMonth = intent.getIntExtra(KEY_MONTH, 0)

        // 仮にデータが空 or エラーだった場合はfinishする
        if ((mYear == 0) or (mMonth == 0)) {
            // 通常はありえないルート
            Log.i("mYear:$mYear, mMonth:$mMonth, error has occurred.")
            showErrorToast()
            finish()
        }
        // Presenter作成
        mSalaryInfoPresenter = SalaryPresenter(this)
        // 共有Presenterを登録
        MainApplication.setPresenter(mSalaryInfoPresenter)

    }

    override fun getSumViewTagList(): List<SumViewTag> {
        return listOf(SumViewTag.WorkStatusSumViewData, SumViewTag.IncomeSumViewData, SumViewTag.DeductionSumViewData)
    }

    override fun getTabPageList(): List<TabPage> {
        return listOf(TabPage.WorkStatus, TabPage.Income, TabPage.Deduction)
    }

    override fun getWorkStatusInputInfoParcelList(): List<InputInfoParcel> {
        return mSalaryInfoPresenter.getInputInfoParcelList(
            listOf(
                InputViewTag.WorkingDayInputViewData,
                InputViewTag.WorkingTimeInputViewData,
                InputViewTag.OverTimeInputViewData
            )
        )
    }

    override fun getIncomeInputInfoParcelList(): List<InputInfoParcel> {
        return mSalaryInfoPresenter.getInputInfoParcelList(
            listOf(
                InputViewTag.BaseIncomeInputViewData,
                InputViewTag.OverTimeIncomeInputViewData,
                InputViewTag.OtherIncomeInputViewData
            )
        )
    }

    override fun getDeductionInputInfoParcelList(): List<InputInfoParcel> {
        return mSalaryInfoPresenter.getInputInfoParcelList(
            listOf(
                InputViewTag.HealthInsuranceInputViewData,
                InputViewTag.LongTermCareInsuranceFeeInputViewData,
                InputViewTag.PensionInsuranceInputViewData,
                InputViewTag.EmploymentInsuranceInputViewData,
                InputViewTag.IncomeTaxInputViewData,
                InputViewTag.ResidentTaxInputViewData,
                InputViewTag.OtherDeductionInputViewData
            )
        )
    }

    override fun updateSumView() {
        val map = super.getSumViewMap()
        map[SumViewTag.WorkStatusSumViewData]?.let {
            it.text = mSalaryInfoPresenter.getSumWorkTime().toString()
        }
        map[SumViewTag.IncomeSumViewData]?.let {
            it.text = mSalaryInfoPresenter.getSumIncome().toString()
        }
        map[SumViewTag.DeductionSumViewData]?.let {
            it.text = mSalaryInfoPresenter.getSumDeduction().toString()
        }
    }

    override fun saveData() {
        mSalaryInfoPresenter.saveData(this)
    }

    override fun deleteData() {
        mSalaryInfoPresenter.deleteData(this)
    }

    override fun getInputDataDescription(): String {
        return mSalaryInfoPresenter.getDataDescription()
    }

    override fun getYear(): Int {
        return mYear
    }

    override fun getMonth(): Int {
        return mMonth
    }
}