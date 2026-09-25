package com.example.notisaver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SenderAdapter(
    private val onClick: (SenderGroup) -> Unit
) : RecyclerView.Adapter<SenderAdapter.ViewHolder>() {

    private var items: List<SenderGroup> = emptyList()

    fun submitList(list: List<SenderGroup>) {
        items = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view.findViewById(R.id.rootRow)
        val avatarInitial: TextView = view.findViewById(R.id.avatarInitial)
        val senderName: TextView = view.findViewById(R.id.senderName)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val time: TextView = view.findViewById(R.id.time)
        val countBadge: TextView = view.findViewById(R.id.countBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sender, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val name = item.title.ifEmpty { "Unknown" }
        holder.senderName.text = name
        holder.avatarInitial.text = name.trim().take(1).uppercase(Locale.getDefault())
        holder.lastMessage.text = item.lastText
        holder.time.text = formatTime(item.lastTimestamp)
        holder.countBadge.text = item.count.toString()
        holder.root.setOnClickListener { onClick(item) }
    }

    private fun formatTime(timestamp: Long): String {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { timeInMillis = timestamp }
        val sameDay = now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
        return if (sameDay) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
        } else {
            SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
        }
    }

    override fun getItemCount() = items.size
}
