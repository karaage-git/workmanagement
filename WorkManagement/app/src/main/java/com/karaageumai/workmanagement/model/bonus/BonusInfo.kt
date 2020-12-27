package com.karaageumai.workmanagement.model.bonus

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "bonus_table")
data class BonusInfo(
    // 主キー
    @PrimaryKey(autoGenerate = true) val id: Int,
    // 支給年
    val year: Int,
    // 支給月
    val month: Int,
    // 支給額
    val bonus: Int,
    // その他の支給
    val otherPayment: Int,
    // 健康保険料
    @ColumnInfo(name = "health_insurance_fee") var healthInsuranceFee: Int = 0,
    // 介護保険料
    @ColumnInfo(name = "long_term_care_insurance_fee") var longTermCareInsuranceFee: Int = 0,
    // 年金保険料
    @ColumnInfo(name = "pension_fee") var pensionFee: Int = 0,
    // 雇用保険料
    @ColumnInfo(name = "employment_insurance_fee") var employmentInsuranceFee: Int = 0,
    // 所得税
    @ColumnInfo(name = "income_tax") var incomeTax: Int = 0,
    // その他
    var other: Int = 0,
    // 完了フラグ
    @ColumnInfo(name = "is_complete") var isComplete: Boolean = false
    ) : Serializable
