package com.karaageumai.workmanagement.util

import android.icu.text.NumberFormat
import kotlin.NumberFormatException

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
        // 文字列が空の場合は何もしないでリターン
        if (aSting.isNotBlank()) {
            if (aSting.last() == '.') {
                return aSting.replace(".", "")
            }
        }
        return aSting
    }

    /**
     * 文字列を金額用に桁区切りする
     *
     * @param aSting 編集対象の文字列（数値を想定）
     * @return 桁区切りされた文字列（引数の文字列を数値に変換できない場合は引数をそのまま返す）
     */
    fun separateThousand(aSting: String): String {
        val formatter = NumberFormat.getInstance()
        formatter.isGroupingUsed = true
        return try {
            // Intに変換できる場合
            formatter.format(aSting.toInt())
        } catch (e: NumberFormatException) {
            try {
                // Doubleに変換できる場合
                formatter.format(aSting.toDouble())
            } catch (e: NumberFormatException) {
                // Int、Doubleに変換できない場合は引数をそのまま返す
                aSting
            }
        }
    }
}