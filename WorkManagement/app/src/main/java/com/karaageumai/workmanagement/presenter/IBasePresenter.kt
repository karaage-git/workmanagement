package com.karaageumai.workmanagement.presenter

import android.content.Context
import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag
import java.lang.NumberFormatException

private const val MAX_DAYS_PER_MONTH = 31.0
private const val MAX_TIME_PER_MONTH = 24.0 * 31.0
private const val INPUT_MAX_VALUE = 1000000000

interface IBasePresenter {
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
     *
     * @param aContext ダイアログ表示に使用するContext
     */
    fun saveData(aContext: Context)

    /**
     * 画面上に表示しているデータをDBから削除する
     *
     * @param aContext ダイアログ表示に使用するContext
     */
    fun deleteData(aContext: Context)

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
            InputViewTag.WorkingDayInputViewData -> {
                // 未入力も許可
                if (aValue.isEmpty()) {
                    return true
                } else {
                    return try {
                        val temp: Double = aValue.toDouble()
                        if (NumberFormatUtil.checkNumberFormat05(aValue)) {
                            temp <= MAX_DAYS_PER_MONTH
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
                            temp <= MAX_TIME_PER_MONTH
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
}