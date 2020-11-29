package com.karaageumai.workmanagement.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.model.salary.SalaryInfoDao

@Database(entities = [SalaryInfo::class], version = 1)
abstract class WorkManagementDB : RoomDatabase() {

    abstract fun salaryInfoDao(): SalaryInfoDao

    companion object {
        const val DB_NAME = "work_management_db"
    }

}