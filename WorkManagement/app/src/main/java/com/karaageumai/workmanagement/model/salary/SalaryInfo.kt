package com.karaageumai.workmanagement.model.salary

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "salary_table")
data class SalaryInfo(
        // 主キー
        @PrimaryKey(autoGenerate = true) val id: Int,
        // ターゲットとなる年
        val year: Int,
        // ターゲットとなる月
        val month: Int,
        // 勤務日数（半休を考慮）（丸め誤差対策として10倍の値を保存）
        var workingDay: Int = 0,
        // 勤務時間（1時間未満を考慮）（丸め誤差対策として10倍の値を保存）
        var workingTime: Int = 0,
        // 残業時間（1時間未満を考慮）（丸め誤差対策として10倍の値を保存）
        var overtime: Int = 0,
        // 有給休暇日数（丸め誤差対策として10倍の値を保存）
        var paidHolidays: Int = 0,
        // 給料
        var baseIncome: Int = 0,
        // 残業手当
        var overtimeIncome: Int = 0,
        // その他（資格報奨金など）
        var otherIncome: Int = 0,
        // 健康保険料
        var healthInsuranceFee: Int = 0,
        // 介護保険料
        var longTermCareInsuranceFee: Int = 0,
        // 年金保険料
        var pensionFee: Int = 0,
        // 雇用保険料
        var employmentInsuranceFee: Int = 0,
        // 所得税
        var incomeTax: Int = 0,
        // 住民税
        var residentTax: Int = 0,
        // その他
        var otherDeduction: Int = 0,
        // 完了フラグ
        var isComplete: Boolean = false
) : Serializable
