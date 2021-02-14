package com.karaageumai.workmanagement.view.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karaageumai.workmanagement.Log
import com.karaageumai.workmanagement.R
import com.karaageumai.workmanagement.presenter.input.util.InputInfoParcel
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewResData
import com.karaageumai.workmanagement.presenter.input.viewdata.InputViewTag
import com.karaageumai.workmanagement.util.NumberFormatUtil

class InputInfoListAdapter(
    private val mInputInfoParcelList: List<InputInfoParcel>
) : RecyclerView.Adapter<InputItemViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.layout_salary_row_item, parent, false)
        return InputItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InputItemViewHolder, position: Int) {
        val parcel: InputInfoParcel = mInputInfoParcelList[position]
        Log.i("onBindViewHolder : $parcel")
        InputViewResData(parcel.mTag).let {
            holder.title.setText(it.mTitleResId)
            holder.value.text = NumberFormatUtil.separateThousand(parcel.mStrValue)
            holder.unit.setText(it.mUnitResId)
            holder.itemView.setOnClickListener { view ->
                mListener.onItemClickListener(view, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mInputInfoParcelList.size
    }

    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int)
    }

    fun setOnItemClickListener(aListener: OnItemClickListener) {
        Log.i("set listener")
        mListener = aListener
    }

    fun getItem(position: Int): InputViewTag {
        return mInputInfoParcelList[position].mTag
    }
}