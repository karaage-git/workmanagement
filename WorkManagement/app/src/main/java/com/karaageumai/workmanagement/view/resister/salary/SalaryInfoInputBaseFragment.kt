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

        // Todo 新規ではない場合に、ここでEditTextにデータをセットする

        setTextWatcher()
    }

    override fun getSalaryInfo(): SalaryInfo {
        return mSalaryInfo
    }


    override fun refreshSalaryInfo(aSalaryInfo: SalaryInfo) {
        mSalaryInfo = aSalaryInfo
    }

    override fun getNotEnteredInputItemList(): MutableList<SalaryInputViewTag.Tag> {
        val retList: MutableList<SalaryInputViewTag.Tag> = mutableListOf()
        for (element in mViewMap) {
            if(!checkAndShowIcon(element.value)) {
                retList.add(element.key)
            }
        }
        return retList
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

    /**
     * 合計値を算出する
     * 各タブで扱う値がIntとDoubleで混在しているため、場合分けを行っている。
     * 現状は下記2パターンしか存在しない。
     * パターン1：合計値算出対象項目がすべてInt
     * パターン2：合計値算出対象項目がすべてDouble
     * （上記パターンは、EditTextのInputTypeで判定）
     *
     * ■現在の実装
     * 入力チェックを行ったタイミングで、MutableMap<SalaryInputViewTag.Tag, Any>に値をマッピングし、
     * 読み込む場合に前述のパターンに応じて型チェックを行い、合計値を算出している。
     * IntとDoubleが混在した場合は、Doubleとして処理が進む。
     */
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
        private val mView: View = aView
        private val mEditText: EditText = aView.findViewById(R.id.et_data)

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 何もしない
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 何もしない
        }

        override fun afterTextChanged(s: Editable?) {
            val tag = mEditText.tag
            val str: String = s.toString()
            if(tag is SalaryInputViewTag.Tag){
                when (tag) {
                    SalaryInputViewTag.Tag.WorkingDayInputViewData -> {
                        val value: Double = if(checkAndShowIcon(mView)){
                            str.toDouble()
                        } else {
                            0.0
                        }
                        mSalaryInfo.workingDay = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.WorkingTimeInputViewData -> {
                        val value: Double = if(checkAndShowIcon(mView)){
                            str.toDouble()
                        } else {
                            0.0
                        }
                        mSalaryInfo.workingTime = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.OverTimeInputViewData -> {
                        val value: Double = if(checkAndShowIcon(mView)){
                            str.toDouble()
                        } else {
                            0.0
                        }
                        mSalaryInfo.overtime = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.BaseIncomeInputViewData -> {
                        val value: Int = if(checkAndShowIcon(mView)){
                            str.toInt()
                        } else {
                            0
                        }
                        mSalaryInfo.salary = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.OverTimeIncomeInputViewData -> {
                        val value: Int = if(checkAndShowIcon(mView)){
                            str.toInt()
                        } else {
                            0
                        }
                        mSalaryInfo.overtimeSalary = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.OtherIncomeInputViewData -> {
                        val value: Int = if(checkAndShowIcon(mView)){
                            str.toInt()
                        } else {
                            0
                        }
                        mSalaryInfo.otherIncome = value
                        addSumList(tag, value)
                    }

                    SalaryInputViewTag.Tag.HealthInsuranceInputViewData -> {
                        val value: Int = if(checkAndShowIcon(mView)){
                            str.toInt()
                        } else {
                            0
                        }
                        mSalaryInfo.healthInsuranceFee = value
                        addSumList(tag, value)
                    }
                }

                notifyObserver()
                updateSum()
            }
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

    /**
     * 入力項目のチェックを行う
     *
     * @param aView チェック対象のEditTextが含まれるView
     * @return true:チェックOK ,false:チェックNG
     */
    private fun checkInputFormat(aView: View) :Boolean {
        val et: EditText = aView.findViewById(R.id.et_data)
        val value: String = et.text.toString()
        return checkInputFormat(aView, value)
    }

    /**
     * 入力チェック及びアイコン・エラーメッセージの切り替えを行う
     *
     * @param aView チェック対象のEditTextが含まれるView
     * @return true:チェックOK ,false:チェックNG
     */
    private fun checkAndShowIcon(aView: View) :Boolean {
        val et: EditText = aView.findViewById(R.id.et_data)
        val value: String = et.text.toString()
        val icon: ImageView = aView.findViewById(R.id.iv_check_ic)
        val error: TextView = aView.findViewById(R.id.tv_error)
        val ret: Boolean = checkInputFormat(aView, value)

        // アイコン、エラーメッセージの切り替えを行う
        if(ret) {
            showOKIcon(icon, error)
        } else {
            showNGIcon(icon, error)
        }

        return ret
    }

    /**
     * 入力項目のチェックを行う(入力値を指定して判定)
     *
     * @param aView チェック対象のEditTextが含まれるView
     * @param aValue チェック対象の値
     * @return true:チェックOK ,false:チェックNG
     */
    private fun checkInputFormat(aView: View, aValue: String): Boolean {
        val mEditText: EditText = aView.findViewById(R.id.et_data)

        val tag = mEditText.tag
        if(tag is SalaryInputViewTag.Tag) {
            when (tag) {
                // 0.5単位、最大値は1ヶ月、未入力不可
                SalaryInputViewTag.Tag.WorkingDayInputViewData -> {
                    try {
                        val temp: Double = aValue.toDouble()
                        return if (NumberFormatUtil.checkNumberFormat05(aValue)) {
                            temp <= MAX_DAYS_PER_MONTH
                        } else {
                            // 0.5単位でなければ無効
                            false
                        }
                    } catch (e: NumberFormatException) {
                        // 数値でなければ無効
                        return false
                    }
                }

                // 0.1単位、最大値は1ヶ月の時間、未入力不可
                SalaryInputViewTag.Tag.WorkingTimeInputViewData,
                SalaryInputViewTag.Tag.OverTimeInputViewData -> {
                    try {
                        val temp: Double = aValue.toDouble()
                        return if (NumberFormatUtil.checkNumberFormat01(aValue)) {
                            temp <= MAX_TIME_PER_MONTH
                        } else {
                            // 0.1単位でなければ無効
                            false
                        }
                    } catch (e: NumberFormatException) {
                        // 数値でなければ無効
                        return false
                    }
                }

                // 整数、最大値あり、未入力不可
                SalaryInputViewTag.Tag.BaseIncomeInputViewData -> {
                    try {
                        val temp: Int = aValue.toInt()
                        return if (NumberFormatUtil.checkNaturalNumberFormat(aValue)) {
                            temp <= INPUT_MAX_VALUE
                        } else {
                            // 整数でなければ無効
                            false
                        }
                    } catch (e: NumberFormatException) {
                        // 数値でなければ無効
                        return false
                    }
                }

                // 整数、最大値あり、未入力可
                SalaryInputViewTag.Tag.OverTimeIncomeInputViewData,
                SalaryInputViewTag.Tag.OtherIncomeInputViewData,
                SalaryInputViewTag.Tag.HealthInsuranceInputViewData -> {
                    // 未入力も許可
                    if (aValue.isEmpty()) {
                        return true
                    } else {
                        return try {
                            val temp: Int = aValue.toInt()
                            if (NumberFormatUtil.checkNaturalNumberFormat(aValue)) {
                                temp <= INPUT_MAX_VALUE
                            } else {
                                // 整数でなければ無効
                                false
                            }
                        } catch (e: NumberFormatException) {
                            // 数値でなければ無効
                            false
                        }
                    }
                }
            }
        }
        return false
    }

    /**
     * OKアイコン表示
     */
    private fun showOKIcon(aIcon: ImageView, aTextView: TextView) {
        showAndChangeIcon(aIcon, R.drawable.ic_baseline_check_circle_24, aTextView, false)
    }

    /**
     * NGアイコン表示
     */
    private fun showNGIcon(aIcon: ImageView, aTextView: TextView) {
        showAndChangeIcon(aIcon, R.drawable.ic_baseline_error_24, aTextView, true)
    }
}