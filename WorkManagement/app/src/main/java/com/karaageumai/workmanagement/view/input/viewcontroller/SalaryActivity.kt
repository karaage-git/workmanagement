package com.karaageumai.workmanagement.view.input.viewcontroller

import androidx.appcompat.app.AlertDialog
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.CalendarUtil
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.util.SalaryInfoHelper
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.view.input.viewdata.SumViewTag

class SalaryActivity : BaseInputActivity() {
    var mEntryMode: Int = ENTRY_MODE_ERROR
    lateinit var mSalaryInfo: SalaryInfo
    private lateinit var mSalaryInfoHelper: SalaryInfoHelper

    override fun init() {
        mEntryMode = intent.getIntExtra(KEY_ENTRY_MODE, ENTRY_MODE_ERROR)

        val year = intent.getIntExtra(KEY_YEAR, 0)
        val month = intent.getIntExtra(KEY_MONTH, 0)

        // 仮にデータが空 or エラーだった場合はトップメニューに遷移させる
        if ((year == 0) or (month == 0)) {
            Log.i("can not get data. go to TopMenu.")
            // 通常はありえないルート
            // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
        }

        // データを作成
        when (mEntryMode) {
            ENTRY_MODE_NEW -> {
                // 新規データ
                Log.i("create new SalaryInfo")
                mSalaryInfo = SalaryInfo(0, year, month)
                mSalaryInfoHelper = SalaryInfoHelper(mSalaryInfo, true)
            }

            ENTRY_MODE_ALREADY_EXIST -> {
                // 既存データを取得
                Log.i("get SalaryInfo from DB")
                ModelFacade.selectSalaryInfo(year, month)?.let {
                    mSalaryInfo = it
                    mSalaryInfoHelper = SalaryInfoHelper(mSalaryInfo, false)
                }
            }

            else -> {
                // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
            }
        }

        Log.i("mMode:$mEntryMode, year:$year, month:$month")

    }


    override fun getEntryMode(): Int {
        return mEntryMode
    }

    override fun getWorkStatusInputInfoParcelList(): List<InputInfoParcel> {
        return mSalaryInfoHelper.getInputInfoParcelList(
            listOf(
                InputViewTag.WorkingDayInputViewData,
                InputViewTag.WorkingTimeInputViewData,
                InputViewTag.OverTimeInputViewData
            )
        )
    }

    override fun getIncomeInputInfoParcelList(): List<InputInfoParcel> {
        return mSalaryInfoHelper.getInputInfoParcelList(
            listOf(
                InputViewTag.BaseIncomeInputViewData,
                InputViewTag.OverTimeIncomeInputViewData,
                InputViewTag.OtherIncomeInputViewData
            )
        )
    }

    override fun getDeductionInputInfoParcelList(): List<InputInfoParcel> {
        return mSalaryInfoHelper.getInputInfoParcelList(
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

    override fun update(aInputInfoObservable: InputInfoObservableFragment) {
        Log.i("update()")
        val receiveParcels = aInputInfoObservable.getInputInfoParcelList()
        Log.i("before update : $mSalaryInfo")
        mSalaryInfoHelper.updateSalaryInfo(receiveParcels)
        Log.i("after update : $mSalaryInfo")
        updateSumView()
    }

    override fun updateSumView() {
        val map = super.getSumViewMap()
        map[SumViewTag.WorkStatusSumViewData]?.let {
            it.text = mSalaryInfoHelper.getSumWorkTime().toString()
        }
        map[SumViewTag.IncomeSumViewData]?.let {
            it.text = mSalaryInfoHelper.getSumIncome().toString()
        }
        map[SumViewTag.DeductionSumViewData]?.let {
            it.text = mSalaryInfoHelper.getSumDeduction().toString()
        }
    }

    override fun saveData() {
        if(mSalaryInfoHelper.checkUserInputFinished()){
            mSalaryInfo.isComplete = true
            when (mEntryMode) {
                ENTRY_MODE_NEW -> {
                    ModelFacade.insertSalaryInfo(mSalaryInfo)
                }

                ENTRY_MODE_ALREADY_EXIST -> {
                    ModelFacade.updateSalaryInfo(mSalaryInfo)
                }
                else -> return
            }
            finish()
        } else {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    mSalaryInfo.isComplete = true
                    when (mEntryMode) {
                        ENTRY_MODE_NEW -> {
                            ModelFacade.insertSalaryInfo(mSalaryInfo)
                        }

                        ENTRY_MODE_ALREADY_EXIST -> {
                            ModelFacade.updateSalaryInfo(mSalaryInfo)
                        }

                        else -> {}
                    }
                    dialog.dismiss()
                    finish()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }
    }

    override fun getTabPageList(): List<TabPage> {
        return listOf(TabPage.WorkStatus, TabPage.Income, TabPage.Deduction)
    }

    override fun getSumViewTagList(): List<SumViewTag> {
        return listOf(SumViewTag.WorkStatusSumViewData, SumViewTag.IncomeSumViewData, SumViewTag.DeductionSumViewData)
    }
}