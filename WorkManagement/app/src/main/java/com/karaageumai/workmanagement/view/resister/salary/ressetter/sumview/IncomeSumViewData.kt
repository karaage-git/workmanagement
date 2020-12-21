package com.karaageumai.workmanagement.view.resister.salary.ressetter.sumview

import com.karaageumai.workmanagement.R

object IncomeSumViewData : BaseSalaryDataSumViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_income_top
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_income_top_unit
    }

    override fun getBackgroundResId(): Int {
        return R.drawable.layout_frame_border_income
    }
}