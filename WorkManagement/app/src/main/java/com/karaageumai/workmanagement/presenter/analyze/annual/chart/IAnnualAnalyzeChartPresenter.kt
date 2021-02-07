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
     * 控除前の月収データを表示するためのダイアログを出力する
     */
    @UiThread
    fun showIncomePerMonthBeforeDeductionDataDialog()

    /**
     * 控除前のボーナスデータを表示するためのダイアログを出力する
     */
    @UiThread
    fun showBonusPerMonthBeforeDeductionDataDialog()

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
    fun getWorkingBaseTimeData(): List<Double>

    /**
     * 残業時間のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return 残業時間のリスト
     */
    fun getWorkingOverTimeData(): List<Double>

    /**
     * 基本給のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return 基本給のリスト
     */
    fun getBaseIncomeData(): List<Int>

    /**
     * 残業代のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return 残業代のリスト
     */
    fun getOvertimeIncomeData(): List<Int>

    /**
     * その他収入のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return その他のリスト
     */
    fun getOtherIncomeData(): List<Int>

    /**
     * ボーナス収入のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return ボーナスのリスト
     */
    fun getBonusIncomeData(): List<Int>

    /**
     * ボーナス(その他)収入のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return ボーナス（その他）のリスト
     */
    fun getOtherBonusIncomeData(): List<Int>

}