package com.karaageumai.workmanagement.presenter.bonus

import com.karaageumai.workmanagement.presenter.IBasePresenter

interface IBonusPresenter : IBasePresenter {
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