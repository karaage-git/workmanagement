package com.karaageumai.workmanagement.presenter.salary

import android.app.AlertDialog
import android.content.Context
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.IBaseInputView
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag

class SalaryPresenter(var mActivity: IBaseInputView) : ISalaryPresenter {
    // 給与情報
    private val mSalaryInfo: SalaryInfo
    // 新規or更新を判定するフラグ
    private val mIsNewEntry: Boolean
    // 給与情報として入力が必要なInputInfoParcelのリスト
    private val mInputInfoParcelList: List<InputInfoParcel>
    // 削除時に使用する変更前データ
    private val mBackup: SalaryInfo

    init {
        mSalaryInfo = ModelFacade.selectSalaryInfo(mActivity.getYear(), mActivity.getMonth()).let{
            if(it != null) {
                mIsNewEntry = false
                it
            } else {
                mIsNewEntry = true
                SalaryInfo(0, mActivity.getYear(), mActivity.getMonth())
            }
        }

        mInputInfoParcelList = listOf(
                InputInfoParcel(InputViewTag.WorkingDayInputViewData, mSalaryInfo.workingDay.toString()),
                InputInfoParcel(InputViewTag.WorkingTimeInputViewData, mSalaryInfo.workingTime.toString()),
                InputInfoParcel(InputViewTag.OverTimeInputViewData, mSalaryInfo.overtime.toString()),
                InputInfoParcel(InputViewTag.BaseIncomeInputViewData, mSalaryInfo.baseIncome.toString()),
                InputInfoParcel(InputViewTag.OverTimeIncomeInputViewData, mSalaryInfo.overtimeIncome.toString()),
                InputInfoParcel(InputViewTag.OtherIncomeInputViewData, mSalaryInfo.otherIncome.toString()),
                InputInfoParcel(InputViewTag.HealthInsuranceInputViewData, mSalaryInfo.healthInsuranceFee.toString()),
                InputInfoParcel(InputViewTag.LongTermCareInsuranceFeeInputViewData, mSalaryInfo.longTermCareInsuranceFee.toString()),
                InputInfoParcel(InputViewTag.PensionInsuranceInputViewData, mSalaryInfo.pensionFee.toString()),
                InputInfoParcel(InputViewTag.EmploymentInsuranceInputViewData, mSalaryInfo.employmentInsuranceFee.toString()),
                InputInfoParcel(InputViewTag.IncomeTaxInputViewData, mSalaryInfo.incomeTax.toString()),
                InputInfoParcel(InputViewTag.ResidentTaxInputViewData, mSalaryInfo.residentTax.toString()),
                InputInfoParcel(InputViewTag.OtherDeductionInputViewData, mSalaryInfo.otherDeduction.toString())
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

        mBackup = mSalaryInfo

    }

    override fun getSumWorkTime(): Double {
        return mSalaryInfo.workingTime + mSalaryInfo.overtime
    }

    override fun getSumIncome(): Int {
        return mSalaryInfo.baseIncome + mSalaryInfo.overtimeIncome + mSalaryInfo.otherIncome
    }

    override fun getSumDeduction(): Int {
        return mSalaryInfo.healthInsuranceFee +
                mSalaryInfo.longTermCareInsuranceFee +
                mSalaryInfo.pensionFee +
                mSalaryInfo.employmentInsuranceFee +
                mSalaryInfo.incomeTax +
                mSalaryInfo.residentTax +
                mSalaryInfo.otherDeduction
    }

    override fun getInputInfoParcelList(): List<InputInfoParcel> {
        return mInputInfoParcelList
    }

    override fun updateItem(aParcel: InputInfoParcel) {
        var isSuccess = true
        when(aParcel.mTag){
            InputViewTag.WorkingDayInputViewData -> {
                mSalaryInfo.workingDay = try {
                    aParcel.mStrValue.toDouble()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0.0
                }
            }

            InputViewTag.WorkingTimeInputViewData -> {
                mSalaryInfo.workingTime = try {
                    aParcel.mStrValue.toDouble()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0.0
                }
            }

            InputViewTag.OverTimeInputViewData -> {
                mSalaryInfo.overtime = try {
                    aParcel.mStrValue.toDouble()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0.0
                }
            }

            InputViewTag.BaseIncomeInputViewData -> {
                mSalaryInfo.baseIncome = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.OverTimeIncomeInputViewData -> {
                mSalaryInfo.overtimeIncome = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.OtherIncomeInputViewData -> {
                mSalaryInfo.otherIncome = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.HealthInsuranceInputViewData -> {
                mSalaryInfo.healthInsuranceFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                mSalaryInfo.longTermCareInsuranceFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.PensionInsuranceInputViewData -> {
                mSalaryInfo.pensionFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.EmploymentInsuranceInputViewData -> {
                mSalaryInfo.employmentInsuranceFee = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.IncomeTaxInputViewData -> {
                mSalaryInfo.incomeTax = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.ResidentTaxInputViewData -> {
                mSalaryInfo.residentTax = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.OtherDeductionInputViewData -> {
                mSalaryInfo.otherDeduction = try {
                    aParcel.mStrValue.toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }
        }
        mActivity.onInputItem(isSuccess)
    }

    override fun saveData(aContext: Context) {
        if(checkUserInputFinished()){
            saveData()
        } else {
            AlertDialog.Builder(aContext)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        saveData()
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
                        ModelFacade.deleteSalaryInfo(mBackup)
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
        return MainApplication.getContext().getString(R.string.salary_description, mActivity.getYear(), mActivity.getMonth())
    }

    private fun saveData() {
        mSalaryInfo.isComplete = true
        if(mIsNewEntry) {
            ModelFacade.insertSalaryInfo(mSalaryInfo)
            mActivity.onInsertData()
        } else {
            ModelFacade.updateSalaryInfo(mSalaryInfo)
            mActivity.onUpdateData()
        }
    }
}