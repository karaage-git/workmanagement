package com.karaageumai.workmanagement.view.input

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewResData

class InputInfoListAdapter(
    private val mContext: Context,
    private val mInputInfoParcelList: List<InputInfoParcel>,
) : BaseAdapter() {

    data class ViewHolder(
        val titleText: TextView,
        val valueText: TextView,
        val unitText: TextView
    )

    override fun getCount(): Int {
        return mInputInfoParcelList.size
    }

    override fun getItem(position: Int): Any {
        return mInputInfoParcelList[position].mTag
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val (viewHolder, view) = if (convertView == null) {
            val itemView = View.inflate(mContext, R.layout.layout_salary_row_item, null)
            val title: TextView = itemView.findViewById(R.id.tv_title)
            val value: TextView = itemView.findViewById(R.id.tv_input_value)
            val unit: TextView = itemView.findViewById(R.id.tv_sum_unit)
            val viewHolder = ViewHolder(title, value, unit)
            itemView.tag = viewHolder
            viewHolder to itemView
        } else {
            convertView.tag as ViewHolder to convertView
        }
        val parcel: InputInfoParcel = mInputInfoParcelList[position]
        InputViewResData(parcel.mTag).let {
            viewHolder.titleText.setText(it.mTitleResId)
            viewHolder.valueText.text = parcel.mStrValue
            viewHolder.unitText.setText(it.mUnitResId)
        }


        return view
    }
}