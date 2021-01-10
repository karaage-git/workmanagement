package com.karaageumai.workmanagement.presenter.input.viewdata

// ボーナス情報は給与情報の一部として表現できるため、タグはInputViewTagの1種のみで必要十分
enum class InputViewTag {
    WorkingDayInputViewData,
    WorkingTimeInputViewData,
    OverTimeInputViewData,
    BaseIncomeInputViewData,
    OverTimeIncomeInputViewData,
    OtherIncomeInputViewData,
    HealthInsuranceInputViewData,
    LongTermCareInsuranceFeeInputViewData,
    PensionInsuranceInputViewData,
    EmploymentInsuranceInputViewData,
    IncomeTaxInputViewData,
    ResidentTaxInputViewData,
    OtherDeductionInputViewData
}