package com.example.ed_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.HistoryAverage

class HistoryAdapter(private var historyList: List<HistoryAverage>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val parameterName: TextView = view.findViewById(R.id.parameterName)
        val parameterValue: TextView = view.findViewById(R.id.parameterValue)
        val parameterStatus: TextView = view.findViewById(R.id.parameterStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_card, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.parameterName.text = item.parameterName
        holder.parameterValue.text = item.value
        holder.parameterStatus.text = item.status
    }

    override fun getItemCount() = historyList.size

    fun updateData(newList: List<HistoryAverage>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
