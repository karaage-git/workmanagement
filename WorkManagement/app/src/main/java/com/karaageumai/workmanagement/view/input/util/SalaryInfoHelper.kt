package com.karaageumai.workmanagement.view.input.util

import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag
import kotlin.NumberFormatException

class SalaryInfoHelper(private val mSalaryInfo: SalaryInfo, mIsNewEntry: Boolean) : BaseHelper() {
    // 給与情報として入力が必要なInputInfoParcelのリスト
    private val mInputInfoParcelList: List<InputInfoParcel> = listOf(
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

    init {
        if(mIsNewEntry){
            for (parcel in mInputInfoParcelList) {
                parcel.mIsComplete = false
            }
        } else {
            for (parcel in mInputInfoParcelList) {
                parcel.mIsComplete = true
            }
        }
    }

    /**
     * タグとバリューから、mSalaryInfoを更新する
     *
     * @param aParcelList 更新対象のSalaryInfoParcel
     */
    fun updateSalaryInfo(aParcelList: List<InputInfoParcel>) {
        for (parcel in aParcelList){
            when(parcel.mTag){
                InputViewTag.WorkingDayInputViewData -> {
                    mSalaryInfo.workingDay = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                InputViewTag.WorkingTimeInputViewData -> {
                    mSalaryInfo.workingTime = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                InputViewTag.OverTimeInputViewData -> {
                    mSalaryInfo.overtime = try {
                        parcel.mStrValue.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                InputViewTag.BaseIncomeInputViewData -> {
                    mSalaryInfo.baseIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.OverTimeIncomeInputViewData -> {
                    mSalaryInfo.overtimeIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.OtherIncomeInputViewData -> {
                    mSalaryInfo.otherIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.HealthInsuranceInputViewData -> {
                    mSalaryInfo.healthInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                    mSalaryInfo.longTermCareInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.PensionInsuranceInputViewData -> {
                    mSalaryInfo.pensionFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.EmploymentInsuranceInputViewData -> {
                    mSalaryInfo.employmentInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.IncomeTaxInputViewData -> {
                    mSalaryInfo.incomeTax = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.ResidentTaxInputViewData -> {
                    mSalaryInfo.residentTax = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.OtherDeductionInputViewData -> {
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

    override fun getInputInfoParcelList(): List<InputInfoParcel> {
        return mInputInfoParcelList
    }
}