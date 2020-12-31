package com.karaageumai.workmanagement.view.input.viewcontroller

import androidx.fragment.app.Fragment
import com.karaageumai.workmanagement.view.input.util.InputInfoParcel

abstract class InputInfoObservableFragment : Fragment() {
    // オブザーバーを管理するリスト
    private var mObservers: ArrayList<InputInfoObserverInterface> = ArrayList()

    /**
     * オブザーバーを追加する
     *
     * @param aObserver 追加するオブザーバー
     */
    fun addObserver(aObserver: InputInfoObserverInterface) {
        mObservers.add(aObserver)
    }

    /**
     * オブザーバーを削除
     *
     * @param aObserver 削除するオブザーバー
     */
    fun deleteObserver(aObserver: InputInfoObserverInterface) {
        mObservers.remove(aObserver)
    }

    /**
     * 全オブザーバーを削除
     */
    fun deleteAllObserver() {
        mObservers.clear()
    }

    /**
     * 登録されたオブザーバーに変更を通知する
     */
    fun notifyObserver() {
        val iterator: Iterator<InputInfoObserverInterface> = mObservers.iterator()
        while (iterator.hasNext()) {
            val observer: InputInfoObserverInterface = iterator.next()
            observer.update(this)
        }
    }

    /**
     * オブザーバーが変更通知を受け、変更内容を取得するための抽象メソッド
     *
     * @return 変更内容
     */
    abstract fun getInputInfoParcelList(): List<InputInfoParcel>
}