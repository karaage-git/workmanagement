package com.karaageumai.workmanagement.view.resister.salary.ressetter

import java.io.Serializable

object SalaryInputViewTag {

    enum class Tag :Serializable {
        WorkingDayInputViewData,
        HealthInsuranceInputViewData,
        PensionDataInputViewData
    }

    val tagDataMap: Map<Tag, BaseSalaryDataInputViewData> = mapOf(
        Pair(Tag.WorkingDayInputViewData, WorkingDayInputViewData),
        Pair(Tag.HealthInsuranceInputViewData, HealthInsuranceInputViewData),
        Pair(Tag.PensionDataInputViewData, PensionDataInputViewData)
    )

    fun getData(aTag: Tag): BaseSalaryDataInputViewData {
        return tagDataMap.getValue(aTag)
    }

}