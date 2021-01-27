package com.karaageumai.workmanagement.view.analyze.annual.chart

import android.content.Context
import android.view.LayoutInflater

interface IAnnualAnalyzeChart {
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

    /**
     * ActivityからContextを取得する
     *
     * @return Context
     */
    fun getActivityContext(): Context

    /**
     * ActivityからLayoutInflaterを取得する
     *
     * @return LayoutInflater
     */
    fun getLayoutInflater(): LayoutInflater
}