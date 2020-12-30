package com.karaageumai.workmanagement.view.input.util.salary

import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.input.viewdata.salary.SalaryInputViewTag
import kotlin.NumberFormatException

class SalaryInfoHelper(private val mSalaryInfo: SalaryInfo, private val mIsNewEntry: Boolean) {

    // mSalaryInfoから生成されるSalaryInfoParcelのリスト
    private val mSalaryInfoParcelList: List<SalaryInfoParcel> = listOf(
            SalaryInfoParcel(SalaryInputViewTag.WorkingDayInputViewData, mSalaryInfo.workingDay.toString()),
            SalaryInfoParcel(SalaryInputViewTag.WorkingTimeInputViewData, mSalaryInfo.workingTime.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OverTimeInputViewData, mSalaryInfo.overtime.toString()),
            SalaryInfoParcel(SalaryInputViewTag.BaseIncomeInputViewData, mSalaryInfo.baseIncome.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OverTimeIncomeInputViewData, mSalaryInfo.overtimeIncome.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OtherIncomeInputViewData, mSalaryInfo.otherIncome.toString()),
            SalaryInfoParcel(SalaryInputViewTag.HealthInsuranceInputViewData, mSalaryInfo.healthInsuranceFee.toString()),
            SalaryInfoParcel(SalaryInputViewTag.LongTermCareInsuranceFeeInputViewData, mSalaryInfo.longTermCareInsuranceFee.toString()),
            SalaryInfoParcel(SalaryInputViewTag.PensionInsuranceInputViewData, mSalaryInfo.pensionFee.toString()),
            SalaryInfoParcel(SalaryInputViewTag.EmploymentInsuranceInputViewData, mSalaryInfo.employmentInsuranceFee.toString()),
            SalaryInfoParcel(SalaryInputViewTag.IncomeTaxInputViewData, mSalaryInfo.incomeTax.toString()),
            SalaryInfoParcel(SalaryInputViewTag.ResidentTaxInputViewData, mSalaryInfo.residentTax.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OtherDeductionInputViewData, mSalaryInfo.otherDeduction.toString())
    )

    init {
        if(mIsNewEntry){
            for (parcel in mSalaryInfoParcelList) {
                parcel.mIsComplete = false
            }
        } else {
            for (parcel in mSalaryInfoParcelList) {
                parcel.mIsComplete = true
            }
        }
    }

    /**
     * タグを指定してSalaryInfoParcelを取得する
     *
     * @param aTag 取得対象のparcelのタグ
     * @return aTagに一致するタグをもつmSalaryInfoParcelListの要素
     * @throws SalaryInfoTagNotFoundException mSalaryInfoParcelListのどの要素とも一致しなかった場合
     */
    private fun getSalaryInfoParcel(aTag: SalaryInputViewTag): SalaryInfoParcel {
        for (parcel in mSalaryInfoParcelList) {
            if(parcel.mTag == aTag){
                return parcel
            }
        }
        throw SalaryInfoTagNotFoundException("$aTag is UNKNOWN tag.")
    }


    /**
     * タグとバリューから、mSalaryInfoを更新する
     *
     * @param aParcelList 更新対象のSalaryInfoParcel
     */
    //
    fun updateSalaryInfo(aParcelList: List<SalaryInfoParcel>) {
        for (parcel in aParcelList){
            when(parcel.mTag){
                SalaryInputViewTag.WorkingDayInputViewData -> {
                    mSalaryInfo.workingDay = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                SalaryInputViewTag.WorkingTimeInputViewData -> {
                    mSalaryInfo.workingTime = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                SalaryInputViewTag.OverTimeInputViewData -> {
                    mSalaryInfo.overtime = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                SalaryInputViewTag.BaseIncomeInputViewData -> {
                    mSalaryInfo.baseIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.OverTimeIncomeInputViewData -> {
                    mSalaryInfo.overtimeIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.OtherIncomeInputViewData -> {
                    mSalaryInfo.otherIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.HealthInsuranceInputViewData -> {
                    mSalaryInfo.healthInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                    mSalaryInfo.longTermCareInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.PensionInsuranceInputViewData -> {
                    mSalaryInfo.pensionFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.EmploymentInsuranceInputViewData -> {
                    mSalaryInfo.employmentInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.IncomeTaxInputViewData -> {
                    mSalaryInfo.incomeTax = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.ResidentTaxInputViewData -> {
                    mSalaryInfo.residentTax = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.OtherDeductionInputViewData -> {
                    mSalaryInfo.otherDeduction = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }
            }
        }
    }

    /**
     * 労働時間の合計値を算出する
     *
     * @return 労働時間の合計値
     */
    fun getSumWorkTime(): Double {
        return mSalaryInfo.workingTime + mSalaryInfo.overtime
    }

    /**
     * 収入の合計値を算出する
     *
     * @return 収入の合計値
     */
    fun getSumIncome(): Int {
        return mSalaryInfo.baseIncome + mSalaryInfo.overtimeIncome + mSalaryInfo.otherIncome
    }

    /**
     * 控除の合計額を算出する
     *
     * @return 控除の合計値
     */
    fun getSumDeduction(): Int {
        return mSalaryInfo.healthInsuranceFee +
                mSalaryInfo.longTermCareInsuranceFee +
                mSalaryInfo.pensionFee +
                mSalaryInfo.employmentInsuranceFee +
                mSalaryInfo.incomeTax +
                mSalaryInfo.residentTax +
                mSalaryInfo.otherDeduction
    }

    /**
     * SalaryInputViewTagのリストから、SalaryInfoParcelのリストを返す
     * mSalaryInfoParcelListに登録されていないタグが引数にあった場合は、リターンのリストにaddしない。
     *
     * @param aTagList
     * @return
     */
    fun getSalaryInfoParcelList(aTagList: List<SalaryInputViewTag>): List<SalaryInfoParcel> {
        val retList: MutableList<SalaryInfoParcel> = mutableListOf()
        for(tag in aTagList) {
            try {
                retList.add(getSalaryInfoParcel(tag))
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
        for(parcel in mSalaryInfoParcelList) {
            ret = ret and parcel.mIsComplete
        }
        return ret
    }

}