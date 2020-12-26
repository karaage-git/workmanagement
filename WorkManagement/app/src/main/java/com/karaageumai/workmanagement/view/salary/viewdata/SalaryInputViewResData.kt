package com.karaageumai.workmanagement.view.salary.viewdata

import android.text.InputType
import com.karaageumai.workmanagement.R

data class SalaryInputViewResData(val mTag: SalaryInputViewTag) {
    val mTitleResId: Int
    val mSubTitleResId: Int
    val mInputHintResId: Int
    val mInputType: Int
    val mInputMaxLength: Int
    val mUnitResId: Int
    val mErrorMessageResId: Int
    val mIsCalcItem: Boolean

    init {
        when(mTag) {
            SalaryInputViewTag.WorkingDayInputViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_workingday_title
                mSubTitleResId = R.string.layoutitem_workstatus_workingday_subtitle
                mInputHintResId = R.string.edittext_hint_workstatus_workingday
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                mInputMaxLength = 4
                mUnitResId = R.string.layoutitem_workstatus_workingday_unit
                mErrorMessageResId = R.string.layoutitem_workstatus_workingday_error
                mIsCalcItem = false
            }

            SalaryInputViewTag.WorkingTimeInputViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_workingtime_title
                mSubTitleResId = R.string.layoutitem_workstatus_workingtime_common_subtitle
                mInputHintResId = R.string.edittext_hint_workstatus_workingtime
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                mInputMaxLength = 5
                mUnitResId = R.string.layoutitem_workstatus_workingtime_common_unit
                mErrorMessageResId = R.string.layoutitem_workstatus_workingtime_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.OverTimeInputViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_overtime_title
                mSubTitleResId = R.string.layoutitem_workstatus_workingtime_common_subtitle
                mInputHintResId = R.string.edittext_hint_workstatus_overtime
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                mInputMaxLength = 5
                mUnitResId = R.string.layoutitem_workstatus_workingtime_common_unit
                mErrorMessageResId = R.string.layoutitem_workstatus_workingtime_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.BaseIncomeInputViewData -> {
                mTitleResId = R.string.layoutitem_income_baseincome_title
                mSubTitleResId = R.string.layoutitem_income_baseincome_common_subtitle
                mInputHintResId = R.string.edittext_hint_income_baseincome
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_income_baseincome_common_unit
                mErrorMessageResId = R.string.layoutitem_income_baseincome_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.OverTimeIncomeInputViewData -> {
                mTitleResId = R.string.layoutitem_income_overtime_title
                mSubTitleResId = R.string.layoutitem_income_baseincome_common_subtitle
                mInputHintResId = R.string.edittext_hint_income_overtime
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_income_baseincome_common_unit
                mErrorMessageResId = R.string.layoutitem_income_baseincome_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.OtherIncomeInputViewData -> {
                mTitleResId = R.string.layoutitem_income_other_title
                mSubTitleResId = R.string.layoutitem_income_baseincome_common_subtitle
                mInputHintResId = R.string.edittext_hint_income_other
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_income_baseincome_common_unit
                mErrorMessageResId = R.string.layoutitem_income_baseincome_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.HealthInsuranceInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_health_title
                mSubTitleResId = R.string.layoutitem_deduction_health_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_health
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_health_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_health_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_longterm_title
                mSubTitleResId = R.string.layoutitem_deduction_health_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_longterm
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_health_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_health_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.PensionInsuranceInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_pension_title
                mSubTitleResId = R.string.layoutitem_deduction_health_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_pension
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_health_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_health_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.EmploymentInsuranceInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_employment_title
                mSubTitleResId = R.string.layoutitem_deduction_health_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_employment
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_health_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_health_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.IncomeTaxInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_income_title
                mSubTitleResId = R.string.layoutitem_deduction_health_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_income
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_health_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_health_common_error
                mIsCalcItem = true
            }

            SalaryInputViewTag.ResidentTaxInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_resident_title
                mSubTitleResId = R.string.layoutitem_deduction_health_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_resident
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_health_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_health_common_error
                mIsCalcItem = true
            }
        }
    }

}