package com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview

import android.text.InputType
import com.karaageumai.workmanagement.R

object WorkingDayInputViewData : BaseSalaryDataInputViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_workstatus_workingday_title
    }

    override fun getSubtitleResId(): Int {
        return R.string.layoutitem_workstatus_workingday_subtitle
    }

    override fun getInputHintResId(): Int {
        return R.string.edittext_hint_workstatus_workingday
    }

    override fun getInputType(): Int {
        return InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    override fun getInputMaxLength(): Int {
        return 4
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_workstatus_workingday_unit
    }

    override fun getErrorMessageResId(): Int {
        return R.string.layoutitem_workstatus_workingday_error
    }

    override fun isCalcItem(): Boolean {
        return false
    }
}