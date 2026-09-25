package com.example.notisaver

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applySavedTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val checkLight = findViewById<ImageView>(R.id.checkLight)
        val checkDark = findViewById<ImageView>(R.id.checkDark)
        val checkSystem = findViewById<ImageView>(R.id.checkSystem)

        fun refreshChecks() {
            val mode = ThemeManager.getSavedMode(this)
            checkLight.visibility = if (mode == ThemeManager.MODE_LIGHT) View.VISIBLE else View.INVISIBLE
            checkDark.visibility = if (mode == ThemeManager.MODE_DARK) View.VISIBLE else View.INVISIBLE
            checkSystem.visibility = if (mode == ThemeManager.MODE_SYSTEM) View.VISIBLE else View.INVISIBLE
        }

        refreshChecks()

        findViewById<View>(R.id.optionLight).setOnClickListener {
            ThemeManager.setMode(this, ThemeManager.MODE_LIGHT)
            refreshChecks()
        }
        findViewById<View>(R.id.optionDark).setOnClickListener {
            ThemeManager.setMode(this, ThemeManager.MODE_DARK)
            refreshChecks()
        }
        findViewById<View>(R.id.optionSystem).setOnClickListener {
            ThemeManager.setMode(this, ThemeManager.MODE_SYSTEM)
            refreshChecks()
        }
    }
}
