package com.example.ed_system.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.Parameter

class ParameterAdapter(
    private val parameters: List<Parameter>,
    private val context: Context
) : RecyclerView.Adapter<ParameterAdapter.ParameterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_parameter_card, parent, false)
        return ParameterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParameterViewHolder, position: Int) {
        holder.bind(parameters[position])
    }

    override fun getItemCount(): Int = parameters.size

    inner class ParameterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val value: TextView = itemView.findViewById(R.id.value)
        private val status: TextView = itemView.findViewById(R.id.status)
        private val updated: TextView = itemView.findViewById(R.id.updated)

        fun bind(parameter: Parameter) {
            icon.setImageResource(parameter.iconRes)
            name.text = parameter.name
            value.text = parameter.value
            status.text = parameter.status
            updated.text = parameter.updatedTime

            // Optional: Color status depending on value
            when (parameter.status.uppercase()) {
                "NORMAL" -> status.setTextColor(context.getColor(R.color.green))
                "WARNING" -> status.setTextColor(context.getColor(R.color.yellow))
                "CRITICAL" -> status.setTextColor(context.getColor(R.color.red))
            }
        }
    }
}
