package com.karaageumai.workmanagement.util

import java.lang.NumberFormatException

object NumberFormatUtil {
    /**
     * 文字列が整数として解釈できるかチェックする
     *
     * @param aSting 検査対象文字列
     * @return true:整数 , false:整数以外
     */
    fun checkNaturalNumberFormat(aSting: String): Boolean {
        // まず、数値にキャストできるか確かめる
        try {
            aSting.toInt()
        } catch (e: NumberFormatException) {
            return false
        }

        // 最初の小数点の位置を調べる
        return when (aSting.indexOf(".")) {
            // 整数の場合
            -1 -> true

            // 一番最後が小数点の場合は、整数とみなす
            (aSting.length - 1) -> true

            // それ以外はfalse
            else -> false
        }
    }

    /**
     * 文字列が0.1単位の数値として解釈できるかチェックする
     *
     * @param aSting 検査対象文字列
     * @return true:0.1単位 , false:0.1単位以外
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
            (aSting.lastIndex) -> true

            // 小数点以下が1桁しか無い場合
            (aSting.lastIndex - 1) -> true

            // それ以外はfalse
            else -> false
        }
    }

    /**
     * 文字列が0.5単位の数値として解釈できるかチェックする
     *
     * @param aSting 検査対象文字列
     * @return true:0.5単位 , false:0.5単位以外
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

    /**
     * 文字列の末尾が小数点だった場合に取り除くメソッド
     *
     * @param aSting 編集対象の文字列（数値の想定）
     * @return 末尾の小数点が取り除かれた文字列
     */
    fun trimLastDot(aSting: String): String {
        if (aSting.last() == '.') {
            return aSting.replace(".", "")
        }
        return aSting
    }
}