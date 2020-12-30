package com.karaageumai.workmanagement.view.input.util

import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag

class BonusInfoHelper(private val mBonusInfo: BonusInfo, mIsNewEntry: Boolean) : BaseHelper() {
    // ボーナス情報として入力が必要なInputInfoParcelのリスト
    private val mInputInfoParcelList: List<InputInfoParcel> = listOf(
        InputInfoParcel(InputViewTag.BaseIncomeInputViewData, mBonusInfo.baseIncome.toString()),
        InputInfoParcel(InputViewTag.OtherIncomeInputViewData, mBonusInfo.otherIncome.toString()),
        InputInfoParcel(InputViewTag.HealthInsuranceInputViewData, mBonusInfo.healthInsuranceFee.toString()),
        InputInfoParcel(InputViewTag.LongTermCareInsuranceFeeInputViewData, mBonusInfo.longTermCareInsuranceFee.toString()),
        InputInfoParcel(InputViewTag.PensionInsuranceInputViewData, mBonusInfo.pensionFee.toString()),
        InputInfoParcel(InputViewTag.EmploymentInsuranceInputViewData, mBonusInfo.employmentInsuranceFee.toString()),
        InputInfoParcel(InputViewTag.IncomeTaxInputViewData, mBonusInfo.incomeTax.toString()),
        InputInfoParcel(InputViewTag.OtherDeductionInputViewData, mBonusInfo.otherDeduction.toString())
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
    fun updateInputInfoParcel(aParcelList: List<InputInfoParcel>) {
        for (parcel in aParcelList){
            when(parcel.mTag){
                InputViewTag.BaseIncomeInputViewData -> {
                    mBonusInfo.baseIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.OtherIncomeInputViewData -> {
                    mBonusInfo.otherIncome = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.HealthInsuranceInputViewData -> {
                    mBonusInfo.healthInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                    mBonusInfo.longTermCareInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.PensionInsuranceInputViewData -> {
                    mBonusInfo.pensionFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.EmploymentInsuranceInputViewData -> {
                    mBonusInfo.employmentInsuranceFee = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.IncomeTaxInputViewData -> {
                    mBonusInfo.incomeTax = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                InputViewTag.OtherDeductionInputViewData -> {
                    mBonusInfo.otherDeduction = try {
                        parcel.mStrValue.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                }

                // 給与情報向けのタグが来た場合は何もしない
                else -> {}
            }
        }
    }

    /**
     * 収入の合計値を算出する
     *
     * @return 収入の合計値
     */
    fun getSumIncome(): Int {
        return mBonusInfo.baseIncome + mBonusInfo.otherIncome
    }

    /**
     * 控除の合計額を算出する
     *
     * @return 控除の合計値
     */
    fun getSumDeduction(): Int {
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
}