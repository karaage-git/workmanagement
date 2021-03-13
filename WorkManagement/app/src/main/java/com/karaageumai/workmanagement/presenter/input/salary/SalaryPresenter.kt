package com.karaageumai.workmanagement.presenter.input.salary

import android.app.AlertDialog
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.presenter.input.IBaseInputPresenter
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.IBaseInputView
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.util.CalendarUtil
import com.karaageumai.workmanagement.util.Constants
import java.lang.ref.WeakReference

class SalaryPresenter(aActivity: IBaseInputView) : ISalaryPresenter {
    // Activity
    private val mActivity: WeakReference<IBaseInputView> = WeakReference(aActivity)
    // 給与情報
    private val mSalaryInfo: SalaryInfo
    // 新規or更新を判定するフラグ
    private val mIsNewEntry: Boolean
    // 給与情報として入力が必要なInputInfoParcelのリスト
    private val mInputInfoParcelList: List<InputInfoParcel>
    // 削除時に使用する変更前データ
    private val mBackup: SalaryInfo

    companion object {
        // エラー時に使用するダミー情報
        private val dummyInfo = SalaryInfo(0,0,0)
    }

    init {
        val activity = mActivity.get()
        if (activity != null) {
            mSalaryInfo =
                ModelFacade.selectSalaryInfo(activity.getYear(), activity.getMonth()).let {
                    if (it != null) {
                        mIsNewEntry = false
                        it
                    } else {
                        mIsNewEntry = true
                        SalaryInfo(0, activity.getYear(), activity.getMonth())
                    }
                }

            mInputInfoParcelList = listOf(
                InputInfoParcel(
                    InputViewTag.WorkingDayInputViewData,
                    (mSalaryInfo.workingDay / 10.0).toString()
                ),
                InputInfoParcel(
                    InputViewTag.PaidHolidaysViewData,
                    (mSalaryInfo.paidHolidays / 10.0).toString()
                ),
                InputInfoParcel(
                    InputViewTag.WorkingTimeInputViewData,
                    (mSalaryInfo.workingTime / 10.0).toString()
                ),
                InputInfoParcel(
                    InputViewTag.OverTimeInputViewData,
                    (mSalaryInfo.overtime / 10.0).toString()
                ),
                InputInfoParcel(
                    InputViewTag.BaseIncomeInputViewData,
                    mSalaryInfo.baseIncome.toString()
                ),
                InputInfoParcel(
                    InputViewTag.OverTimeIncomeInputViewData,
                    mSalaryInfo.overtimeIncome.toString()
                ),
                InputInfoParcel(
                    InputViewTag.OtherIncomeInputViewData,
                    mSalaryInfo.otherIncome.toString()
                ),
                InputInfoParcel(
                    InputViewTag.HealthInsuranceInputViewData,
                    mSalaryInfo.healthInsuranceFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.LongTermCareInsuranceFeeInputViewData,
                    mSalaryInfo.longTermCareInsuranceFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.PensionInsuranceInputViewData,
                    mSalaryInfo.pensionFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.EmploymentInsuranceInputViewData,
                    mSalaryInfo.employmentInsuranceFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.IncomeTaxInputViewData,
                    mSalaryInfo.incomeTax.toString()
                ),
                InputInfoParcel(
                    InputViewTag.ResidentTaxInputViewData,
                    mSalaryInfo.residentTax.toString()
                ),
                InputInfoParcel(
                    InputViewTag.OtherDeductionInputViewData,
                    mSalaryInfo.otherDeduction.toString()
                )
            )

            // 更新時にはすべて入力済みとして扱う対応
            if (mIsNewEntry) {
                for (parcel in mInputInfoParcelList) {
                    parcel.mIsComplete = false
                }
            } else {
                for (parcel in mInputInfoParcelList) {
                    parcel.mIsComplete = true
                }
            }

            // バックアップデータ
            mBackup = mSalaryInfo

        } else {
            // Activityが取得できない場合はエラーとみなし、ダミーデータをセットする
            mSalaryInfo = dummyInfo
            mIsNewEntry = false
            mInputInfoParcelList = listOf()
            mBackup = dummyInfo
        }
    }

