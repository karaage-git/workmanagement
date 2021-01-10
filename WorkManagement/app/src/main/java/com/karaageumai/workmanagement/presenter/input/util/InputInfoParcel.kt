package com.karaageumai.workmanagement.presenter.input.util

import android.os.Parcelable
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import kotlinx.parcelize.Parcelize

@Parcelize
data class InputInfoParcel(
        val mTag: InputViewTag,
        var mStrValue: String,
        var mIsComplete: Boolean = false
) : Parcelable
