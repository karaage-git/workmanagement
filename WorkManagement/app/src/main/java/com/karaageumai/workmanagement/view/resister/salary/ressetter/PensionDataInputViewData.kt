package com.karaageumai.workmanagement.view.resister.salary.ressetter

import android.text.InputType
import com.karaageumai.workmanagement.R

object PensionDataInputViewData : BaseSalaryDataInputViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_deduction_pension_title
    }

    override fun getSubtitleResId(): Int {
        return R.string.layoutitem_deduction_pension_subtitle
    }

    override fun getInputHintResId(): Int {
        return R.string.edittext_hint_deduction_pension
    }

    override fun getInputType(): Int {
        return InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
    }

    override fun getInputMaxLength(): Int {
        return 9
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_deduction_pension_unit
    }

    override fun getErrorMessageResId(): Int {
        return R.string.layoutitem_deduction_pension_error
    }

    override fun isCalcItem(): Boolean {
        return true
    }
}