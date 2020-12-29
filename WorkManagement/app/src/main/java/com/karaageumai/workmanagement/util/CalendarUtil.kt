package com.karaageumai.workmanagement.util

import com.karaageumai.workmanagement.Log
import kotlin.IllegalArgumentException
import kotlin.NumberFormatException

object CalendarUtil {
    enum class CheckFormatResultCode {
        // 結果：OK
        RESULT_OK,
        // 結果：年月として不適当
        RESULT_NG_ILLEGAL_FORMAT,
        // 結果：年月として解釈できるが、アプリのサポート対象外年月
        RESULT_NG_OUT_OF_RANGE
    }

    /**
     * 入力された任意の文字列が、YYYYmmとして解釈できるかチェックする
     *
     * @param aYYYYmm チェック対象の文字列
     * @return CheckFormatResultCode
     */
    fun checkFormat(aYYYYmm: String) : CheckFormatResultCode {

        if(aYYYYmm.length != 6) {
            // 6桁以外の場合は即終了
            Log.i("argument is not 6 digit")
            return CheckFormatResultCode.RESULT_NG_ILLEGAL_FORMAT
        }

        val yearMonthPair = try {
            splitYearMonth(aYYYYmm)
        } catch (e: IllegalArgumentException) {
            return CheckFormatResultCode.RESULT_NG_ILLEGAL_FORMAT
        }

        val year: Int = yearMonthPair.first
        val month: Int = yearMonthPair.second

        if((month < 1) || (month > 12)) {
            // 月が1未満 または 12より大きい場合はNG
            Log.i("Format of Month is wrong")
            return CheckFormatResultCode.RESULT_NG_ILLEGAL_FORMAT
        }

        if((year < 2000) || (year > 2050)) {
            // 2000〜2050年までを有効とみなす
            Log.i("Out of range")
            return CheckFormatResultCode.RESULT_NG_OUT_OF_RANGE
        }

        Log.i("OK")
        return CheckFormatResultCode.RESULT_OK
    }

    /**
     * 年月を表す文字列を年と月に分割する
     *
     * @param aYYYYmm 年月を表す文字列
     * @throws NumberFormatException 引数が数値に変換できない場合にスローされる
     */
    fun splitYearMonth(aYYYYmm: String): Pair<Int, Int>  {

        val yyyyMM: Int = try {
            aYYYYmm.toInt()
        } catch (e: NumberFormatException) {
            // 入力値を数値に変換できなかった場合は終了
            Log.i("argument is not Integer", e)
            throw IllegalArgumentException("$aYYYYmm is not Int.")
        }

        // 100で割った余りを月として考える
        val month: Int = yyyyMM % 100
        // 100で割った商を年として考える
        val year: Int = yyyyMM / 100

        return Pair(year, month)
    }

}