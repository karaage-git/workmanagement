package com.karaageumai.workmanagement.util

import com.karaageumai.workmanagement.Log
import java.lang.NumberFormatException

class CalendarUtil {

    companion object {

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
                // 6桁未満の場合は即終了
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT
            }

            val yyyymm: Int = try {
                aYYYYMM.toInt()
            } catch (e: NumberFormatException) {
                // 入力値を数値に変換できなかった場合は終了
                Log.i("checkFormat()", e)
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT
            }

            // 100で割った余りを月として考える
            val month: Int = yyyymm % 100
            // 100で割った商を年として考える
            val year: Int = yyyymm / 100

            if((month < 1) || (month > 12)) {
                // 月が1未満 または 12より大きい場合はNG
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_ILLEGAL_FORMAT
            }

            if((year < 2000) || (year > 2050)) {
                // 2000〜2050年までを有効とみなす
                return CHECK_FORMAT_RESULT_CODE.RESULT_NG_OUT_OF_RANGE
            }

            // Todo: ここでDBへアクセスてデータ存在するかチェックする判定を加える
            Log.i("OK")
            return CHECK_FORMAT_RESULT_CODE.RESULT_OK_NEW_ENTRY

        }

    }

}