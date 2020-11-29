package com.karaageumai.workmanagement.util

import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import kotlinx.coroutines.runBlocking
import java.lang.NumberFormatException

class CalendarUtil {

    companion object {

        private val modelFacade: ModelFacade = ModelFacade

        enum class CHECK_FORMAT_RESULT_CODE {
            RESULT_OK_NEW_ENTRY,
            RESULT_OK_ALREADY_EXIST,
            RESULT_NG_ILLEGAL_FORMAT,
            RESULT_NG_OUT_OF_RANGE
        }
        /**
         * 入力されたYYYYMMが有効な値かチェックする
         */
        fun checkFormat(aYYYYMM: String) : CHECK_FORMAT_RESULT_CODE {

            if(aYYYYMM.length != 6) {
                // 6桁以外の場合は即終了
                Log.i("argument is not 6 digit")
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT
            }

            val yyyymm: Int = try {
                aYYYYMM.toInt()
            } catch (e: NumberFormatException) {
                // 入力値を数値に変換できなかった場合は終了
                Log.i("argument is not Integer", e)
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT
            }

            // 100で割った余りを月として考える
            val month: Int = yyyymm % 100
            // 100で割った商を年として考える
            val year: Int = yyyymm / 100

            if((month < 1) || (month > 12)) {
                // 月が1未満 または 12より大きい場合はNG
                Log.i("Format of Month is wrong")
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT
            }

            if((year < 2000) || (year > 2050)) {
                // 2000〜2050年までを有効とみなす
                Log.i("Out of range")
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_OUT_OF_RANGE
            }

            // コルーチンでDBアクセス
            val list: List<SalaryInfo> = runBlocking {
                modelFacade.getSalaryDataWith(year, month)
            }

            // 検索結果が1件以上だった場合は既存データが存在するとみなす
            if(list.isNotEmpty()) {
                Log.i("OK_ALREADY_EXIST")
                return CHECK_FORMAT_RESULT_CODE.RESULT_OK_ALREADY_EXIST
            }

            Log.i("OK_NEW_ENTRY")
            return CHECK_FORMAT_RESULT_CODE.RESULT_OK_NEW_ENTRY

        }

    }

}