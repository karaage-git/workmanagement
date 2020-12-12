package com.karaageumai.workmanagement.view.resister.salary.ressetter

import java.io.Serializable

// データ入力用のレイアウトに埋め込むデータをまとめたクラス
interface BaseSalaryDataInputViewData : Serializable {
    /**
     * タイトルのリソースID取得
     *
     * @return タイトルのリソースID
     */
    fun getTitleResId(): Int

    /**
     * サブタイトルのリソースID取得
     *
     * @return サブタイトルのリソースID
     */
    fun getSubtitleResId(): Int

    /**
     * 入力ヒントのリソースID取得
     *
     * @return 入力ヒントのリソースID
     */
    fun getInputHintResId(): Int

    /**
     * 入力タイプを取得
     *
     * @return 入力タイプ
     */
    fun getInputType(): Int

    /**
     * 入力最大文字数を取得
     *
     * @return 入力最大文字数
     */
    fun getInputMaxLength(): Int

    /**
     * 単位のリソースIDを取得
     *
     * @return 単位のリソースID
     */
    //
    fun getUnitResId(): Int

    /**
     * エラーメッセージのリソースIDを取得
     *
     * @return エラーメッセージのリソースID
     */
    fun getErrorMessageResId(): Int

    /**
     * 合計値計算対象項目かを判定するフラグ
     *
     * @return true:対象, false:対象外
     */
    fun isCalcItem() :Boolean
}