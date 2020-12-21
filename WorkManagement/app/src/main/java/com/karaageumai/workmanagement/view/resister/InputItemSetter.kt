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
import com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview.BaseSalaryDataInputViewData
import com.karaageumai.workmanagement.view.resister.salary.ressetter.inputview.SalaryInputViewTag

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



}