package com.karaageumai.workmanagement.view.salary.viewdata.sumview

import com.karaageumai.workmanagement.R

object WorkStatusSumViewData : BaseSalaryDataSumViewData {
    override fun getTitleResId(): Int {
        return R.string.layoutitem_workstatus_top
    }

    override fun getUnitResId(): Int {
        return R.string.layoutitem_workstatus_top_unit
    }

    override fun getBackgroundResId(): Int {
        return R.drawable.layout_frame_border_work
    }
}