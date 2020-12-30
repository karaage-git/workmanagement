package com.karaageumai.workmanagement.view.input.viewcontroller.salary

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.view.input.util.salary.SalaryInfoParcel
import com.karaageumai.workmanagement.view.input.viewdata.salary.SalaryInputViewResData

class SalaryInfoListAdapter(
        private val mContext: Context,
        private val mSalaryInfoParcelList: List<SalaryInfoParcel>,
) : BaseAdapter() {

    data class ViewHolder(
        val titleText: TextView,
        val valueText: TextView,
        val unitText: TextView
    )

    override fun getCount(): Int {
        return mSalaryInfoParcelList.size
    }

    override fun getItem(position: Int): Any {
        return mSalaryInfoParcelList[position].mTag
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
        val parcel: SalaryInfoParcel = mSalaryInfoParcelList[position]
        SalaryInputViewResData(parcel.mTag).let {
            viewHolder.titleText.setText(it.mTitleResId)
            viewHolder.valueText.text = parcel.mStrValue
            viewHolder.unitText.setText(it.mUnitResId)
        }


        return view
    }
}