package com.karaageumai.workmanagement.util

import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

class CalendarUtil {

    companion object {

        private val modelFacade: ModelFacade = ModelFacade

        enum class CheckFormatResultCode {
            RESULT_OK_NEW_ENTRY,
            RESULT_OK_ALREADY_EXIST,
            RESULT_NG_ILLEGAL_FORMAT,
            RESULT_NG_OUT_OF_RANGE,
            ERROR
        }
        /**
         * 入力されたYYYYMMが有効な値かチェックする
         */
        fun checkFormat(aYYYYMM: String) : CheckFormatResultCode {

            if(aYYYYMM.length != 6) {
                // 6桁以外の場合は即終了
                Log.i("argument is not 6 digit")
                return CheckFormatResultCode.RESULT_NG_ILLEGAL_FORMAT
            }

            val yearMonthPair = try {
                splitYearMonth(aYYYYMM)
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

            // 既存データが存在するかチェック
            if(modelFacade.isExistSalaryInfo(year, month)) {
                Log.i("OK_ALREADY_EXIST")
                return CheckFormatResultCode.RESULT_OK_ALREADY_EXIST
            }

            Log.i("OK_NEW_ENTRY")
            return CheckFormatResultCode.RESULT_OK_NEW_ENTRY

        }

        @Throws(IllegalArgumentException::class)
        fun splitYearMonth(aYYYYMM: String): Pair<Int, Int>  {

            val yyyymm: Int = try {
                aYYYYMM.toInt()
            } catch (e: NumberFormatException) {
                // 入力値を数値に変換できなかった場合は終了
                Log.i("argument is not Integer", e)
                throw IllegalArgumentException("$aYYYYMM is not Int.")
            }

            // 100で割った余りを月として考える
            val month: Int = yyyymm % 100
            // 100で割った商を年として考える
            val year: Int = yyyymm / 100

            return Pair(year, month)
        }

    }

}