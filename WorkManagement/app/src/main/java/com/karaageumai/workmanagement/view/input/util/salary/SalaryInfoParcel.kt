package com.karaageumai.workmanagement.view.input.util.salary

import android.os.Parcelable
import com.karaageumai.workmanagement.view.input.viewdata.salary.SalaryInputViewTag
import kotlinx.parcelize.Parcelize

@Parcelize
data class SalaryInfoParcel(
        val mTag: SalaryInputViewTag,
        var mStrValue: String,
        var mIsComplete: Boolean = false
) : Parcelable
