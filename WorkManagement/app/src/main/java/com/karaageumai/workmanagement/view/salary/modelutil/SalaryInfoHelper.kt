package com.karaageumai.workmanagement.view.salary.modelutil

import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.salary.viewdata.SalaryInputViewTag
import java.lang.NumberFormatException

class SalaryInfoHelper(private val mSalaryInfo: SalaryInfo) {

    // SalaryInfoParcelを作成するメソッド
    fun createParcel(aTag: SalaryInputViewTag): SalaryInfoParcel {

        when (aTag) {
            SalaryInputViewTag.WorkingDayInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.workingDay.toString())
            }

            SalaryInputViewTag.WorkingTimeInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.workingTime.toString())
            }

            SalaryInputViewTag.OverTimeInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.overtime.toString())
            }

            SalaryInputViewTag.BaseIncomeInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.salary.toString())
            }

            SalaryInputViewTag.OverTimeIncomeInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.overtimeSalary.toString())
            }

            SalaryInputViewTag.OtherIncomeInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.otherIncome.toString())
            }

            SalaryInputViewTag.HealthInsuranceInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.healthInsuranceFee.toString())
            }

            SalaryInputViewTag.PensionDataInputViewData -> {
                return SalaryInfoParcel(aTag, mSalaryInfo.pensionFee.toString())
            }
        }

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

}