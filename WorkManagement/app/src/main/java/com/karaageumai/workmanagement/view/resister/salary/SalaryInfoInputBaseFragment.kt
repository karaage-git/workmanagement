package com.karaageumai.workmanagement.view.resister.salary

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.salary.SalaryInfo
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.view.resister.InputItemSetter
import com.karaageumai.workmanagement.view.resister.salary.ressetter.BaseSalaryDataInputViewData
import com.karaageumai.workmanagement.view.resister.salary.ressetter.SalaryInputViewTag
import java.lang.NumberFormatException

private const val KEY_SALARY_INFO = "KEY_SALARY_INFO"
private const val KEY_IS_NEW_ENTRY = "KEY_IS_NEW_ENTRY"
private const val KEY_TAG_LIST = "KEY_TAG_LIST"
private const val KEY_SUM_VIEW_BACKGROUND_LAYOUT_RES_ID = "KEY_SUM_VIEW_BACKGROUND_LAYOUT_RES_ID"
private const val KEY_SUM_TITLE_STRING_RES_ID = "KEY_SUM_TITLE_STRING_RES_ID"
private const val KEY_SUM_UNIT_STRING_RES_ID = "KEY_SUM_UNIT_STRING_RES_ID"

private const val MAX_DAYS_PER_MONTH = 31.0
private const val MAX_TIME_PER_MONTH = 24.0 * 31.0
private const val INPUT_MAX_VALUE = 1000000000

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
    private var mInputViewTagList: MutableList<SalaryInputViewTag.Tag> = mutableListOf()
    // 入力項目のViewを管理するマップ
    private var mViewMap: MutableMap<SalaryInputViewTag.Tag, View> = mutableMapOf()
    // 合計値を計算の対象となる項目を管理するマップ
    private var mSumMap: MutableMap<SalaryInputViewTag.Tag, Any> = mutableMapOf()
    // 合計を表示するViewの背景レイアウトID
    private var mSumViewBackgroundResId = 0
    // 合計を表示するViewのタイトルのリソースID
    private var mSumViewTitleResId = 0
    // 合計を表示するViewの単位のリソースID
    private var mSumViewUnitResId = 0
    // 合計値を表示するView
    private lateinit var mSumViewValue: TextView
    // 合計値がIntかDoubleを判定するフラグ
    private var isSumInt: Boolean = true

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
                        aTagList: Array<SalaryInputViewTag.Tag>,
                        aSumViewBackgroundResId: Int,
                        aSumTitleStringResId: Int,
                        aSumUnitStringResId: Int
        ) = SalaryInfoInputBaseFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_SALARY_INFO, aSalaryInfo)
                putBoolean(KEY_IS_NEW_ENTRY, aIsNewEntry)
                putSerializable(KEY_TAG_LIST, aTagList)
                putInt(KEY_SUM_VIEW_BACKGROUND_LAYOUT_RES_ID, aSumViewBackgroundResId)
                putInt(KEY_SUM_TITLE_STRING_RES_ID, aSumTitleStringResId)
                putInt(KEY_SUM_UNIT_STRING_RES_ID, aSumUnitStringResId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            mSalaryInfo = bundle.getSerializable(KEY_SALARY_INFO) as SalaryInfo
            mIsNewEntry = bundle.getBoolean(KEY_IS_NEW_ENTRY)
            // 一応、型チェックをやっておく
            bundle.getSerializable(KEY_TAG_LIST).let {
                if (it is Array<*>){
                    for (element in it) {
                        if(element is SalaryInputViewTag.Tag){
                            mInputViewTagList.add(element)
                        }
                    }
                }
            }
            mSumViewBackgroundResId = bundle.getInt(KEY_SUM_VIEW_BACKGROUND_LAYOUT_RES_ID)
            mSumViewTitleResId = bundle.getInt(KEY_SUM_TITLE_STRING_RES_ID)
            mSumViewUnitResId = bundle.getInt(KEY_SUM_UNIT_STRING_RES_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_salary_info_input_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 合計を表示するViewの作成
        val sumView: LinearLayout = view.findViewById(R.id.ll_sum)
        sumView.background = context?.let { ContextCompat.getDrawable(it, mSumViewBackgroundResId) }

        val sumViewTitle: TextView = view.findViewById(R.id.tv_sum_title)
        sumViewTitle.text = getString(mSumViewTitleResId)

        val sumViewUnit: TextView = view.findViewById(R.id.tv_sum_unit)
        sumViewUnit.text = getString(mSumViewUnitResId)

        mSumViewValue = view.findViewById(R.id.tv_sum_value)

        // スペースの表示、非表示を切り替えるフラグ
        var isFirstView = true
        // addViewする親View
        val parent: LinearLayout = view.findViewById(R.id.ll_parent)
        for(tag in mInputViewTagList) {
            val data: BaseSalaryDataInputViewData = SalaryInputViewTag.tagDataMap[tag] ?: continue
            if(!isFirstView) {
                // 先頭でない場合はスペースを追加
                val space = layoutInflater.inflate(R.layout.layout_input_margin, parent, false)
                parent.addView(space)
            }

            if(data.isCalcItem()) {
                if((InputType.TYPE_NUMBER_FLAG_DECIMAL and data.getInputType()) != 0) {
                    // InputType.TYPE_NUMBER_FLAG_DECIMALが立っている場合
                    isSumInt = false
                }
            }

            // 入力項目用のViewを取得
            val inputItemView = createInputItemView(layoutInflater, parent, tag, data)
            // マップに登録
            mViewMap[tag] = inputItemView
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

    private fun updateSum() {

        if(isSumInt) {
            var sum = 0
            for (target in mSumMap) {
                val value = target.value
                if (value is Int) {
                    sum += value
                }
            }
            mSumViewValue.text = sum.toString()
        } else {
            var sum = 0.0
            for (target in mSumMap) {
                val value = target.value
                if (value is Double) {
                    sum += value
                }
            }
            if(sum == 0.0){
                mSumViewValue.setText(R.string.zero)
            } else {
                mSumViewValue.text = sum.toString()
            }
        }
    }

    // カスタムTextWatcher
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
            val tag = mEditText.tag
            if(tag is SalaryInputViewTag.Tag){
                when (tag) {
                    SalaryInputViewTag.Tag.WorkingDayInputViewData -> {
                        val value = s.let {
                            try {
                                val temp: Double = it.toString().toDouble()
                                if (NumberFormatUtil.checkNumberFormat05(s.toString())) {
                                    if (temp > MAX_DAYS_PER_MONTH) {
                                        // 1ヶ月の最大日数超えていたら無効
                                        showNGIcon()
                                        0.0
                                    } else {
                                        showOKIcon()
                                        temp
                                    }
                                } else {
                                    // 0.5単位でなければ無効
                                    showNGIcon()
                                    0.0
                                }
                            } catch (e: NumberFormatException) {
                                // 数値でなければ無効
                                showNGIcon()
                                0.0
                            }
                        }
                        mSalaryInfo.workingDay = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.WorkingTimeInputViewData -> {
                        val value = s.let {
                            try {
                                val temp: Double = it.toString().toDouble()
                                if (NumberFormatUtil.checkNumberFormat01(s.toString())) {
                                    if (temp > MAX_TIME_PER_MONTH) {
                                        // 1ヶ月の最大時間を超えていたら無効
                                        showNGIcon()
                                        0.0
                                    } else {
                                        showOKIcon()
                                        temp
                                    }
                                } else {
                                    // 0.1単位でなければ無効
                                    showNGIcon()
                                    0.0
                                }
                            } catch (e: NumberFormatException) {
                                // 数値でなければ無効
                                showNGIcon()
                                0.0
                            }
                        }
                        mSalaryInfo.workingTime = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.OverTimeInputViewData -> {
                        val value = s.let {
                            try {
                                val temp: Double = it.toString().toDouble()
                                if (NumberFormatUtil.checkNumberFormat01(s.toString())) {
                                    if (temp > MAX_TIME_PER_MONTH) {
                                        // 1ヶ月の最大時間を超えていたら無効
                                        showNGIcon()
                                        0.0
                                    } else {
                                        showOKIcon()
                                        temp
                                    }
                                } else {
                                    // 0.1単位でなければ無効
                                    showNGIcon()
                                    0.0
                                }
                            } catch (e: NumberFormatException) {
                                // 数値でなければ無効
                                showNGIcon()
                                0.0
                            }
                        }
                        mSalaryInfo.overtime = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.HealthInsuranceInputViewData -> {
                        val value = s.let {
                            try {
                                val temp: Int = it.toString().toInt()
                                if (NumberFormatUtil.checkNaturalNumberFormat(s.toString())) {
                                    if (temp > INPUT_MAX_VALUE) {
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
                        mSalaryInfo.healthInsuranceFee = value
                        addSumList(tag, value)
                    }
                }

                notifyObserver()
                updateSum()
            }
        }

        private fun showOKIcon() {
            showAndChangeIcon(mIcon, R.drawable.ic_baseline_check_circle_24, mErrorTextView, false)
        }

        private fun showNGIcon() {
            showAndChangeIcon(mIcon, R.drawable.ic_baseline_error_24, mErrorTextView, true)
        }

        private fun addSumList(aTag: SalaryInputViewTag.Tag, aValue: Int) {
            val data: BaseSalaryDataInputViewData = SalaryInputViewTag.tagDataMap[aTag] ?: return
            if(data.isCalcItem()) {
                mSumMap[aTag] = aValue
            }
        }

        private fun addSumList(aTag: SalaryInputViewTag.Tag, aValue: Double) {
            val data: BaseSalaryDataInputViewData = SalaryInputViewTag.tagDataMap[aTag] ?: return
            if(data.isCalcItem()) {
                mSumMap[aTag] = aValue
            }
        }
    }

}