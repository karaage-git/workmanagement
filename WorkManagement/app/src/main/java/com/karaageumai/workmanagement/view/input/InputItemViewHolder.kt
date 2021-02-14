package com.karaageumai.workmanagement.view.input

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karaageumai.workmanagement.R

class InputItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tv_title)
    val value: TextView = itemView.findViewById(R.id.tv_input_value)
    val unit: TextView = itemView.findViewById(R.id.tv_sum_unit)
}
