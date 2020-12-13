package com.karaageumai.workmanagement.view.resister.salary.ressetter

import android.text.InputType
import com.karaageumai.workmanagement.R

object OtherIncomeInputViewData : BaseSalaryDataInputViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_income_other_title
    }

    override fun getSubtitleResId(): Int {
        return R.string.layoutitem_income_other_subtitle
    }

    override fun getInputHintResId(): Int {
        return R.string.edittext_hint_income_other
    }

    override fun getInputType(): Int {
        return InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_NUMBER
    }

    override fun getInputMaxLength(): Int {
        return 9
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_income_other_unit
    }

    override fun getErrorMessageResId(): Int {
        return R.string.layoutitem_income_other_error
    }

    override fun isCalcItem(): Boolean {
        return true
    }
}