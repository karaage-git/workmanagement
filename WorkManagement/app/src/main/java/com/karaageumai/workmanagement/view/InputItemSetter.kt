package com.karaageumai.workmanagement.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.karaageumai.workmanagement.MainApplication

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