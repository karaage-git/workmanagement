package com.karaageumai.workmanagement.model.bonus

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
        var baseIncome: Int = 0,
        // その他の支給
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
        // その他
        var otherDeduction: Int = 0,
        // 完了フラグ
        var isComplete: Boolean = false
) : Serializable
