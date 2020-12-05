package com.karaageumai.workmanagement.util

import java.lang.NumberFormatException

class NumberFormatUtil {

    companion object {

        /**
         * 文字列が0.1単位の数値として解釈できるかチェックする
         *
         * @param aSting 検査対象文字列
         * @return a
         */
        fun checkNumberFormat01(aSting: String): Boolean {
            // まず、数値にキャストできるか確かめる
            try {
                aSting.toDouble()
            } catch (e: NumberFormatException) {
                return false
            }

            // 最初の小数点の位置を調べる
            return when (aSting.indexOf(".")) {
                // 整数の場合
                -1 -> true

                // 一番最後が小数点の場合は、整数とみなす
                (aSting.length - 1) -> true

                // 小数点以下が1桁しか無い場合
                (aSting.length - 2) -> true

                // それ以外はfalse
                else -> false
            }
        }

        /**
         * 文字列が0.5単位の数値として解釈できるかチェックする
         *
         * @param aSting 検査対象文字列
         * @return a
         */
        fun checkNumberFormat05(aSting: String): Boolean {
            // まず、数値にキャストできるか確かめる
            try {
                aSting.toDouble()
            } catch (e: NumberFormatException) {
                return false
            }

            // 最初の小数点の位置を調べる
            return when (aSting.indexOf(".")) {
                // 整数の場合
                -1 -> true

                // 一番最後が小数点の場合は、整数とみなす
                (aSting.lastIndex) -> true

                // 小数点以下が1桁が0 or 5の場合はOK
                (aSting.lastIndex - 1) -> {
                    when (aSting.last()) {
                        '0' -> true
                        '5' -> true
                        else -> false
                    }
                }

                // それ以外はfalse
                else -> false
            }
        }


    }

}