package com.karaageumai.workmanagement.view.input.bonus

import android.content.Context
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.presenter.input.bonus.BonusPresenter
import com.karaageumai.workmanagement.presenter.input.bonus.IBonusPresenter
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.BaseInputActivity
import com.karaageumai.workmanagement.view.input.common.KEY_MONTH
import com.karaageumai.workmanagement.view.input.common.KEY_YEAR
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.presenter.input.viewdata.SumViewTag
import com.karaageumai.workmanagement.util.NumberFormatUtil

class BonusActivity : BaseInputActivity() {
    // ボーナス情報のPresenter
    private var mBonusInfoPresenter: IBonusPresenter? = null
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
        mBonusInfoPresenter = BonusPresenter(this)
        // 共有Presenterを登録
        MainApplication.setPresenter(mBonusInfoPresenter)

    }

    override fun getSumViewTagList(): List<SumViewTag> {
        return listOf(SumViewTag.IncomeSumViewData, SumViewTag.DeductionSumViewData)
    }

    override fun getTabPageList(): List<TabPage> {
        return listOf(TabPage.Income, TabPage.Deduction)
    }

    override fun getWorkStatusInputInfoParcelList(): List<InputInfoParcel> {
        // ボーナス情報では勤怠情報を入力しないため、空のリストを返しておく
        return listOf()
    }

    override fun getIncomeInputInfoParcelList(): List<InputInfoParcel> {
        return mBonusInfoPresenter?.getInputInfoParcelList(
            listOf(
                InputViewTag.BaseIncomeInputViewData,
                InputViewTag.OtherIncomeInputViewData
            )
        ) ?: listOf()
    }

    override fun getDeductionInputInfoParcelList(): List<InputInfoParcel> {
        return mBonusInfoPresenter?.getInputInfoParcelList(
            listOf(
                InputViewTag.HealthInsuranceInputViewData,
                InputViewTag.LongTermCareInsuranceFeeInputViewData,
                InputViewTag.PensionInsuranceInputViewData,
                InputViewTag.EmploymentInsuranceInputViewData,
                InputViewTag.IncomeTaxInputViewData,
                InputViewTag.OtherDeductionInputViewData
            )
        ) ?: listOf()
    }

    override fun updateSumView() {
        val map = super.getSumViewMap()
        map[SumViewTag.IncomeSumViewData]?.let {
            it.text = NumberFormatUtil.separateThousand(mBonusInfoPresenter?.getSumIncome().toString())
        }
        map[SumViewTag.DeductionSumViewData]?.let {
            it.text = NumberFormatUtil.separateThousand(mBonusInfoPresenter?.getSumDeduction().toString())
        }
    }

    override fun saveData() {
        mBonusInfoPresenter?.saveData()
    }

    override fun deleteData() {
        mBonusInfoPresenter?.deleteData()
    }

    override fun getInputDataDescription(): String {
        return mBonusInfoPresenter?.getDataDescription() ?: ""
    }

    override fun removePresenter() {
        mBonusInfoPresenter = null
    }

    override fun getYear(): Int {
        return mYear
    }

    override fun getMonth(): Int {
        return mMonth
    }

    override fun getActivityContext(): Context {
        return this
    }
}