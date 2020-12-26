package com.karaageumai.workmanagement.view.salary.util

import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.salary.viewdata.SalaryInputViewTag
import kotlin.NumberFormatException

class SalaryInfoHelper(private val mSalaryInfo: SalaryInfo, private val mIsNewEntry: Boolean) {

    // mSalaryInfoから生成されるSalaryInfoParcelのリスト
    private val mSalaryInfoParcelList: List<SalaryInfoParcel> = listOf(
            SalaryInfoParcel(SalaryInputViewTag.WorkingDayInputViewData, mSalaryInfo.workingDay.toString()),
            SalaryInfoParcel(SalaryInputViewTag.WorkingTimeInputViewData, mSalaryInfo.workingTime.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OverTimeInputViewData, mSalaryInfo.overtime.toString()),
            SalaryInfoParcel(SalaryInputViewTag.BaseIncomeInputViewData, mSalaryInfo.salary.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OverTimeIncomeInputViewData, mSalaryInfo.overtimeSalary.toString()),
            SalaryInfoParcel(SalaryInputViewTag.OtherIncomeInputViewData, mSalaryInfo.otherIncome.toString()),
            SalaryInfoParcel(SalaryInputViewTag.HealthInsuranceInputViewData, mSalaryInfo.healthInsuranceFee.toString()),
            SalaryInfoParcel(SalaryInputViewTag.PensionDataInputViewData, mSalaryInfo.pensionFee.toString())
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
                    mSalaryInfo.salary = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                SalaryInputViewTag.OverTimeIncomeInputViewData -> {
                    mSalaryInfo.overtimeSalary = try {
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

                SalaryInputViewTag.PensionDataInputViewData -> {
                    mSalaryInfo.pensionFee = try {
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
        return mSalaryInfo.salary + mSalaryInfo.overtimeSalary + mSalaryInfo.otherIncome
    }

    /**
     * 控除の合計額を算出する
     *
     * @return 控除の合計値
     */
    fun getSumDeduction(): Int {
        return mSalaryInfo.healthInsuranceFee + mSalaryInfo.pensionFee
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