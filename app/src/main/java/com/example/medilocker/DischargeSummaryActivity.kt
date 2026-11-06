package com.example.medilocker

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DischargeSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_discharge_summary)
        val btnBack = findViewById<TextView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}