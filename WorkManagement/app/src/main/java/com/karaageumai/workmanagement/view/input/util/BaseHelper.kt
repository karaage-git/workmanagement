package com.karaageumai.workmanagement.view.input.util

import com.karaageumai.workmanagement.exception.SalaryInfoTagNotFoundException
import com.karaageumai.workmanagement.view.input.viewdata.InputViewTag

abstract class BaseHelper {
    /**
     * タグを指定してInputInfoParcelを取得する
     *
     * @param aTag 取得対象のparcelのタグ
     * @return aTagに一致するタグをもつmSalaryInfoParcelListの要素
     * @throws SalaryInfoTagNotFoundException mSalaryInfoParcelListのどの要素とも一致しなかった場合
     */
    private fun getInputInfoParcel(aTag: InputViewTag): InputInfoParcel {
        for (parcel in getInputInfoParcelList()) {
            if(parcel.mTag == aTag){
                return parcel
            }
        }
        throw SalaryInfoTagNotFoundException("$aTag is UNKNOWN tag.")
    }

    /**
     * InputViewTagのリストを使用して、子クラスのメンバで保持しているInputInfoParcelを返す
     * 登録されていないタグが引数にあった場合は、リターンするリストにaddしない。
     *
     * @param aTagList
     * @return
     */
    fun getInputInfoParcelList(aTagList: List<InputViewTag>): List<InputInfoParcel> {
        val retList: MutableList<InputInfoParcel> = mutableListOf()
        for(tag in aTagList) {
            try {
                retList.add(getInputInfoParcel(tag))
            } catch (e: SalaryInfoTagNotFoundException) {
                continue
            }
        }
        return retList
    }

    /**
     * ユーザの入力が完了しているかチェックする
     */
    fun checkUserInputFinished(): Boolean {
        var ret = true
        for(parcel in getInputInfoParcelList()) {
            ret = ret and parcel.mIsComplete
        }
        return ret
    }

    /**
     * 子クラスで保持するInputInfoParcelのリストを取得する
     *
     * @return 子クラスで保持するInputInfoParcelのリスト
     */
    abstract fun getInputInfoParcelList(): List<InputInfoParcel>
}