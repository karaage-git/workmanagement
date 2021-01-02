package com.karaageumai.workmanagement.view.input.viewcontroller

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.model.ModelFacade
import com.karaageumai.workmanagement.view.INPUT_MODE_BONUS
import com.karaageumai.workmanagement.view.INPUT_MODE_SALARY

class YearMonthSpinnerAdapter(
    private val mContext: Context,
    private val mYearMonthList: List<Pair<Int, Int>>,
    private val mDBDataYearMonthList: List<Pair<Int, Int>>,
    private val mMode: Int
) : BaseAdapter() {
    data class ViewHolder(
        val icon: ImageView,
        val itemText: TextView
    )

    override fun getCount(): Int {
        return mYearMonthList.size
    }

    override fun getItem(position: Int): Any {
        return mYearMonthList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val (viewHolder, view) = if (convertView == null) {
            val itemView = View.inflate(mContext, R.layout.layout_spinner_row_item, null)
            val icon: ImageView = itemView.findViewById(R.id.iv_check_ic)
            val value: TextView = itemView.findViewById(R.id.tv_spinner_item)
            val viewHolder = ViewHolder(icon, value)
            itemView.tag = viewHolder
            viewHolder to itemView
        } else {
            convertView.tag as ViewHolder to convertView
        }

        val targetYearMonth = mYearMonthList[position]
        viewHolder.itemText.text = when (mMode){
            INPUT_MODE_SALARY -> {
                 mContext.getString(R.string.spinner_salary, targetYearMonth.first, targetYearMonth.second)
            }

            INPUT_MODE_BONUS -> {
                mContext.getString(R.string.spinner_bonus, targetYearMonth.first, targetYearMonth.second)
            }

            else -> ""
        }

        viewHolder.icon.visibility = View.INVISIBLE
        for(dataYearMonth in mDBDataYearMonthList) {
            if(targetYearMonth == dataYearMonth) {
                viewHolder.icon.visibility = View.VISIBLE
            }
        }

        return view
    }
}