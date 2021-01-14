package com.karaageumai.workmanagement.presenter.analyze.annual

interface IAnnualAnalyzePresenter {
    /**
     * 年を指定してデータをロードする
     */
    fun loadData(aYear: Int)

    /**
     * 年と年度を切り替える
     */
    fun changeMode(aIsWorkYearMode: Boolean)
}