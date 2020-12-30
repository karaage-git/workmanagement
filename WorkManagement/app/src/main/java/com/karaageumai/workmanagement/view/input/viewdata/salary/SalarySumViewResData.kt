package com.karaageumai.workmanagement.view.input.viewdata.salary

import com.karaageumai.workmanagement.R

data class SalarySumViewResData(val mTag: SalarySumViewTag) {
    val mTitleResId: Int
    val mUnitResId: Int
    val mBackgroundResId: Int

    init {
        when(mTag) {
            SalarySumViewTag.WorkStatusSumViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_top
                mUnitResId = R.string.layoutitem_workstatus_top_unit
                mBackgroundResId = R.drawable.layout_frame_border_work
            }

            SalarySumViewTag.IncomeSumViewData -> {
                mTitleResId = R.string.layoutitem_income_top
                mUnitResId = R.string.layoutitem_income_top_unit
                mBackgroundResId = R.drawable.layout_frame_border_income
            }

            SalarySumViewTag.DeductionSumViewData -> {
                mTitleResId = R.string.layoutitem_deduction_top
                mUnitResId = R.string.layoutitem_deduction_top_unit
                mBackgroundResId = R.drawable.layout_frame_border_deduction
            }
        }
    }
}