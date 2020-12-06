package com.karaageumai.workmanagement.model.salary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.YearMonth

@Entity(tableName = "salary_table")
data class SalaryInfo(
    // 主キー
    @PrimaryKey(autoGenerate = true) val id: Int,
    // ターゲットとなる年
    val year: Int,
    // ターゲットとなる月
    val month: Int,
    // 勤務日数（半休を考慮）
    @ColumnInfo(name = "working_day") var workingDay: Double = 0.0,
    // 勤務時間（1時間未満を考慮）
    @ColumnInfo(name = "working_time") var workingTime: Double = 0.0,
    // 残業時間（1時間未満を考慮）
    var overtime: Double = 0.0,
    // 有給休暇日数
    @ColumnInfo(name = "paid_holidays") var paidHolidays: Double = 0.0,
    // 給料
    var salary: Int = 0,
    // 残業手当
    @ColumnInfo(name = "overtime_salary") var overtimeSalary: Int = 0,
    // その他（資格報奨金など）
    @ColumnInfo(name = "other_income") var otherIncome: Int = 0,
    // 健康保険料
    @ColumnInfo(name = "health_insurance_fee") var healthInsuranceFee: Int = 0,
    // 年金保険料
    @ColumnInfo(name = "pension_fee") var pensionFee: Int = 0,
    // 雇用保険料
    @ColumnInfo(name = "employment_insurance_fee") var employmentInsuranceFee: Int = 0,
    // 所得税
    @ColumnInfo(name = "income_tax") var incomeTax: Int = 0,
    // 住民税
    @ColumnInfo(name = "resident_tax") var residentTax: Int = 0,
    // その他
    var other: Int = 0,
    // 完了フラグ
    @ColumnInfo(name = "is_complete") var isComplete: Boolean = false
) : Serializable
