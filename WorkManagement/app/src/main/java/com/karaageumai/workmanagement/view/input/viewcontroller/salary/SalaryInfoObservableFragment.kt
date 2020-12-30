package com.karaageumai.workmanagement.view.input.viewcontroller.salary

import androidx.fragment.app.Fragment
import com.karaageumai.workmanagement.view.input.util.salary.SalaryInfoParcel
import com.karaageumai.workmanagement.view.input.viewdata.salary.SalaryInputViewTag

abstract class SalaryInfoObservableFragment : Fragment() {
    // オブザーバーを管理するリスト
    private var observers: ArrayList<SalaryInfoObserverInterface> = ArrayList()

    /**
     * オブザーバーを追加する
     *
     * @param aObserver 追加するオブザーバー
     */
    fun addObserver(aObserver: SalaryInfoObserverInterface) {
        observers.add(aObserver)
    }

    /**
     * オブザーバーを削除
     *
     * @param aObserver 削除するオブザーバー
     */
    fun deleteObserver(aObserver: SalaryInfoObserverInterface) {
        observers.remove(aObserver)
    }

    /**
     * 全オブザーバーを削除
     */
    fun deleteAllObserver() {
        observers.clear()
    }

    /**
     * 登録されたオブザーバーに変更を通知する
     */
    fun notifyObserver() {
        val iterator: Iterator<SalaryInfoObserverInterface> = observers.iterator()
        while (iterator.hasNext()) {
            val observer: SalaryInfoObserverInterface = iterator.next()
            observer.update(this)
        }
    }

    /**
     * オブザーバーが変更通知を受け、変更内容を取得するための抽象メソッド
     *
     * @return 変更内容
     */
    abstract fun getSalaryInfoParcelList(): List<SalaryInfoParcel>
}