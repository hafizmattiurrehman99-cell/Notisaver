package com.example.notisaver

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConversationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_TITLE = "extra_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""

        findViewById<TextView>(R.id.toolbarTitle).text = title.ifEmpty { "Unknown" }
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ConversationAdapter()
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        AppDatabase.getInstance(this).notificationDao()
            .getMessagesForSender(packageName, title)
            .observe(this) { list ->
                adapter.submitList(list)
                if (list.isNotEmpty()) {
                    recyclerView.scrollToPosition(list.size - 1)
                }
            }
    }
}
