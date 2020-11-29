package com.karaageumai.workmanagement.model

import android.content.Context
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.util.CalendarUtil

// 各APIの入り口を管理するクラス
object ModelFacade {

    fun checkYearMonth(aYYYYmm: String): CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE {
        return CalendarUtil.checkFormat(aYYYYmm)
    }


}