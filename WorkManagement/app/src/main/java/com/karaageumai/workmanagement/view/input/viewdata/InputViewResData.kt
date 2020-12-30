package com.karaageumai.workmanagement.view.input.viewdata

import android.text.InputType
import com.karaageumai.workmanagement.R

data class InputViewResData(val mTag: InputViewTag) {
    val mTitleResId: Int
    val mSubTitleResId: Int
    val mInputHintResId: Int
    val mInputType: Int
    val mInputMaxLength: Int
    val mUnitResId: Int
    val mErrorMessageResId: Int

    init {
        when(mTag) {
            InputViewTag.WorkingDayInputViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_workingday_title
                mSubTitleResId = R.string.layoutitem_workstatus_workingday_subtitle
                mInputHintResId = R.string.edittext_hint_workstatus_workingday
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                mInputMaxLength = 4
                mUnitResId = R.string.layoutitem_workstatus_workingday_unit
                mErrorMessageResId = R.string.layoutitem_workstatus_workingday_error
            }

            InputViewTag.WorkingTimeInputViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_workingtime_title
                mSubTitleResId = R.string.layoutitem_workstatus_common_subtitle
                mInputHintResId = R.string.edittext_hint_workstatus_workingtime
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                mInputMaxLength = 5
                mUnitResId = R.string.layoutitem_workstatus_common_unit
                mErrorMessageResId = R.string.layoutitem_workstatus_common_error
            }

            InputViewTag.OverTimeInputViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_overtime_title
                mSubTitleResId = R.string.layoutitem_workstatus_common_subtitle
                mInputHintResId = R.string.edittext_hint_workstatus_overtime
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                mInputMaxLength = 5
                mUnitResId = R.string.layoutitem_workstatus_common_unit
                mErrorMessageResId = R.string.layoutitem_workstatus_common_error
            }

            InputViewTag.BaseIncomeInputViewData -> {
                mTitleResId = R.string.layoutitem_income_baseincome_title
                mSubTitleResId = R.string.layoutitem_income_common_subtitle
                mInputHintResId = R.string.edittext_hint_income_baseincome
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_income_common_unit
                mErrorMessageResId = R.string.layoutitem_income_common_error
            }

            InputViewTag.OverTimeIncomeInputViewData -> {
                mTitleResId = R.string.layoutitem_income_overtime_title
                mSubTitleResId = R.string.layoutitem_income_common_subtitle
                mInputHintResId = R.string.edittext_hint_income_overtime
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_income_common_unit
                mErrorMessageResId = R.string.layoutitem_income_common_error
            }

            InputViewTag.OtherIncomeInputViewData -> {
                mTitleResId = R.string.layoutitem_income_other_title
                mSubTitleResId = R.string.layoutitem_income_common_subtitle
                mInputHintResId = R.string.edittext_hint_income_other
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_income_common_unit
                mErrorMessageResId = R.string.layoutitem_income_common_error
            }

            InputViewTag.HealthInsuranceInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_health_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_health
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }

            InputViewTag.LongTermCareInsuranceFeeInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_longterm_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_longterm
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }

            InputViewTag.PensionInsuranceInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_pension_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_pension
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }

            InputViewTag.EmploymentInsuranceInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_employment_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_employment
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }

            InputViewTag.IncomeTaxInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_income_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_income
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }

            InputViewTag.ResidentTaxInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_resident_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_resident
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }

            InputViewTag.OtherDeductionInputViewData -> {
                mTitleResId = R.string.layoutitem_deduction_other_title
                mSubTitleResId = R.string.layoutitem_deduction_common_subtitle
                mInputHintResId = R.string.edittext_hint_deduction_other
                mInputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
                mInputMaxLength = 9
                mUnitResId = R.string.layoutitem_deduction_common_unit
                mErrorMessageResId = R.string.layoutitem_deduction_common_error
            }
        }
    }

}