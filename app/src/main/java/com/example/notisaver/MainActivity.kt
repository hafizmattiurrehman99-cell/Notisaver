package com.example.notisaver

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: AppGroupAdapter
    private lateinit var permissionCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val emptyState = findViewById<View>(R.id.emptyState)
        permissionCard = findViewById(R.id.permissionCard)

        adapter = AppGroupAdapter(this) { group ->
            val i = Intent(this, SenderListActivity::class.java)
            i.putExtra(SenderListActivity.EXTRA_PACKAGE_NAME, group.packageName)
            i.putExtra(SenderListActivity.EXTRA_APP_NAME, group.appName)
            startActivity(i)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        AppDatabase.getInstance(this).notificationDao().getAppGroups()
            .observe(this) { list ->
                adapter.submitList(list)
                if (list.isEmpty()) {
                    emptyState.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyState.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }

        findViewById<View>(R.id.btnPermission).setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        permissionCard.visibility = if (isNotificationAccessGranted()) View.GONE else View.VISIBLE
    }

    private fun isNotificationAccessGranted(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners != null && enabledListeners.contains(packageName)
    }
}
