package com.karaageumai.workmanagement.view.salary.viewdata.sumview

import java.io.Serializable

interface BaseSalaryDataSumViewData : Serializable {
    /**
     * タイトルのリソースID取得
     *
     * @return タイトルのリソースID
     */
    fun getTitleResId(): Int

    /**
     * 単位のリソースIDを取得
     *
     * @return 単位のリソースID
     */
    fun getUnitResId(): Int

    /**
     * 背景のリソースIDを取得
     *
     * @return 背景のリソースID
     */
    fun getBackgroundResId(): Int
}