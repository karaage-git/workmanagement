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
     * 月別の控除を表すデータ
     */
    @UiThread
    fun showDeductionDataDialog()

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
     * 基本支給額のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 基本支給額のリスト
     */
    fun getBaseIncomeData(aIsBonus: Boolean): List<Int>

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
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return その他のリスト
     */
    fun getOtherIncomeData(aIsBonus: Boolean): List<Int>

    /**
     * 給与/ボーナスの健康保険のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 給与の健康保険リスト
     */
    fun getHealthInsuranceFeeData(aIsBonus: Boolean): List<Int>

    /**
     * 給与/ボーナスの介護保険のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 給与の介護保険リスト
     */
    fun getLongTermCareInsuranceFeeData(aIsBonus: Boolean): List<Int>

    /**
     * 給与/ボーナスの年金保険のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 給与の年金保険リスト
     */
    fun getPensionFeeData(aIsBonus: Boolean): List<Int>

    /**
     * 給与/ボーナスの雇用保険のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 給与の雇用保険リスト
     */
    fun getEmploymentInsuranceFeeData(aIsBonus: Boolean): List<Int>

    /**
     * 給与/ボーナスの所得税のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 給与の所得税リスト
     */
    fun getIncomeTaxData(aIsBonus: Boolean): List<Int>

    /**
     * 給与の住民税のリストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @return 給与の住民税リスト
     */
    fun getResidentTaxData(): List<Int>

    /**
     * 給与/ボーナスのその他控除リストを取得する
     *
     * データが存在しない月は0を要素に持つ
     *
     * リターンされるリストは月でソート（年：1→12、年度：4→3）
     *
     * @param aIsBonus 給料 or ボーナスを判定するためのフラグ
     * @return 給与のその他控除リスト
     */
    fun getOtherDeductionData(aIsBonus: Boolean): List<Int>
}