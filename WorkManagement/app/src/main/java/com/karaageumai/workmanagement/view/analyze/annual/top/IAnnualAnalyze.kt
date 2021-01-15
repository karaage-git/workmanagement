package com.karaageumai.workmanagement.view.analyze.annual.top

import com.karaageumai.workmanagement.presenter.analyze.annual.util.AnnualDataRow

interface IAnnualAnalyze {
    /**
     * View側の表示年を取得する
     */
    fun getYear(): Int

    /**
     * Activity側の年or年度モードを取得する
     * @return true : 年度モード / false : 年モード
     */
    fun getIsWorkYearMode(): Boolean

    /**
     * 表示データの準備ができた際にコールされる
     *
     * @param aLoadDataList ロードできたデータのリスト
     */
    fun onLoadedData(aLoadDataList: List<AnnualDataRow>)
}