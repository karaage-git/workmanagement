package com.karaageumai.workmanagement.view.salary.util

import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.salary.viewdata.SalaryInputViewTag
import kotlin.NumberFormatException

class SalaryInfoHelper(private val mSalaryInfo: SalaryInfo) {

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

    /**
     * タグを指定してSalaryInfoParcelを取得する
     *
     * @param aTag 取得対象のparcelのタグ
     * @throws SalaryInfoTagNotFoundException mSalaryInfoParcelListのどの要素とも一致しなかった場合
     */
    private fun getSalaryInfoParcel(aTag: SalaryInputViewTag): SalaryInfoParcel {
        for (parcel in mSalaryInfoParcelList) {
            if(parcel.mTag == aTag){
                return parcel
            }
        }
        throw SalaryInfoTagNotFoundException("")
    }


    // タグとバリューから、メンバーのSalaryInfoのプロパティを更新するメソッド
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

    fun getSumWorkTime(): Double {
        return mSalaryInfo.workingTime + mSalaryInfo.overtime
    }

    fun getSumIncome(): Int {
        return mSalaryInfo.salary + mSalaryInfo.overtimeSalary + mSalaryInfo.otherIncome
    }

    fun getSumDeduction(): Int {
        return mSalaryInfo.healthInsuranceFee + mSalaryInfo.pensionFee
    }

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
}