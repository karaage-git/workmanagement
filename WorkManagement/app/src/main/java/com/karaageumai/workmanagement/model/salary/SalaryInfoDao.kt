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
    suspend fun getSalaryWithYearMonth(aYear: Int, aMonth: Int): List<SalaryInfo>

    @Query("select * from salary_table")
    suspend fun selectAllData(): List<SalaryInfo>
}