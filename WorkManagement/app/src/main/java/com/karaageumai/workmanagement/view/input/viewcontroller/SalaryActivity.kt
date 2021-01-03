package com.karaageumai.workmanagement.view.input.viewcontroller

import androidx.appcompat.app.AlertDialog
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.util.SalaryInfoHelper
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.view.input.viewdata.SumViewTag

class SalaryActivity : BaseInputActivity() {
    // 新規or更新を判定する値
    var mEntryMode: Int = ENTRY_MODE_ERROR
    // 給与情報
    private lateinit var mSalaryInfo: SalaryInfo
    // 給与情報（バックアップ）
    private lateinit var mSalaryInfoBackup: SalaryInfo
    // 給与情報編集用のヘルパー
    private lateinit var mSalaryInfoHelper: SalaryInfoHelper
    // 年
    private var mYear = 0
    // 月
    private var mMonth = 0

    override fun init() {
        mEntryMode = intent.getIntExtra(KEY_ENTRY_MODE, ENTRY_MODE_ERROR)

        mYear = intent.getIntExtra(KEY_YEAR, 0)
        mMonth = intent.getIntExtra(KEY_MONTH, 0)

        // 仮にデータが空 or エラーだった場合はfinishする
        if ((mYear == 0) or (mMonth == 0)) {
            // 通常はありえないルート
            Log.i("mYear:$mYear, mMonth:$mMonth, error has occurred.")
            showErrorToast()
            finish()
        }

        // データを作成
        when (mEntryMode) {
            ENTRY_MODE_NEW -> {
                // 新規データ
                Log.i("create new SalaryInfo")
                mSalaryInfo = SalaryInfo(0, mYear, mMonth)
                mSalaryInfoHelper = SalaryInfoHelper(mSalaryInfo, true)
            }

            ENTRY_MODE_ALREADY_EXIST -> {
                // 既存データを取得
                Log.i("get SalaryInfo from DB")
                ModelFacade.selectSalaryInfo(mYear, mMonth)?.let {
                    mSalaryInfo = it
                    mSalaryInfoHelper = SalaryInfoHelper(mSalaryInfo, false)
                }
            }

            else -> {
                // 通常はありえないルート
                Log.i("mMode:$mEntryMode, error has occurred.")
                showErrorToast()
                finish()
            }
        }

        // 編集前のデータをバックアップしておく
        mSalaryInfoBackup = mSalaryInfo

        Log.i("mMode:$mEntryMode, year:$mYear, month:$mMonth")

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
                    showSaveToast()
                }

                ENTRY_MODE_ALREADY_EXIST -> {
                    ModelFacade.updateSalaryInfo(mSalaryInfo)
                    showUpdateToast()
                }
                else -> return
            }
            finish()
        } else {
            AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        mSalaryInfo.isComplete = true
                        when (mEntryMode) {
                            ENTRY_MODE_NEW -> {
                                ModelFacade.insertSalaryInfo(mSalaryInfo)
                                showSaveToast()
                            }

                            ENTRY_MODE_ALREADY_EXIST -> {
                                ModelFacade.updateSalaryInfo(mSalaryInfo)
                                showUpdateToast()
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

    override fun deleteData() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_delete)
                .setMessage(getString(R.string.dialog_message_delete, getInputDataDescription()))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    if (mEntryMode == ENTRY_MODE_ALREADY_EXIST) {
                        ModelFacade.deleteSalaryInfo(mSalaryInfoBackup)
                        showDeleteToast()
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

    override fun getTabPageList(): List<TabPage> {
        return listOf(TabPage.WorkStatus, TabPage.Income, TabPage.Deduction)
    }

    override fun getSumViewTagList(): List<SumViewTag> {
        return listOf(SumViewTag.WorkStatusSumViewData, SumViewTag.IncomeSumViewData, SumViewTag.DeductionSumViewData)
    }

    override fun getInputDataDescription(): String {
        return getString(R.string.salary_description, mYear, mMonth)
    }
}