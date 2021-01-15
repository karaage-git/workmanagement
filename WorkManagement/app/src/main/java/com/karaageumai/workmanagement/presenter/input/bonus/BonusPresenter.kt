package com.karaageumai.workmanagement.presenter.input.bonus

import android.app.AlertDialog
import android.content.Context
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.IBaseInputView
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag

class BonusPresenter(var mActivity: IBaseInputView) : IBonusPresenter {
    // ボーナス情報
    private val mBonusInfo: BonusInfo
    // 新規or更新を判定するフラグ
    private val mIsNewEntry: Boolean
    // ボーナス情報として入力が必要なInputInfoParcelのリスト
    private val mInputInfoParcelList: List<InputInfoParcel>
    // 削除時に使用する変更前データ
    private val mBackup: BonusInfo

    init {
        mBonusInfo = ModelFacade.selectBonusInfo(mActivity.getYear(), mActivity.getMonth()).let{
            if(it != null) {
                mIsNewEntry = false
                it
            } else {
                mIsNewEntry = true
                BonusInfo(0, mActivity.getYear(), mActivity.getMonth())
            }
        }

        mInputInfoParcelList = listOf(
                InputInfoParcel(InputViewTag.BaseIncomeInputViewData, mBonusInfo.baseIncome.toString()),
                InputInfoParcel(InputViewTag.OtherIncomeInputViewData, mBonusInfo.otherIncome.toString()),
                InputInfoParcel(InputViewTag.HealthInsuranceInputViewData, mBonusInfo.healthInsuranceFee.toString()),
                InputInfoParcel(InputViewTag.LongTermCareInsuranceFeeInputViewData, mBonusInfo.longTermCareInsuranceFee.toString()),
                InputInfoParcel(InputViewTag.PensionInsuranceInputViewData, mBonusInfo.pensionFee.toString()),
                InputInfoParcel(InputViewTag.EmploymentInsuranceInputViewData, mBonusInfo.employmentInsuranceFee.toString()),
                InputInfoParcel(InputViewTag.IncomeTaxInputViewData, mBonusInfo.incomeTax.toString()),
                InputInfoParcel(InputViewTag.OtherDeductionInputViewData, mBonusInfo.otherDeduction.toString())
        )

        // 更新時にはすべて入力済みとして扱う対応
        if(mIsNewEntry){
            for (parcel in mInputInfoParcelList) {
                parcel.mIsComplete = false
            }
        } else {
            for (parcel in mInputInfoParcelList) {
                parcel.mIsComplete = true
            }
        }

        mBackup = mBonusInfo

    }

    override fun getSumIncome(): Int {
        return mBonusInfo.baseIncome + mBonusInfo.otherIncome
    }

    override fun getSumDeduction(): Int {
        return mBonusInfo.healthInsuranceFee +
                mBonusInfo.longTermCareInsuranceFee +
                mBonusInfo.pensionFee +
                mBonusInfo.employmentInsuranceFee +
                mBonusInfo.incomeTax +
                mBonusInfo.otherDeduction
    }

    override fun getInputInfoParcelList(): List<InputInfoParcel> {
        return mInputInfoParcelList
    }

    override fun updateItem(aParcel: InputInfoParcel) {
        var isSuccess = true
        when(aParcel.mTag){
            InputViewTag.BaseIncomeInputViewData -> {
                mBonusInfo.baseIncome = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.OtherIncomeInputViewData -> {
                mBonusInfo.otherIncome = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.HealthInsuranceInputViewData -> {
                mBonusInfo.healthInsuranceFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                mBonusInfo.longTermCareInsuranceFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.PensionInsuranceInputViewData -> {
                mBonusInfo.pensionFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.EmploymentInsuranceInputViewData -> {
                mBonusInfo.employmentInsuranceFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.IncomeTaxInputViewData -> {
                mBonusInfo.incomeTax = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.OtherDeductionInputViewData -> {
                mBonusInfo.otherDeduction = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            // 給与情報向けのタグが来た場合は何もしない
            else -> {isSuccess = false}
        }
        mActivity.onInputItem(isSuccess)
    }

    override fun saveData(aContext: Context) {
        if(checkUserInputFinished()) {
            insertOrUpdateData(aContext)
        } else {
            AlertDialog.Builder(aContext)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        insertOrUpdateData(aContext)
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun deleteData(aContext: Context) {
        AlertDialog.Builder(aContext)
                .setTitle(R.string.dialog_title_delete)
                .setMessage(aContext.getString(R.string.dialog_message_delete, getDataDescription()))
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    if (!mIsNewEntry) {
                        ModelFacade.deleteBonusInfo(mBackup)
                    }
                    dialog.dismiss()
                    mActivity.onDeleteData()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    override fun getDataDescription(): String {
        return MainApplication.getContext().getString(R.string.bonus_description, mActivity.getYear(), mActivity.getMonth())
    }

    private fun insertOrUpdateData(aContext: Context) {
        AlertDialog.Builder(aContext)
            .setTitle(R.string.dialog_title)
            .setMessage(aContext.getString(R.string.dialog_message_after_tax, (getSumIncome() - getSumDeduction()).toString()))
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                mBonusInfo.isComplete = true
                if(mIsNewEntry) {
                    ModelFacade.insertBonusInfo(mBonusInfo)
                    mActivity.onInsertData()
                } else {
                    ModelFacade.updateBonusInfo(mBonusInfo)
                    mActivity.onUpdateData()
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}