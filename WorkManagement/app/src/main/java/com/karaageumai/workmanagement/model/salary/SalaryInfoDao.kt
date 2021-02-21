package com.karaageumai.workmanagement.model.salary

import androidx.room.*

@Dao
interface SalaryInfoDao {
    @Insert
    suspend fun insert(aData: SalaryInfo)

    @Update
    suspend fun update(aData: SalaryInfo)

    @Delete
    suspend fun delete(aData: SalaryInfo)

    @Query("select * from salary_table where year = :aYear and month = :aMonth")
    suspend fun selectSalaryWithYearMonth(aYear: Int, aMonth: Int): List<SalaryInfo>

    @Query("select * from salary_table where year = :aYear")
    suspend fun selectSalaryWithYear(aYear: Int): List<SalaryInfo>

    @Query("select * from salary_table")
    suspend fun selectAllData(): List<SalaryInfo>

    @Query("delete from salary_table")
    suspend fun deleteAllData()

    @Query("delete from salary_table where year = :aYear")
    suspend fun deleteWithYear(aYear: Int)

    @Query("""
        select * from salary_table 
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
    suspend fun selectSalaryWithTerm(aStartYear: Int, aStartMonth: Int, aEndYear: Int, aEndMonth: Int): List<SalaryInfo>
}