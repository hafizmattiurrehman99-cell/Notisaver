package com.example.notisaver

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppGroupAdapter(
    private val context: Context,
    private val onClick: (AppGroup) -> Unit
) : RecyclerView.Adapter<AppGroupAdapter.ViewHolder>() {

    private var items: List<AppGroup> = emptyList()
    private val iconCache = HashMap<String, Drawable?>()

    fun submitList(list: List<AppGroup>) {
        items = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view.findViewById(R.id.rootRow)
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val appName: TextView = view.findViewById(R.id.appName)
        val countLabel: TextView = view.findViewById(R.id.countLabel)
        val time: TextView = view.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.appName.text = item.appName
        holder.countLabel.text = if (item.count == 1) "1 notification" else "${item.count} notifications"
        holder.time.text = formatTime(item.lastTimestamp)

        val icon = getAppIcon(item.packageName)
        if (icon != null) {
            holder.appIcon.setImageDrawable(icon)
            holder.appIcon.background = null
            holder.appIcon.setPadding(0, 0, 0, 0)
        } else {
            holder.appIcon.setImageResource(R.drawable.ic_app_placeholder)
            holder.appIcon.setBackgroundResource(R.drawable.bg_app_icon)
            val pad = (9 * context.resources.displayMetrics.density).toInt()
            holder.appIcon.setPadding(pad, pad, pad, pad)
        }

        holder.root.setOnClickListener { onClick(item) }
    }

    private fun getAppIcon(packageName: String): Drawable? {
        if (iconCache.containsKey(packageName)) return iconCache[packageName]
        val drawable = try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
        iconCache[packageName] = drawable
        return drawable
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
