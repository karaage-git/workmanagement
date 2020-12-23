package com.karaageumai.workmanagement.view.salary.modelutil

import android.os.Parcelable
import com.karaageumai.workmanagement.view.salary.viewdata.SalaryInputViewTag
import kotlinx.parcelize.Parcelize

@Parcelize
data class SalaryInfoParcel(
        val mTag: SalaryInputViewTag,
        var mStrValue: String,
        var mIsComplete: Boolean = false
) : Parcelable
