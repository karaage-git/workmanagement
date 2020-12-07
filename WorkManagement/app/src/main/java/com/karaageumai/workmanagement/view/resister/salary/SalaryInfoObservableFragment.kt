package com.karaageumai.workmanagement.view.resister.salary

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.karaageumai.workmanagement.model.salary.SalaryInfo

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

    abstract fun getSalaryInfo(): SalaryInfo
    abstract fun refreshSalaryInfo(aSalaryInfo: SalaryInfo)

}