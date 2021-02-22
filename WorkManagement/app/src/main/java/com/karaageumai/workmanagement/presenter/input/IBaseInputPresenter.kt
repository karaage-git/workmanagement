package com.karaageumai.workmanagement.presenter.input

import android.app.AlertDialog
import android.content.Context
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.util.CalendarUtil
import com.karaageumai.workmanagement.util.Constants.INPUT_MAX_VALUE
import com.karaageumai.workmanagement.util.Constants.MAX_TIME_PER_DAY
import java.lang.NumberFormatException

interface IBaseInputPresenter {
    /**
     * タグを指定してInputInfoParcelを取得する
     *
     * @param aTag 取得対象のparcelのタグ
     * @return aTagに一致するタグをもつmSalaryInfoParcelListの要素
     * @throws SalaryInfoTagNotFoundException mSalaryInfoParcelListのどの要素とも一致しなかった場合
     */
    private fun getInputInfoParcel(aTag: InputViewTag): InputInfoParcel {
        for (parcel in getInputInfoParcelList()) {
            if(parcel.mTag == aTag){
                return parcel
            }
        }
        throw SalaryInfoTagNotFoundException("$aTag is UNKNOWN tag.")
    }

    /**
     * InputViewTagのリストを使用して、子クラスのメンバで保持しているInputInfoParcelを返す
     * 登録されていないタグが引数にあった場合は、リターンするリストにaddしない。
     *
     * @param aTagList
     * @return
     */
    fun getInputInfoParcelList(aTagList: List<InputViewTag>): List<InputInfoParcel> {
        val retList: MutableList<InputInfoParcel> = mutableListOf()
        for(tag in aTagList) {
            try {
                retList.add(getInputInfoParcel(tag))
            } catch (e: SalaryInfoTagNotFoundException) {
                continue
            }
        }
        return retList
    }

    /**
     * ユーザの入力が完了しているかチェックする
     */
    fun checkUserInputFinished(): Boolean {
        var ret = true
        for(parcel in getInputInfoParcelList()) {
            ret = ret and parcel.mIsComplete
        }
        return ret
    }

    /**
     * 子クラスで保持するInputInfoParcelのリストを取得する
     *
     * @return 子クラスで保持するInputInfoParcelのリスト
     */
    fun getInputInfoParcelList(): List<InputInfoParcel>

    /**
     * 入力データでPresenterが保持するデータを更新する
     */
    fun updateItem(aParcel: InputInfoParcel)

    /**
     * データをDBに保存する
     */
    fun saveData()

    /**
     * 画面上に表示しているデータをDBから削除する
     */
    fun deleteData()

    /**
     * データの説明を取得する
     *
     * @return データの説明
     */
    fun getDataDescription(): String

    /**
     * 入力値をチェックする
     *
     * @param aTag
     * @param aValue
     * @return true:OK / false:NG
     */
    fun checkInputData(aTag: InputViewTag, aValue: String): Boolean {
        when (aTag) {
            // 0.5単位、最大値は1ヶ月、未入力不可
            InputViewTag.WorkingDayInputViewData,
            InputViewTag.PaidHolidaysViewData -> {
                // 未入力も許可
                if (aValue.isEmpty()) {
                    return true
                } else {
                    return try {
                        val temp: Double = aValue.toDouble()
                        if (NumberFormatUtil.checkNumberFormat05(aValue)) {
                            // 当該月の日数以下の場合に有効とみなす
                            temp <= CalendarUtil.getDaysOfMonth(getYear(), getMonth())
                        } else {
                            // 0.5単位でなければ無効
                            false
                        }
                    } catch (e: NumberFormatException) {
                        // 数値でなければ無効
                        false
                    }
                }
            }

            // 0.1単位、最大値は1ヶ月の時間、未入力不可
            InputViewTag.WorkingTimeInputViewData,
            InputViewTag.OverTimeInputViewData -> {
                // 未入力も許可
                if (aValue.isEmpty()) {
                    return true
                } else {
                    return try {
                        val temp: Double = aValue.toDouble()
                        if (NumberFormatUtil.checkNumberFormat01(aValue)) {
                            // 当該月の最大時間以下の場合に有効とみなす
                            temp <= CalendarUtil.getDaysOfMonth(getYear(), getMonth()) * MAX_TIME_PER_DAY
                        } else {
                            // 0.1単位でなければ無効
                            false
                        }
                    } catch (e: NumberFormatException) {
                        // 数値でなければ無効
                        false
                    }
                }
            }

            // 整数、最大値あり、未入力可
            InputViewTag.BaseIncomeInputViewData,
            InputViewTag.OverTimeIncomeInputViewData,
            InputViewTag.OtherIncomeInputViewData,
            InputViewTag.HealthInsuranceInputViewData,
            InputViewTag.LongTermCareInsuranceFeeInputViewData,
            InputViewTag.PensionInsuranceInputViewData,
            InputViewTag.EmploymentInsuranceInputViewData,
            InputViewTag.IncomeTaxInputViewData,
            InputViewTag.ResidentTaxInputViewData,
            InputViewTag.OtherDeductionInputViewData -> {
                // 未入力も許可
                if (aValue.isEmpty()) {
                    return true
                } else {
                    return try {
                        val temp: Int = aValue.toInt()
                        if (NumberFormatUtil.checkNaturalNumberFormat(aValue)) {
                            temp <= INPUT_MAX_VALUE
                        } else {
                            // 整数でなければ無効
                            false
                        }
                    } catch (e: NumberFormatException) {
                        // 数値でなければ無効
                        false
                    }
                }
            }
        }
    }

    /**
     * 整合性チェックの結果
     */
    enum class DataConsistency {
        // 整合性OK
        OK,
        // 勤務日数と有給の合計が1月の最大日数を超えている
        ERROR_SUM_WORKING_DAY_EXCESS,
        // 勤務時間、残業時間の合計値が、1ヶ月の最大時間を超えている
        ERROR_SUM_WORKING_TIME_EXCESS,
        // 収入よりも控除が多い（手取りがマイナスになっている）
        ERROR_DEDUCTION_IS_HIGHER_THAN_INCOME
    }

    /**
     * データの整合性をチェックする
     */
    fun checkDataConsistency(): DataConsistency

    /**
     * 対象年を取得する
     *
     * @return 年（取得できない場合は負の値を返す）
     */
    fun getYear(): Int

    /**
     * 対象月を取得する
     *
     * @return 月（取得できない場合は負の値を返す）
     */
    fun getMonth(): Int

    /**
     * OKボタンがあるダイアログを表示するための共通メソッド
     */
    fun showErrorDialog(aContext: Context, aMessageResId: Int) {
        AlertDialog.Builder(aContext)
                .setTitle(R.string.dialog_title)
                .setMessage(aMessageResId)
                .setPositiveButton(R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }
}