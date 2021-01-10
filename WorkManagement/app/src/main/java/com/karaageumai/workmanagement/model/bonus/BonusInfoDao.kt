package com.karaageumai.workmanagement.model.bonus

import androidx.room.*
import com.karaageumai.workmanagement.model.salary.SalaryInfo

@Dao
interface BonusInfoDao {
    @Insert
    suspend fun insert(aData: BonusInfo)

    @Update
    suspend fun update(aData: BonusInfo)

    @Delete
    suspend fun delete(aData: BonusInfo)

    @Query("select * from bonus_table where year = :aYear and month = :aMonth")
    suspend fun getBonusWithYearMonth(aYear: Int, aMonth: Int): List<BonusInfo>

    @Query("select * from bonus_table where year = :aYear")
    suspend fun getBonusWithYear(aYear: Int): List<BonusInfo>

    @Query("select * from bonus_table")
    suspend fun selectAllData(): List<BonusInfo>
}