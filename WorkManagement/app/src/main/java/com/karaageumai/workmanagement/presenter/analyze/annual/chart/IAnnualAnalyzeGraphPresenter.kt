package com.karaageumai.workmanagement.presenter.analyze.annual.chart

interface IAnnualAnalyzeGraphPresenter {
    /**
     * 勤務日数の月別グラフを表示するために必要なデータを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return 月別の勤務日数データ
     */
    fun getWorkingDayData(): List<Double>
}