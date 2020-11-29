package com.karaageumai.workmanagement.model

import android.content.Context
import androidx.room.Room
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.CalendarUtil

// 各APIの入り口を管理するクラス
object ModelFacade {

    // DBの取得
    private val mDb = Room.databaseBuilder(MainApplication.getContext(), WorkManagementDB::class.java, WorkManagementDB.DB_NAME).build()
    private val mDao = mDb.salaryInfoDao()

    // 有効なYYYYMMかチェックする
    fun checkYearMonth(aYYYYmm: String): CalendarUtil.Companion.CHECK_FORMAT_RESULT_CODE {
        return CalendarUtil.checkFormat(aYYYYmm)
    }

    // 年、月の情報から、DBにデータが存在するかチェックする
    suspend fun getSalaryDataWith(aYear: Int, aMonth: Int): List<SalaryInfo> {
        return mDao.getSalaryWithYearMonth(aYear, aMonth)
    }

}