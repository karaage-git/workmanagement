package com.karaageumai.workmanagement.model

import androidx.room.Room
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import kotlinx.coroutines.runBlocking

// 各APIの入り口を管理するクラス
object ModelFacade {

    // DBの取得
    private val mDb = Room.databaseBuilder(MainApplication.getContext(), WorkManagementDB::class.java, WorkManagementDB.DB_NAME).build()
    private val mSalaryInfoDao = mDb.salaryInfoDao()
    private val mBonusInfoDao = mDb.bonusInfoDao()

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

    /**
     * SalaryInfoの更新
     *
     * @param aSalaryInfo 更新データ
     */
    fun updateSalaryInfo(aSalaryInfo: SalaryInfo) {
        return runBlocking {
            mSalaryInfoDao.update(aSalaryInfo)
        }
    }

    /**
     * 給与情報を全件取得し、年月のデータリストを作成する
     *
     * @return データが存在する年と月のPairリスト
     */
    fun selectSalaryYearMonthList(): List<Pair<Int, Int>> {
        val ret: MutableList<Pair<Int, Int>> = mutableListOf()

        val dataList = runBlocking {
            mSalaryInfoDao.selectAllData()
        }

        for (data in dataList) {
            ret.add(Pair(data.year, data.month))
        }
        return ret
    }

    /**
     * 指定したSalaryInfoの削除
     */
    fun deleteSalaryInfo(aSalaryInfo: SalaryInfo) {
        return runBlocking {
            mSalaryInfoDao.delete(aSalaryInfo)
        }
    }


    /**
     * 年、月の情報から、DBにデータが存在するかチェックする
     *
     * @param aYear
     * @param aMonth
     * @return 存在する:true, 存在しない:false
     */
    fun isExistBonusInfo(aYear: Int, aMonth: Int): Boolean {
        val bonusInfoList = runBlocking {
            mBonusInfoDao.getBonusWithYearMonth(aYear, aMonth)
        }

        return bonusInfoList.isNotEmpty()

    }

    /**
     * 新規にBonusInfoをDBに挿入する
     *
     * @param aBonusInfo
     */
    fun insertBonusInfo(aBonusInfo: BonusInfo) {
        runBlocking {
            mBonusInfoDao.insert(aBonusInfo)
        }
        return
    }

    /**
     * 年、月を元にDBからBonusInfoを取得する
     *
     * @param aYear 年
     * @param aMonth 月
     * @return SalaryInfo
     */
    fun selectBonusInfo(aYear: Int, aMonth: Int): BonusInfo? {
        // 基本的にはゼロ件か1件しか取得できない想定
        val bonusInfoList = runBlocking {
            mBonusInfoDao.getBonusWithYearMonth(aYear, aMonth)
        }

        // ゼロ件だった場合はnullを返す
        if(bonusInfoList.isEmpty()) {
            return null
        }

        return bonusInfoList.first()

    }

    /**
     * BonusInfoの更新
     *
     * @param aBonusInfo 更新データ
     */
    fun updateBonusInfo(aBonusInfo: BonusInfo) {
        return runBlocking {
            mBonusInfoDao.update(aBonusInfo)
        }
    }

    /**
     * ボーナス情報を全件取得し、年月のデータリストを作成する
     *
     * @return データが存在する年と月のPairリスト
     */
    fun selectBonusYearMonthList(): List<Pair<Int, Int>> {
        val ret: MutableList<Pair<Int, Int>> = mutableListOf()

        val dataList = runBlocking {
            mBonusInfoDao.selectAllData()
        }

        for (data in dataList) {
            ret.add(Pair(data.year, data.month))
        }
        return ret
    }

    /**
     * 指定したBonusInfoの削除
     */
    fun deleteBonusInfo(aBonusInfo: BonusInfo) {
        return runBlocking {
            mBonusInfoDao.delete(aBonusInfo)
        }
    }

}