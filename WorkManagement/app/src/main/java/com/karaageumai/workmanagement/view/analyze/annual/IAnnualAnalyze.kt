package com.karaageumai.workmanagement.view.analyze.annual

import com.karaageumai.workmanagement.presenter.analyze.annual.util.AnnualDataRow

interface IAnnualAnalyze {
    /**
     * View側の表示年を取得する
     */
    fun getYear(): Int

    /**
     * 表示データの準備ができた際にコールされる
     *
     * @param aLoadDataList ロードできたデータのリスト
     */
    fun onLoadedData(aLoadDataList: List<AnnualDataRow>)
}