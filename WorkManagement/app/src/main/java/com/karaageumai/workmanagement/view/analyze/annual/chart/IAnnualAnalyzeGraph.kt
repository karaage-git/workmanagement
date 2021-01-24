package com.karaageumai.workmanagement.view.analyze.annual.chart

interface IAnnualAnalyzeGraph {
    /**
     * 表示対象の年を取得する
     *
     * @return 年
     */
    fun getYear(): Int

    /**
     * 年or年度を示すフラグを取得する
     *
     * @return true:年度 / false:年
     */
    fun getIsWorkYearMode(): Boolean
}