package com.karaageumai.workmanagement.util

import java.util.*

object CalendarUtil {
    /**
     * 実行時タイミングにおける年/月を取得する
     */
    fun getCurrentYearMonth(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        return Pair(getYear(calendar), getMonth(calendar))
    }

    /**
     * 実行時タイミングにおける年を取得する
     */
    fun getCurrentYear(): Int {
        return getYear(Calendar.getInstance())
    }

    /**
     * 指定されたタイミングにおける年を取得する
     */
    private fun getYear(aCalendar: Calendar): Int {
        return aCalendar.get(Calendar.YEAR)
    }

    /**
     * 指定されたタイミングにおける月を取得する
     */
    private fun getMonth(aCalendar: Calendar): Int {
        return aCalendar.get(Calendar.MONTH) + 1
    }

    /**
     * 指定した月の日数を取得する
     */
    fun getDaysOfMonth(aYear: Int, aMonth: Int): Int {
        if ((aYear < 0) or (aMonth < 0)) {
            return 0
        }
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, aYear)
        calendar.set(Calendar.MONTH, aMonth - 1)
        return calendar.getActualMaximum(Calendar.DATE)
    }
}