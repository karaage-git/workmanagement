package com.karaageumai.workmanagement.view.input

import androidx.annotation.UiThread

interface IBaseInputView {
    /**
     * ユーザが1つのアイテムについて入力が完了した際に呼ばれる
     *
     * @param aIsSuccess 入力結果
     */
    @UiThread
    fun onInputItem(aIsSuccess: Boolean)

    /**
     * DBにデータがInsertされた際に呼ばれる
     */
    @UiThread
    fun onInsertData()

    /**
     * DBのデータがUpdateされた際に呼ばれる
     */
    @UiThread
    fun onUpdateData()

    /**
     * DBのデータがDeleteされた際に呼ばれる
     */
    @UiThread
    fun onDeleteData()

    /**
     * Presenterから対象の年を取得するためのメソッド
     *
     * @return 年
     */
    fun getYear(): Int

    /**
     * Presenterから対象の月を取得するためのメソッド
     *
     * @return 月
     */
    fun getMonth(): Int
}