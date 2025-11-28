package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ed_system.R
import com.example.ed_system.model.Alert

class AlertAdapter : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    private val alerts = mutableListOf<Alert>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert_card, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(alerts[position])
    }

    override fun getItemCount(): Int = alerts.size

    fun submitList(newAlerts: List<Alert>) {
        alerts.clear()
        alerts.addAll(newAlerts)
        notifyDataSetChanged()
    }

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.alertIcon)
        private val title: TextView = itemView.findViewById(R.id.alertTitle)
        private val time: TextView = itemView.findViewById(R.id.alertTime)
        private val description: TextView = itemView.findViewById(R.id.alertDescription)
        private val value: TextView = itemView.findViewById(R.id.alertValue)
        private val threshold: TextView = itemView.findViewById(R.id.alertThreshold)
        private val status: TextView = itemView.findViewById(R.id.alertStatus)

        fun bind(alert: Alert) {
            icon.setImageResource(alert.iconRes)
            title.text = alert.title
            description.text = alert.description
            value.text = alert.value
            threshold.text = alert.threshold
            time.text = alert.timeAgo
            status.text = alert.status

            val color = when (alert.status.uppercase()) {
                "DANGER" -> R.color.red
                "WARNING" -> R.color.orange
                else -> R.color.gray
            }

            status.setTextColor(ContextCompat.getColor(itemView.context, color))
            value.setTextColor(ContextCompat.getColor(itemView.context, color))
        }
    }
}
