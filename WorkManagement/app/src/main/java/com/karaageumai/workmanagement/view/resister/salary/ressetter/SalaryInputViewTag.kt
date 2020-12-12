package com.karaageumai.workmanagement.view.resister.salary.ressetter

import java.io.Serializable

object SalaryInputViewTag {
    // Tag
    enum class Tag :Serializable {
        WorkingDayInputViewData,
        WorkingTimeInputViewData,
        OverTimeInputViewData,
        HealthInsuranceInputViewData,
        PensionDataInputViewData
    }

    // Tagとデータをマッピング
    val tagDataMap: Map<Tag, BaseSalaryDataInputViewData> = mapOf(
        Pair(Tag.WorkingDayInputViewData, WorkingDayInputViewData),
        Pair(Tag.WorkingTimeInputViewData, WorkingTimeInputViewData),
        Pair(Tag.OverTimeInputViewData, OverTimeInputViewData),
        Pair(Tag.HealthInsuranceInputViewData, HealthInsuranceInputViewData),
        Pair(Tag.PensionDataInputViewData, PensionDataInputViewData)
    )

}