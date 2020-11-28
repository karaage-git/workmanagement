package com.karaageumai.workmanagement.model

import java.time.YearMonth

data class SalaryInfo(
    // ターゲットとなる年月
    val yearMonth: YearMonth,
    // 勤務日数（半休を考慮）
    var workingDay: Float = 0f,
    // 勤務時間（1時間未満を考慮）
    var workingTime: Float = 0f,
    // 残業時間（1時間未満を考慮）
    var overtime: Float = 0f,
    // 有給休暇日数
    var paidHolidays: Float = 0f,
    // 給料
    var salary: Int = 0,
    // 残業手当
    var overtimeSalary: Int = 0,
    // 交通費など
    var transportationExpenses: Int = 0,
    // 健康保険料
    var healthInsuranceFee: Int = 0,
    // 年金保険料
    var pensionFee: Int = 0,
    // 雇用保険料
    var employmentInsuranceFee: Int = 0,
    // 所得税
    var incomeTax: Int = 0,
    // 住民税
    var residentTax: Int = 0,
    // その他
    var other: Int = 0
)
