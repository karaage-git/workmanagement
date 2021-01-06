package com.karaageumai.workmanagement.presenter.salary

import com.karaageumai.workmanagement.presenter.IBasePresenter

interface ISalaryPresenter : IBasePresenter {
    /**
     * 労働時間の合計値を算出する
     *
     * @return 労働時間の合計値
     */
    fun getSumWorkTime(): Double

    /**
     * 収入の合計値を算出する
     *
     * @return 収入の合計値
     */
    fun getSumIncome(): Int

    /**
     * 控除の合計額を算出する
     *
     * @return 控除の合計値
     */
    fun getSumDeduction(): Int

}