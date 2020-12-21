package com.karaageumai.workmanagement.view.resister.salary

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.util.NumberFormatUtil
import com.karaageumai.workmanagement.view.resister.InputItemSetter
import com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview.SalaryInputViewTag
import java.lang.NumberFormatException

private const val KEY_SALARY_INFO_PARCEL_LIST = "KEY_SALARY_INFO_PARCEL_LIST"
private const val KEY_IS_NEW_ENTRY = "KEY_IS_NEW_ENTRY"
private const val KEY_BACKGROUND_LAYOUT_RES_ID = "KEY_BACKGROUND_LAYOUT_RES_ID"

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
    private var mSalaryInfoParcelList: MutableList<SalaryInfoParcel> = mutableListOf()
    // データ更新か否かを示すフラグ
    private var mIsNewEntry: Boolean = true
    // 入力項目のViewを管理するマップ
    private var mViewMap: MutableMap<SalaryInputViewTag.Tag, View> = mutableMapOf()
    // 背景色ID
    private var mBackgroundResId = 0

    private var mAlertDialog: AlertDialog? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param aSalaryInfo
         * @return A new instance of fragment DeductionInputFragment.
         */
        @JvmStatic
        fun newInstance(aSalaryInfoParcelList: ArrayList<SalaryInfoParcel>,
                        aIsNewEntry: Boolean,
                        aBackgroundResId: Int
        ) = SalaryInfoInputBaseFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(KEY_SALARY_INFO_PARCEL_LIST, aSalaryInfoParcelList)
                putBoolean(KEY_IS_NEW_ENTRY, aIsNewEntry)
                putInt(KEY_BACKGROUND_LAYOUT_RES_ID, aBackgroundResId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getParcelableArrayList<SalaryInfoParcel>(KEY_SALARY_INFO_PARCEL_LIST)?.let {
                mSalaryInfoParcelList = it
            }
            mIsNewEntry = bundle.getBoolean(KEY_IS_NEW_ENTRY)
            mBackgroundResId = bundle.getInt(KEY_BACKGROUND_LAYOUT_RES_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_salary_info_input_base, container, false)

        val rootView: LinearLayout = view.findViewById(R.id.ll_root)
        rootView.setBackgroundResource(mBackgroundResId)

        // Todo 新規ではない場合に、ここでEditTextにデータをセットする

        val listView: ListView = view.findViewById(R.id.lv_item)
        val activity = activity
        if(activity != null) {
            // リストは参照渡しなので、要注意。（SalaryInfoListAdapter内でのリスト操作がここに影響します。）
            val adapter = SalaryInfoListAdapter(activity, mSalaryInfoParcelList)
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                Log.i("item is clicked")
                val tag = adapter.getItem(position)
                Log.i(tag.toString())
                if(tag is SalaryInputViewTag.Tag){
                    createInputItemView(tag)?.let {
                        val targetParcel: SalaryInfoParcel? = mSalaryInfoParcelList.let{ list ->
                            var ret: SalaryInfoParcel? = null
                            for(element in list){
                                if(element.mTag == tag) {
                                    ret = element
                                    break
                                }
                            }
                            ret
                        }

                        if(targetParcel != null){
                            val editText: EditText = it.findViewById(R.id.et_data)
                            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
                            val textWatcher = SalaryInfoTextWatcher(it)

                            if(targetParcel.mIsComplete){
                                editText.setText(targetParcel.mStrValue)
                            }

                            editText.addTextChangedListener(textWatcher)
                            mAlertDialog = alertDialog
                                .setView(it)
                                .setPositiveButton(R.string.ok) { dialog, _ ->
                                    // Todo 末尾が「.」の場合は、ここで除去する
                                    editText.removeTextChangedListener(textWatcher)
                                    val strValue = editText.text.toString()
                                    Log.i(strValue)

                                    dialog.dismiss()

                                    targetParcel.mStrValue = strValue
                                    targetParcel.mIsComplete = true

                                    notifyObserver()
                                }
                                .setNegativeButton(R.string.cancel) { dialog, _ ->
                                    editText.removeTextChangedListener(textWatcher)
                                    dialog.dismiss()
                                }
                                .setCancelable(false)
                                .show()
                        }

                    }
                }
            }
        }


        return view
    }

    override fun getSalaryInfoParcelList(): MutableList<SalaryInfoParcel> {
        return mSalaryInfoParcelList
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

    //private fun updateSalaryInfo()

    // カスタムTextWatcher
    inner class SalaryInfoTextWatcher(private val mView: View) : TextWatcher {

        private val mEditText: EditText = mView.findViewById(R.id.et_data)

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 何もしない
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 何もしない
        }

        override fun afterTextChanged(s: Editable?) {
            // 入力値のチェック及びダイアログのOKボタンの活性状態を制御
            mAlertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = checkAndShowIcon(mView)
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

    /**
     * リソースを入力レイアウトにセットする
     *
     * @param aTag SalaryInputViewTag.Tag
     * @return 作成されたView
     */
    private fun createInputItemView(aTag: SalaryInputViewTag.Tag): View? {
        // レイアウトファイルからViewを読み込む
        val inputView: View = View.inflate(context, R.layout.layout_input_data, null)

        val viewData = SalaryInputViewTag.tagDataMap[aTag]

        if(viewData != null){
            // タイトル
            val title: TextView = inputView.findViewById(R.id.tv_title)
            title.setText(viewData.getTitleResId())

            // サブタイトル
            val subtitle: TextView = inputView.findViewById(R.id.tv_subtitle)
            subtitle.setText(viewData.getSubtitleResId())

            // 入力欄
            val editText: EditText = inputView.findViewById(R.id.et_data)
            // 入力ヒント
            editText.setHint(viewData.getInputHintResId())
            // 入力形式
            editText.inputType = viewData.getInputType()
            // 最大入力文字数
            val inputFilter: Array<InputFilter> = arrayOf(InputFilter.LengthFilter(viewData.getInputMaxLength()))
            editText.filters = inputFilter
            // タグをセット
            editText.tag = aTag

            // 単位
            val unit: TextView = inputView.findViewById(R.id.tv_data_unit)
            unit.setText(viewData.getUnitResId())

            // アイコン（初期は非表示）
            val icon: ImageView = inputView.findViewById(R.id.iv_check_ic)
            icon.visibility = View.INVISIBLE

            // エラーメッセージ（初期は非表示、表示スペースも消す）
            val error: TextView = inputView.findViewById(R.id.tv_error)
            error.setText(viewData.getErrorMessageResId())
            error.visibility = View.INVISIBLE

            return inputView
        } else {
            return null
        }
    }
}