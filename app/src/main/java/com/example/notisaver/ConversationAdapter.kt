package com.example.notisaver

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    private var items: List<NotificationEntity> = emptyList()

    fun submitList(list: List<NotificationEntity>) {
        items = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bubbleContainer: LinearLayout = view.findViewById(R.id.bubbleContainer)
        val messageText: TextView = view.findViewById(R.id.messageText)
        val messageTime: TextView = view.findViewById(R.id.messageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_bubble, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.messageText.text = item.text.ifEmpty { item.title }
        holder.messageTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(item.timestamp))

        val params = holder.bubbleContainer.layoutParams as LinearLayout.LayoutParams
        if (item.isOutgoing) {
            params.gravity = Gravity.END
            holder.bubbleContainer.layoutParams = params
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble_outgoing)
            holder.messageText.setTextColor(0xFFFFFFFF.toInt())
            holder.messageTime.setTextColor(0xCCFFFFFF.toInt())
        } else {
            params.gravity = Gravity.START
            holder.bubbleContainer.layoutParams = params
            holder.bubbleContainer.setBackgroundResource(R.drawable.bg_bubble)
            val context = holder.itemView.context
            holder.messageText.setTextColor(
                context.resources.getColor(R.color.text_primary, context.theme)
            )
            holder.messageTime.setTextColor(
                context.resources.getColor(R.color.text_hint, context.theme)
            )
        }
    }

    override fun getItemCount() = items.size
}
