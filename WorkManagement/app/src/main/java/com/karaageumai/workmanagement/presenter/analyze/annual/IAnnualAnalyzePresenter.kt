package com.karaageumai.workmanagement.presenter.analyze.annual

interface IAnnualAnalyzePresenter {
    /**
     * 年を指定してデータをロードする
     */
    fun loadData(aYear: Int)
}