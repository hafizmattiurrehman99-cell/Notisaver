package com.example.notisaver

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SenderListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender_list)

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: ""

        findViewById<TextView>(R.id.toolbarTitle).text = appName
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = SenderAdapter { sender ->
            val i = Intent(this, ConversationActivity::class.java)
            i.putExtra(ConversationActivity.EXTRA_PACKAGE_NAME, packageName)
            i.putExtra(ConversationActivity.EXTRA_TITLE, sender.title)
            startActivity(i)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        AppDatabase.getInstance(this).notificationDao().getSenderGroups(packageName)
            .observe(this) { list -> adapter.submitList(list) }
    }
}
