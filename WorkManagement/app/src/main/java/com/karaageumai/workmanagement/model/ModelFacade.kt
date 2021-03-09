package com.karaageumai.workmanagement.model

import androidx.room.Room
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.Constants
import kotlinx.coroutines.runBlocking

// 各APIの入り口を管理するクラス
object ModelFacade {

    // DBの取得
    private val mDb = Room.databaseBuilder(MainApplication.getContext(), WorkManagementDB::class.java, WorkManagementDB.DB_NAME).build()
    private val mSalaryInfoDao = mDb.salaryInfoDao()
    private val mBonusInfoDao = mDb.bonusInfoDao()

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
            mSalaryInfoDao.selectSalaryWithYearMonth(aYear, aMonth)
        }
        
        // ゼロ件だった場合はnullを返す
        if(salaryInfoList.isEmpty()) {
            return null
        }
        
        return salaryInfoList.first()
    }

    /**
     * 年を指定してSalaryInfoのリストを取得する
     *
     * @param aYear 年
     * @return SalaryInfoのリスト
     */
    fun selectSalaryInfo(aYear: Int): List<SalaryInfo> {
        return runBlocking {
            mSalaryInfoDao.selectSalaryWithYear(aYear)
        }
    }

    /**
     * 期間を指定してSalaryInfoを取得する
     *
     * @param aStartYear 開始年
     * @param aStartMonth 開始月
     * @param aEndYear 終了年
     * @param aEndMonth 終了月
     * @return SalaryInfoのリスト
     */
    fun selectSalaryInfo(
            aStartYear: Int,
            aStartMonth: Int,
            aEndYear: Int,
            aEndMonth: Int
    ): List<SalaryInfo> {
        return runBlocking {
            mSalaryInfoDao.selectSalaryWithTerm(aStartYear, aStartMonth, aEndYear, aEndMonth)
        }
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
            mBonusInfoDao.selectBonusWithYearMonth(aYear, aMonth)
        }

        // ゼロ件だった場合はnullを返す
        if(bonusInfoList.isEmpty()) {
            return null
        }

        return bonusInfoList.first()

    }

    /**
     * 年を指定してBonusInfoのリストを取得する
     *
     * @param aYear 年
     * @return BonusInfoのリスト
     */
    fun selectBonusInfo(aYear: Int): List<BonusInfo> {
        return runBlocking {
            mBonusInfoDao.selectBonusWithYear(aYear)
        }
    }

    /**
     * 期間を指定してBonusInfoを取得する
     *
     * @param aStartYear 開始年
     * @param aStartMonth 開始月
     * @param aEndYear 終了年
     * @param aEndMonth 終了月
     * @return BonusInfoのリスト
     */
    fun selectBonusInfo(
            aStartYear: Int,
            aStartMonth: Int,
            aEndYear: Int,
            aEndMonth: Int
    ): List<BonusInfo> {
        return runBlocking {
            mBonusInfoDao.selectBonusWithTerm(aStartYear, aStartMonth, aEndYear, aEndMonth)
        }
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

    /**
     * テストデータを仕込むメソッド
     */
    fun crateTestData(aYear: Int) {
        return runBlocking {
            // DB初期化
            mSalaryInfoDao.deleteWithYear(aYear)
            mBonusInfoDao.deleteWithYear(aYear)

            for (month in Constants.YEAR_START_MONTH .. Constants.YEAR_END_MONTH) {
                val salary = SalaryInfo(
                    0,
                    aYear,
                    month,
                    (20 - month) * 10,
                    (150 - (month * 10)) * 10,
                    (50 - month) * 10,
                    month * 10,
                    250000,
                    month * 10000,
                    month * 10000,
                    month * 1000,
                    month * 1000,
                    month * 1000,
                    month * 1000,
                    month * 1000,
                    month * 1000,
                    month * 1000
                )
                mSalaryInfoDao.insert(salary)

                if ((month == 6) or (month == 12)) {
                    val bonus = BonusInfo(
                            0,
                            aYear,
                            month,
                            500000 + month * 10000,
                            100000,
                            month * 1000,
                            month * 1000,
                            month * 1000,
                            month * 1000,
                            month * 1000,
                            month * 1000
                    )
                    mBonusInfoDao.insert(bonus)
                }
            }
        }
    }

}