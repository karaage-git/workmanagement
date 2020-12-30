package com.karaageumai.workmanagement.view.input

import androidx.fragment.app.Fragment
import com.karaageumai.workmanagement.view.input.util.salary.SalaryInfoParcel
import com.karaageumai.workmanagement.view.input.viewdata.salary.SalaryInputViewTag

abstract class SalaryInfoObservableFragment : Fragment() {
    private var observers: ArrayList<SalaryInfoObserverInterface> = ArrayList()

    fun addObserver(aObserver: SalaryInfoObserverInterface) {
        observers.add(aObserver)
    }

    fun deleteObserver(aObserver: SalaryInfoObserverInterface) {
        observers.remove(aObserver)
    }

    fun deleteAllObserver() {
        observers.clear()
    }

    fun notifyObserver() {
        val iterator: Iterator<SalaryInfoObserverInterface> = observers.iterator()
        while (iterator.hasNext()) {
            val observer: SalaryInfoObserverInterface = iterator.next()
            observer.update(this)
        }
    }

    abstract fun getSalaryInfoParcelList(): MutableList<SalaryInfoParcel>
    abstract fun getNotEnteredInputItemList(): MutableList<SalaryInputViewTag>
}