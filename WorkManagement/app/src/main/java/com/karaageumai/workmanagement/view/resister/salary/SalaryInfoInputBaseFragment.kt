package com.karaageumai.workmanagement.view.resister.salary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.view.resister.InputItemSetter
import com.karaageumai.workmanagement.view.resister.salary.ressetter.BaseSalaryDataInputViewData
import com.karaageumai.workmanagement.view.resister.salary.ressetter.SalaryInputViewTag
import java.lang.NumberFormatException

private const val KEY_SALARY_INFO = "KEY_SALARY_INFO"
private const val KEY_IS_NEW_ENTRY = "KEY_IS_NEW_ENTRY"
private const val KEY_DATA_LIST = "KEY_DATA_LIST"
private const val KEY_LAYOUT_RES_ID = "KEY_LAYOUT_RES_ID"

private const val MAX_INCOME = 1000000000

/**
 * A simple [Fragment] subclass.
 * Use the [SalaryInfoInputBaseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalaryInfoInputBaseFragment : SalaryInfoObservableFragment(), InputItemSetter {
    // 給与情報
    private lateinit var mSalaryInfo: SalaryInfo
    // データ更新か否かを示すフラグ
    private var mIsNewEntry: Boolean = true
    // 表示する入力項目を管理するリスト
    private var mInputViewList: MutableList<BaseSalaryDataInputViewData> = mutableListOf()
    // 入力項目のViewを管理するマップ
    private var mViewMap: MutableMap<SalaryInputViewTag, View> = mutableMapOf()
    // rootのViewになるレイアウトID
    private var mLayoutResId: Int = 0

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param aSalaryInfo
         * @return A new instance of fragment DeductionInputFragment.
         */
        @JvmStatic
        fun newInstance(aSalaryInfo: SalaryInfo,
                        aIsNewEntry: Boolean,
                        aDataList: Array<BaseSalaryDataInputViewData>,
                        aLayoutResId: Int) =
                SalaryInfoInputBaseFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(KEY_SALARY_INFO, aSalaryInfo)
                        putBoolean(KEY_IS_NEW_ENTRY, aIsNewEntry)
                        putSerializable(KEY_DATA_LIST, aDataList)
                        putInt(KEY_LAYOUT_RES_ID, aLayoutResId)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            mSalaryInfo = bundle.getSerializable(KEY_SALARY_INFO) as SalaryInfo
            mIsNewEntry = bundle.getBoolean(KEY_IS_NEW_ENTRY)
            // 一応、型チェックをやっておく
            bundle.getSerializable(KEY_DATA_LIST).let {
                if (it is Array<*>){
                    for (item in it) {
                        if(item is BaseSalaryDataInputViewData){
                            mInputViewList.add(item)
                        }
                    }
                }
            }
            mLayoutResId = bundle.getInt(KEY_LAYOUT_RES_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(mLayoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // スペースの表示、非表示を切り替えるフラグ
        var isFirstView = true
        // addViewする親View
        val parent: LinearLayout = view.findViewById(R.id.ll_root)
        for(target in mInputViewList) {
            if(!isFirstView) {
                // 先頭でない場合はスペースを追加
                val space = layoutInflater.inflate(R.layout.layout_input_margin, parent, false)
                parent.addView(space)
            }

            // 入力項目用のViewを取得
            val inputItemView = createInputItemView(layoutInflater, parent, target)
            // マップに登録
            mViewMap[target.getTag()] = inputItemView
            // 表示
            parent.addView(inputItemView)
            // 初回フラグを更新
            isFirstView = false
        }
        setTextWatcher()
    }

    override fun getSalaryInfo(): SalaryInfo {
        return mSalaryInfo
    }


    override fun refreshSalaryInfo(aSalaryInfo: SalaryInfo) {
        mSalaryInfo = aSalaryInfo
    }

    /**
     * カスタムTextWatcherをセットする
     */
    private fun setTextWatcher() {
        for (element in mViewMap){
            val view: View = element.value
            val et: EditText = view.findViewById(R.id.et_data)
            et.addTextChangedListener(SalaryInfoTextWatcher(view))
        }
    }

    inner class SalaryInfoTextWatcher(aView: View) : TextWatcher {
        private val mEditText: EditText = aView.findViewById(R.id.et_data)
        private val mIcon: ImageView = aView.findViewById(R.id.iv_check_ic)
        private val mErrorTextView: TextView = aView.findViewById(R.id.tv_error)

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 何もしない
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 何もしない
        }

        override fun afterTextChanged(s: Editable?) {
            when (mEditText.tag) {
                SalaryInputViewTag.BaseSalaryDataInputViewData -> {

                    val value = s.let {
                        try {
                            val temp: Int = it.toString().toInt()
                            if (NumberFormatUtil.checkNaturalNumberFormat(s.toString())) {
                                if (temp > MAX_INCOME) {
                                    // 大きな値が入ってきたら無効
                                    showNGIcon()
                                    0
                                } else {
                                    showOKIcon()
                                    temp
                                }
                            } else {
                                // 整数でなければ無効
                                showNGIcon()
                                0
                            }
                        } catch (e: NumberFormatException) {
                            // 数値でなければ無効
                            showNGIcon()
                            0
                        }
                    }
                    mSalaryInfo.salary = value
                }
            }
            notifyObserver()
        }

        private fun showOKIcon() {
            showAndChangeIcon(mIcon, R.drawable.ic_baseline_check_circle_24, mErrorTextView, false)
        }

        private fun showNGIcon() {
            showAndChangeIcon(mIcon, R.drawable.ic_baseline_error_24, mErrorTextView, true)
        }
    }

}