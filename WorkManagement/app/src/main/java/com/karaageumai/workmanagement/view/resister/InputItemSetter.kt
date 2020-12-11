package com.karaageumai.workmanagement.view.resister

import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.karaageumai.workmanagement.MainApplication
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.view.resister.salary.ressetter.BaseSalaryDataInputViewData

interface InputItemSetter {

    // アイコンとエラーメッセージの表示切り替え
    fun showAndChangeIcon(
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

    /**
     * リソースを入力レイアウトにセットし、親Viewにセットする
     *
     * @param aLayoutInflater 呼び出し元のLayoutInflater
     * @param aParent 親View
     * @param aViewData BaseSalaryDataInputViewData
     * @return 作成されたView
     */
    fun createInputItemView(aLayoutInflater: LayoutInflater,
                            aParent: ViewGroup,
                            aViewData: BaseSalaryDataInputViewData
    ): View {
        // レイアウトファイルからViewを読み込む
        val inputView: View = aLayoutInflater.inflate(R.layout.layout_input_data, aParent, false)

        // タイトル
        val title: TextView = inputView.findViewById(R.id.tv_title)
        title.setText(aViewData.getTitleResId())

        // サブタイトル
        val subtitle: TextView = inputView.findViewById(R.id.tv_subtitle)
        subtitle.setText(aViewData.getSubtitleResId())

        // 入力欄
        val editText: EditText = inputView.findViewById(R.id.et_data)
        // 入力ヒント
        editText.setHint(aViewData.getInputHintResId())
        // 入力形式
        editText.inputType = aViewData.getInputType()
        // 最大入力文字数
        val inputFilter: Array<InputFilter> = arrayOf(InputFilter.LengthFilter(aViewData.getInputMaxLength()))
        editText.filters = inputFilter
        // タグをセット
        editText.tag = aViewData.getTag()

        // 単位
        val unit: TextView = inputView.findViewById(R.id.tv_data_unit)
        unit.setText(aViewData.getUnitResId())

        // アイコン（初期は非表示）
        val aicon: ImageView = inputView.findViewById(R.id.iv_check_ic)
        aicon.visibility = View.INVISIBLE

        // エラーメッセージ（初期は非表示）
        val error: TextView = inputView.findViewById(R.id.tv_error)
        error.setText(aViewData.getErrorMessageResId())
        error.visibility = View.INVISIBLE

        return inputView
    }

}