    override fun getSumWorkTime(): Double {
        return (mSalaryInfo.workingTime + mSalaryInfo.overtime) / 10.0
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
        // Activityがnullの場合は何もしない
        if(mActivity.get() == null) {
            Log.i("mActivity is null")
            return
        }
        Log.i(mSalaryInfo.toString())
        var isSuccess = true
        when(aParcel.mTag){
            InputViewTag.WorkingDayInputViewData -> {
                mSalaryInfo.workingDay = try {
                    (aParcel.mStrValue.toDouble() * 10).toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.PaidHolidaysViewData -> {
                mSalaryInfo.paidHolidays = try {
                    (aParcel.mStrValue.toDouble() * 10).toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.WorkingTimeInputViewData -> {
                mSalaryInfo.workingTime = try {
                    (aParcel.mStrValue.toDouble() * 10).toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
                }
            }

            InputViewTag.OverTimeInputViewData -> {
                mSalaryInfo.overtime = try {
                    (aParcel.mStrValue.toDouble() * 10).toInt()
                } catch (e: NumberFormatException) {
                    isSuccess = false
                    0
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
        Log.i(mSalaryInfo.toString())
        mActivity.get()?.onInputItem(isSuccess)
    }

    override fun saveData() {
        // Activityがnullの場合は何もしない
        if(mActivity.get() == null) {
            Log.i("mActivity is null")
            return
        }
        if(checkUserInputFinished()){
            insertOrUpdateData()
        } else {
            AlertDialog.Builder(mActivity.get()?.getActivityContext())
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        insertOrUpdateData()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun deleteData() {
        // Activityがnullの場合は何もしない
        if(mActivity.get() == null) {
            Log.i("mActivity is null")
            return
        }
        AlertDialog.Builder(mActivity.get()?.getActivityContext())
                .setTitle(R.string.dialog_title_delete)
                .setMessage(mActivity.get()?.getActivityContext()?.getString(
                        R.string.dialog_message_delete,
                        getDataDescription())
                )
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    if (!mIsNewEntry) {
                        ModelFacade.deleteSalaryInfo(mBackup)
                    }
                    dialog.dismiss()
                    mActivity.get()?.onDeleteData()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    override fun getDataDescription(): String {
        return if (mActivity.get() == null) {
            ""
        } else {
            MainApplication.getContext().getString(R.string.salary_description, mActivity.get()?.getYear(), mActivity.get()?.getMonth())
        }
    }

    override fun checkDataConsistency(): IBaseInputPresenter.DataConsistency {
        val dayOfMonth = CalendarUtil.getDaysOfMonth(getYear(), getMonth())
        // 勤務日数と有給取得数のチェック（データとしては10倍で保持しているため注意）
        if ((mSalaryInfo.workingDay + mSalaryInfo.paidHolidays) > dayOfMonth * 10) {
            Log.i("ERROR_SUM_WORKING_DAY_EXCESS")
            return IBaseInputPresenter.DataConsistency.ERROR_SUM_WORKING_DAY_EXCESS
        }

        // 基本労働時間と残業時間のチェック（データとしては10倍で保持しているため注意）
        if ((mSalaryInfo.workingTime + mSalaryInfo.overtime) > dayOfMonth * Constants.MAX_TIME_PER_DAY * 10) {
            Log.i("ERROR_SUM_WORKING_TIME_EXCESS")
            return IBaseInputPresenter.DataConsistency.ERROR_SUM_WORKING_TIME_EXCESS
        }

        // 収入と控除のチェック
        if ((getSumIncome() - getSumDeduction()) < 0) {
            Log.i("ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME")
            return IBaseInputPresenter.DataConsistency.ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME
        }
        Log.i("data : $mSalaryInfo, dayOfMonth : $dayOfMonth")
        return IBaseInputPresenter.DataConsistency.OK
    }

    override fun getYear(): Int {
        mActivity.get()?.let {
            return it.getYear()
        }
        return -1
    }

    override fun getMonth(): Int {
        mActivity.get()?.let {
            return it.getMonth()
        }
        return -1
    }

    private fun insertOrUpdateData() {
        // Activityがnullの場合は何もしない
        val activity = mActivity.get()
        if(activity == null) {
            Log.i("mActivity is null")
            return
        }
        val context = activity.getActivityContext()
        when (checkDataConsistency()) {
            IBaseInputPresenter.DataConsistency.OK -> {
                AlertDialog.Builder(mActivity.get()?.getActivityContext())
                        .setTitle(R.string.dialog_title)
                        .setMessage(mActivity.get()?.getActivityContext()?.getString(
                                R.string.dialog_message_after_tax,
                                (getSumIncome() - getSumDeduction()).toString())
                        )
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            mSalaryInfo.isComplete = true
                            if(mIsNewEntry) {
                                ModelFacade.insertSalaryInfo(mSalaryInfo)
                                mActivity.get()?.onInsertData()
                            } else {
                                ModelFacade.updateSalaryInfo(mSalaryInfo)
                                mActivity.get()?.onUpdateData()
                            }
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }

            IBaseInputPresenter.DataConsistency.ERROR_SUM_WORKING_DAY_EXCESS ->
                showErrorDialog(context, R.string.dialog_message_error_working_day)

            IBaseInputPresenter.DataConsistency.ERROR_SUM_WORKING_TIME_EXCESS ->
                showErrorDialog(context, R.string.dialog_message_error_working_time)

            IBaseInputPresenter.DataConsistency.ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME ->
                showErrorDialog(context, R.string.dialog_message_error_money)
        }
    }
}