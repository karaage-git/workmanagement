package com.karaageumai.workmanagement.presenter.input.viewdata

import com.karaageumai.workmanagement.R

data class SumViewResData(val mTag: SumViewTag) {
    val mTitleResId: Int
    val mUnitResId: Int
    val mBackgroundResId: Int

    init {
        when(mTag) {
            SumViewTag.WorkStatusSumViewData -> {
                mTitleResId = R.string.layoutitem_workstatus_top
                mUnitResId = R.string.layoutitem_workstatus_top_unit
                mBackgroundResId = R.drawable.layout_frame_border_work
            }

            SumViewTag.IncomeSumViewData -> {
                mTitleResId = R.string.layoutitem_income_top
                mUnitResId = R.string.layoutitem_income_top_unit
                mBackgroundResId = R.drawable.layout_frame_border_income
            }

            SumViewTag.DeductionSumViewData -> {
                mTitleResId = R.string.layoutitem_deduction_top
                mUnitResId = R.string.layoutitem_deduction_top_unit
                mBackgroundResId = R.drawable.layout_frame_border_deduction
            }
        }
    }
}