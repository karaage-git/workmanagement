package com.karaageumai.workmanagement.view.input

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.presenter.input.IBaseInputPresenter
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewResData
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.util.NumberFormatUtil

private const val KEY_SALARY_INFO_PARCEL_ARRAY = "KEY_SALARY_INFO_PARCEL_ARRAY"
private const val KEY_BACKGROUND_LAYOUT_RES_ID = "KEY_BACKGROUND_LAYOUT_RES_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [BaseInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BaseInputFragment : Fragment() {
    // 給与情報
    private var mInputInfoParcelList: MutableList<InputInfoParcel> = mutableListOf()
    // 背景色ID
    private var mBackgroundResId = 0
    // 入力ダイアログ
    private var mAlertDialog: AlertDialog? = null
    // Presenter
    private var mInputPresenter: IBaseInputPresenter? = MainApplication.getPresenter()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param aInputInfoParcelArray 表示する入力項目
         * @param aBackgroundResId 背景色
         * @return A new instance of fragment DeductionInputFragment.
         */
        @JvmStatic
        fun newInstance(aInputInfoParcelArray: Array<InputInfoParcel>,
                        aBackgroundResId: Int
        ) = BaseInputFragment().apply {
            arguments = Bundle().apply {
                putParcelableArray(KEY_SALARY_INFO_PARCEL_ARRAY, aInputInfoParcelArray)
                putInt(KEY_BACKGROUND_LAYOUT_RES_ID, aBackgroundResId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getParcelableArray(KEY_SALARY_INFO_PARCEL_ARRAY)?.let {
                // ダイレクトにキャストできないので、型チェックしてリストに追加する
                for(parcel in it){
                    if(parcel is InputInfoParcel){
                        mInputInfoParcelList.add(parcel)
                    }
                }
            }
            mBackgroundResId = bundle.getInt(KEY_BACKGROUND_LAYOUT_RES_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_salary_info_input, container, false)

        val rootView: LinearLayout = view.findViewById(R.id.ll_root)
        rootView.setBackgroundResource(mBackgroundResId)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_item)
        val activity = activity
        activity?.let {
            // Layout Managerをセット
            recyclerView.layoutManager = LinearLayoutManager(activity)
            // Adapter作成
            val adapter = InputInfoListAdapter(mInputInfoParcelList)
            adapter.setOnItemClickListener(object: InputInfoListAdapter.OnItemClickListener{
                override fun onItemClickListener(view: View, position: Int) {
                    Log.i("item is clicked. position : $position")

                    val tag = adapter.getItem(position)
                    Log.i(tag.toString())

                    createInputItemView(tag).let {
                        val targetParcel: InputInfoParcel? = mInputInfoParcelList.let{ list ->
                            var ret: InputInfoParcel? = null
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
                                        editText.removeTextChangedListener(textWatcher)
                                        val userInput = editText.text.toString()
                                        val strValue = NumberFormatUtil.trimLastDot(userInput)
                                        Log.i(strValue)

                                        dialog.dismiss()

                                        targetParcel.mStrValue = strValue
                                        targetParcel.mIsComplete = true

                                        // itemが変更されたことをAdapterに通知し、再描画する
                                        adapter.notifyItemChanged(position)

                                        mInputPresenter?.updateItem(targetParcel)
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
            })
            Log.i("adapter is created")

            // RecyclerViewにAdapterをセット
            recyclerView.adapter = adapter
        }
        return view
    }

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
        if(tag is InputViewTag) {
            return mInputPresenter?.checkInputData(tag, aValue) ?: false
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
    private fun createInputItemView(aTag: InputViewTag): View {
        // レイアウトファイルからViewを読み込む
        val inputView: View = View.inflate(context, R.layout.layout_input_data, null)

        val viewData = InputViewResData(aTag)

        // タイトル
        val title: TextView = inputView.findViewById(R.id.tv_title)
        title.setText(viewData.mTitleResId)

        // サブタイトル
        val subtitle: TextView = inputView.findViewById(R.id.tv_subtitle)
        subtitle.setText(viewData.mSubTitleResId)

        // 入力欄
        val editText: EditText = inputView.findViewById(R.id.et_data)
        // 入力ヒント
        editText.setHint(viewData.mInputHintResId)
        // 入力形式
        editText.inputType = viewData.mInputType
        // 最大入力文字数
        val inputFilter: Array<InputFilter> = arrayOf(InputFilter.LengthFilter(viewData.mInputMaxLength))
        editText.filters = inputFilter
        // タグをセット
        editText.tag = aTag

        // 単位
        val unit: TextView = inputView.findViewById(R.id.tv_data_unit)
        unit.setText(viewData.mUnitResId)

        // アイコン（初期は非表示）
        val icon: ImageView = inputView.findViewById(R.id.iv_check_ic)
        icon.visibility = View.INVISIBLE

        // エラーメッセージ（初期は非表示、表示スペースも消す）
        val error: TextView = inputView.findViewById(R.id.tv_error)
        error.setText(viewData.mErrorMessageResId)
        error.visibility = View.INVISIBLE

        return inputView

    }

    /**
     * アイコンとエラーメッセージの表示切り替え
     */
    private fun showAndChangeIcon(
            aImageView: ImageView,
            aResId: Int,
            aErrorMessageTextView: TextView,
            isShowMessage: Boolean) {
        val context: Context = MainApplication.getContext()
        aImageView.setImageDrawable(ContextCompat.getDrawable(context, aResId))
        aImageView.visibility = View.VISIBLE

        aErrorMessageTextView.visibility = if (isShowMessage) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}