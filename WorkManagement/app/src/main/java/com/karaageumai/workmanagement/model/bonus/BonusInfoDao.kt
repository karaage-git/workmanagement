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
    suspend fun selectBonusWithYearMonth(aYear: Int, aMonth: Int): List<BonusInfo>

    @Query("select * from bonus_table where year = :aYear")
    suspend fun selectBonusWithYear(aYear: Int): List<BonusInfo>

    @Query("select * from bonus_table")
    suspend fun selectAllData(): List<BonusInfo>

    @Query("delete from bonus_table")
    suspend fun deleteAllData()

    @Query("delete from bonus_table where year = :aYear")
    suspend fun deleteWithYear(aYear: Int)

    @Query("""
        select * from bonus_table 
        where
            case
                when (:aEndYear - :aStartYear >= 2)
                    then ((year = :aStartYear and month >= :aStartMonth) or (year >= :aStartYear + 1 and year <= :aEndYear - 1) or (year = :aEndYear and month <= :aEndMonth))
                when (:aEndYear - :aStartYear = 1)
                    then ((year = :aStartYear and month >= :aStartMonth) or (year = :aEndYear and month <= :aEndMonth))
                when (:aEndYear - :aStartYear = 0)
                    then
                        case
                            when (:aStartMonth <= :aEndMonth)
                                then ((year = :aStartYear and month >= :aStartMonth and month <= :aEndMonth))
                            else (year = 0 and month = 0)
                        end
                else (year = 0 and month = 0)
            end
    """)
    suspend fun selectBonusWithTerm(aStartYear: Int, aStartMonth: Int, aEndYear: Int, aEndMonth: Int): List<BonusInfo>
}