package com.karaageumai.workmanagement.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karaageumai.workmanagement.model.bonus.BonusInfo
import com.karaageumai.workmanagement.model.bonus.BonusInfoDao
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfoDao

@Database(entities = [SalaryInfo::class, BonusInfo::class], version = 1, exportSchema = true)
abstract class WorkManagementDB : RoomDatabase() {

    abstract fun salaryInfoDao(): SalaryInfoDao
    abstract fun bonusInfoDao(): BonusInfoDao

    companion object {
        const val DB_NAME = "work_management_db"
    }

}