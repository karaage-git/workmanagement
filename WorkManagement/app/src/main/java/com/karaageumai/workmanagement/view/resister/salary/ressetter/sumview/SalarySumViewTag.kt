package com.karaageumai.workmanagement.view.resister.salary.ressetter.sumview

import java.io.Serializable

object SalarySumViewTag {

    enum class Tag : Serializable {
        WorkStatusSumViewData,
        IncomeSumViewData,
        DeductionSumViewData
    }

    val tagMap:Map<Tag, BaseSalaryDataSumViewData> = mapOf(
            Pair(Tag.WorkStatusSumViewData, WorkStatusSumViewData),
            Pair(Tag.IncomeSumViewData, IncomeSumViewData),
            Pair(Tag.DeductionSumViewData, DeductionSumViewData)
    )

}