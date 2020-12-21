package com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview

import android.text.InputType
import com.karaageumai.workmanagement.R

object WorkingTimeInputViewData : BaseSalaryDataInputViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_workstatus_workingtime_title
    }

    override fun getSubtitleResId(): Int {
        return R.string.layoutitem_workstatus_workingtime_subtitle
    }

    override fun getInputHintResId(): Int {
        return R.string.edittext_hint_workstatus_workingtime
    }

    override fun getInputType(): Int {
        return InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    override fun getInputMaxLength(): Int {
        return 5
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_workstatus_workingtime_unit
    }

    override fun getErrorMessageResId(): Int {
        return R.string.layoutitem_workstatus_workingtime_error
    }

    override fun isCalcItem(): Boolean {
        return true
    }
}