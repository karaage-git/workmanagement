package com.karaageumai.workmanagement.model

import androidx.room.Room
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.CalendarUtil
import kotlinx.coroutines.runBlocking

// 各APIの入り口を管理するクラス
object ModelFacade {

    // DBの取得
    private val mDb = Room.databaseBuilder(MainApplication.getContext(), WorkManagementDB::class.java, WorkManagementDB.DB_NAME).build()
    private val mSalaryInfoDao = mDb.salaryInfoDao()
    private val mBonusInfoDao = mDb.bonusInfoDao()

    // 有効なYYYYMMかチェックする
    fun checkYearMonth(aYYYYmm: String): CalendarUtil.Companion.CheckFormatResultCode {
        return CalendarUtil.checkFormat(aYYYYmm)
    }

    /**
     * 年、月の情報から、DBにデータが存在するかチェックする
     * 
     * @param aYear
     * @param aMonth
     * @return 存在する:true, 存在しない:false
     */
    fun isExistSalaryInfo(aYear: Int, aMonth: Int): Boolean {
        val salaryInfoList = runBlocking {
            mSalaryInfoDao.getSalaryWithYearMonth(aYear, aMonth)
        }

        return salaryInfoList.isNotEmpty()

    }

    /**
     * 新規にSalaryInfoをDBに挿入する
     * 
     * @param aSalaryInfo
     */
    fun insertSalaryInfo(aSalaryInfo: SalaryInfo) {
        runBlocking {
            mSalaryInfoDao.insert(aSalaryInfo)
        }
        return
    }

    /**
     * 年、月を元にDBからSalaryInfoを取得する
     * 
     * @param aYear 年
     * @param aMonth 月
     * @return SalaryInfo
     */
    fun selectSalaryInfo(aYear: Int, aMonth: Int): SalaryInfo? {
        // 基本的にはゼロ件か1件しか取得できない想定
        val salaryInfoList = runBlocking {
            mSalaryInfoDao.getSalaryWithYearMonth(aYear, aMonth)
        }
        
        // ゼロ件だった場合はnullを返す
        if(salaryInfoList.isEmpty()) {
            return null
        }
        
        return salaryInfoList.first()

    }

    fun updateSalaryInfo(aSalaryInfo: SalaryInfo) {
        return runBlocking {
            mSalaryInfoDao.update(aSalaryInfo)
        }
    }

}