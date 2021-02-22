package com.karaageumai.workmanagement.presenter.input.bonus

import android.app.AlertDialog
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.presenter.input.IBaseInputPresenter
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.IBaseInputView
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import java.lang.ref.WeakReference

class BonusPresenter(aActivity: IBaseInputView) : IBonusPresenter {
    // Activity
    private val mActivity: WeakReference<IBaseInputView> = WeakReference(aActivity)
    // ボーナス情報
    private val mBonusInfo: BonusInfo
    // 新規or更新を判定するフラグ
    private val mIsNewEntry: Boolean
    // ボーナス情報として入力が必要なInputInfoParcelのリスト
    private val mInputInfoParcelList: List<InputInfoParcel>
    // 削除時に使用する変更前データ
    private val mBackup: BonusInfo

    companion object {
        // エラー時に使用するダミー情報
        private val dummyInfo = BonusInfo(0,0,0)
    }

    init {
        val activity = mActivity.get()
        if (activity != null) {
            mBonusInfo =
                ModelFacade.selectBonusInfo(activity.getYear(), activity.getMonth()).let {
                    if (it != null) {
                        mIsNewEntry = false
                        it
                    } else {
                        mIsNewEntry = true
                        BonusInfo(0, activity.getYear(), activity.getMonth())
                    }
                }
            mInputInfoParcelList = listOf(
                InputInfoParcel(
                    InputViewTag.BaseIncomeInputViewData,
                    mBonusInfo.baseIncome.toString()
                ),
                InputInfoParcel(
                    InputViewTag.OtherIncomeInputViewData,
                    mBonusInfo.otherIncome.toString()
                ),
                InputInfoParcel(
                    InputViewTag.HealthInsuranceInputViewData,
                    mBonusInfo.healthInsuranceFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.LongTermCareInsuranceFeeInputViewData,
                    mBonusInfo.longTermCareInsuranceFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.PensionInsuranceInputViewData,
                    mBonusInfo.pensionFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.EmploymentInsuranceInputViewData,
                    mBonusInfo.employmentInsuranceFee.toString()
                ),
                InputInfoParcel(
                    InputViewTag.IncomeTaxInputViewData,
                    mBonusInfo.incomeTax.toString()
                ),
                InputInfoParcel(
                    InputViewTag.OtherDeductionInputViewData,
                    mBonusInfo.otherDeduction.toString()
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
            mBackup = mBonusInfo

        } else {
            // Activityが取得できない場合はエラーとみなし、ダミーデータをセットする
            mBonusInfo = dummyInfo
            mIsNewEntry = false
            mInputInfoParcelList = listOf()
            mBackup = dummyInfo
        }

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
        // Activityがnullの場合は何もしない
        if(mActivity.get() == null) {
            Log.i("mActivity is null")
            return
        }

        // 念の為、空チェックしておく
        if (aParcel.mStrValue.isBlank()) {
            aParcel.mStrValue = "0"
        }

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
        mActivity.get()?.onInputItem(isSuccess)
    }

    override fun saveData() {
        // Activityがnullの場合は何もしない
        val activity = mActivity.get()
        if(activity == null) {
            Log.i("mActivity is null")
            return
        }

        val context = activity.getActivityContext()
        if(checkUserInputFinished()) {
            insertOrUpdateData()
        } else {
            AlertDialog.Builder(context)
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
                        ModelFacade.deleteBonusInfo(mBackup)
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
            MainApplication.getContext().getString(R.string.bonus_description, mActivity.get()?.getYear(), mActivity.get()?.getMonth())
        }
    }

    override fun checkDataConsistency(): IBaseInputPresenter.DataConsistency {
        // 収入と控除のチェック
        if ((getSumIncome() - getSumDeduction()) < 0) {
            Log.i("ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME")
            return IBaseInputPresenter.DataConsistency.ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME
        }
        Log.i("data : $mBonusInfo")
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
                            mBonusInfo.isComplete = true
                            if(mIsNewEntry) {
                                ModelFacade.insertBonusInfo(mBonusInfo)
                                mActivity.get()?.onInsertData()
                            } else {
                                ModelFacade.updateBonusInfo(mBonusInfo)
                                mActivity.get()?.onUpdateData()
                            }
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
            }

            IBaseInputPresenter.DataConsistency.ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME ->
                showErrorDialog(context, R.string.dialog_message_error_money)

            // 通常はありえないフロー（金額の整合性エラーに倒しておく）
            else -> showErrorDialog(context, R.string.dialog_message_error_money)
        }
    }
}