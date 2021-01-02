package com.karaageumai.workmanagement.view.input.viewcontroller

import androidx.appcompat.app.AlertDialog
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.view.input.util.BonusInfoHelper
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.view.input.viewdata.SumViewTag

class BonusActivity : BaseInputActivity() {
    // 新規or更新を判定する値
    var mEntryMode: Int = ENTRY_MODE_ERROR
    // ボーナス情報
    private lateinit var mBonusInfo: BonusInfo
    // ボーナス情報(バックアップ)
    private lateinit var mBonusInfoBackup: BonusInfo
    // ボーナス情報編集用のヘルパー
    private lateinit var mBonusInfoHelper: BonusInfoHelper
    // 年
    private var mYear = 0
    // 月
    private var mMonth = 0

    override fun init() {
        mEntryMode = intent.getIntExtra(KEY_ENTRY_MODE, ENTRY_MODE_ERROR)

        mYear = intent.getIntExtra(KEY_YEAR, 0)
        mMonth = intent.getIntExtra(KEY_MONTH, 0)

        // 仮にデータが空 or エラーだった場合はトップメニューに遷移させる
        if ((mYear == 0) or (mMonth == 0)) {
            Log.i("can not get data. go to TopMenu.")
            // 通常はありえないルート
            // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
        }

        // データを作成
        when (mEntryMode) {
            ENTRY_MODE_NEW -> {
                // 新規データ
                Log.i("create new BonusInfo")
                mBonusInfo = BonusInfo(0, mYear, mMonth)
                mBonusInfoHelper = BonusInfoHelper(mBonusInfo, true)
            }

            ENTRY_MODE_ALREADY_EXIST -> {
                // 既存データを取得
                Log.i("get BonusInfo from DB")
                ModelFacade.selectBonusInfo(mYear, mMonth)?.let {
                    mBonusInfo = it
                    mBonusInfoHelper = BonusInfoHelper(mBonusInfo, false)
                }
            }

            else -> {
                // Todo : 失敗したらfinish()してトップに戻す。トップでダイアログだしておく。
            }
        }

        mBonusInfoBackup = mBonusInfo

        Log.i("mMode:$mEntryMode, year:$mYear, month:$mMonth")

    }


    override fun getEntryMode(): Int {
        return mEntryMode
    }

    override fun getWorkStatusInputInfoParcelList(): List<InputInfoParcel> {
        // ボーナス情報では勤怠情報を入力しないため、空のリストを返しておく
        return listOf()
    }

    override fun getIncomeInputInfoParcelList(): List<InputInfoParcel> {
        return mBonusInfoHelper.getInputInfoParcelList(
            listOf(
                InputViewTag.BaseIncomeInputViewData,
                InputViewTag.OtherIncomeInputViewData
            )
        )
    }

    override fun getDeductionInputInfoParcelList(): List<InputInfoParcel> {
        return mBonusInfoHelper.getInputInfoParcelList(
            listOf(
                InputViewTag.HealthInsuranceInputViewData,
                InputViewTag.LongTermCareInsuranceFeeInputViewData,
                InputViewTag.PensionInsuranceInputViewData,
                InputViewTag.EmploymentInsuranceInputViewData,
                InputViewTag.IncomeTaxInputViewData,
                InputViewTag.OtherDeductionInputViewData
            )
        )
    }

    override fun update(aInputInfoObservable: InputInfoObservableFragment) {
        Log.i("update()")
        val receiveParcels = aInputInfoObservable.getInputInfoParcelList()
        Log.i("before update : $mBonusInfo")
        mBonusInfoHelper.updateInputInfoParcel(receiveParcels)
        Log.i("after update : $mBonusInfo")
        updateSumView()
    }

    override fun updateSumView() {
        val map = super.getSumViewMap()
        map[SumViewTag.IncomeSumViewData]?.let {
            it.text = mBonusInfoHelper.getSumIncome().toString()
        }
        map[SumViewTag.DeductionSumViewData]?.let {
            it.text = mBonusInfoHelper.getSumDeduction().toString()
        }
    }

    override fun saveData() {
        if(mBonusInfoHelper.checkUserInputFinished()){
            mBonusInfo.isComplete = true
            when (mEntryMode) {
                ENTRY_MODE_NEW -> {
                    ModelFacade.insertBonusInfo(mBonusInfo)
                }

                ENTRY_MODE_ALREADY_EXIST -> {
                    ModelFacade.updateBonusInfo(mBonusInfo)
                }
                else -> return
            }
            finish()
        } else {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    mBonusInfo.isComplete = true
                    when (mEntryMode) {
                        ENTRY_MODE_NEW -> {
                            ModelFacade.insertBonusInfo(mBonusInfo)
                        }

                        ENTRY_MODE_ALREADY_EXIST -> {
                            ModelFacade.updateBonusInfo(mBonusInfo)
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
                        ModelFacade.deleteBonusInfo(mBonusInfoBackup)
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
        return listOf(TabPage.Income, TabPage.Deduction)
    }

    override fun getSumViewTagList(): List<SumViewTag> {
        return listOf(SumViewTag.IncomeSumViewData, SumViewTag.DeductionSumViewData)
    }

    override fun getInputDataDescription(): String {
        return getString(R.string.bonus_description, mYear, mMonth)
    }
}