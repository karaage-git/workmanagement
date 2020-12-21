package com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview

import android.text.InputType
import com.karaageumai.workmanagement.R

object OverTimeIncomeInputViewData : BaseSalaryDataInputViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_income_overtime_title
    }

    override fun getSubtitleResId(): Int {
        return R.string.layoutitem_income_overtime_subtitle
    }

    override fun getInputHintResId(): Int {
        return R.string.edittext_hint_income_overtime
    }

    override fun getInputType(): Int {
        return InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
    }

    override fun getInputMaxLength(): Int {
        return 9
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_income_overtime_unit
    }

    override fun getErrorMessageResId(): Int {
        return R.string.layoutitem_income_overtime_error
    }

    override fun isCalcItem(): Boolean {
        return true
    }
}