package com.karaageumai.workmanagement.view.resister.salary

import android.os.Parcelable
import com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview.SalaryInputViewTag
import kotlinx.parcelize.Parcelize

@Parcelize
data class SalaryInfoParcel(
    val mTag: SalaryInputViewTag.Tag,
    var mStrValue: String,
    var mIsComplete: Boolean = false
) : Parcelable
