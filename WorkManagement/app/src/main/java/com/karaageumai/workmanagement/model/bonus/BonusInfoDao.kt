package com.karaageumai.workmanagement.model.bonus

import androidx.room.*

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
}