package com.karaageumai.workmanagement.presenter.analyze.annual.chart

import androidx.annotation.UiThread

interface IAnnualAnalyzeChartPresenter {

    /**
     * 勤務日数データを表示するダイアログを出力する
     */
    @UiThread
    fun showWorkingDayDataDialog()

    /**
     * 有給休暇データを表示するダイアログを出力する
     */
    @UiThread
    fun showPaidHolidayDataDialog()

    /**
     * 労働時間データを表示するダイアログを出力する
     */
    @UiThread
    fun showWorkingTimeDataDialog()

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

    /**
     * 有給休暇の月別グラフを表示するために必要なデータを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return 月別の有給休暇取得日数データ
     */
    fun getPaidHolidayData(): List<Double>

    /**
     * 所定労働時間のリストを取得する
     *
     * @return 所定労働時間のリスト
     */
    fun getWorkingBaseTime(): List<Double>

    /**
     * 残業時間のリストを取得する
     *
     * @return 残業時間のリスト
     */
    fun getWorkingOverTime(): List<Double>


}