package com.example.medilocker

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dashboard_activity)
        val btnOpenAI = findViewById<Button>(R.id.btnOpenAI)

        btnOpenAI.setOnClickListener {
            val intent = Intent(this, AIChatActivity::class.java)
            startActivity(intent)
        }

        // Dynamic greeting
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 5..11 -> "Good Morning 👋"
            in 12..16 -> "Good Afternoon ☀️"
            in 17..20 -> "Good Evening 🌆"
            else -> "Good Night 🌙"
        }

        tvGreeting.text = greeting

        val btnPrescriptions = findViewById<TextView>(R.id.btnPrescriptions)

        btnPrescriptions.setOnClickListener {
            val intent = Intent(this, PrescriptionDetailsActivity::class.java)
            startActivity(intent)
        }

        val btnLabReports = findViewById<TextView>(R.id.btnLabReports)

        btnLabReports.setOnClickListener {
            val intent = Intent(this, RecordDetailsActivity::class.java)
            startActivity(intent)
        }

        val btnScanReports = findViewById<TextView>(R.id.btnScanReports)

        btnScanReports.setOnClickListener {
            startActivity(
                Intent(this, ScanReportsActivity::class.java)
            )
        }

        // Open Medical Records
        val healthSummaryCard =
            findViewById<androidx.cardview.widget.CardView>(R.id.healthSummaryCard)

        healthSummaryCard.setOnClickListener {

            val intent = Intent(this, RecordsActivity::class.java)
            startActivity(intent)
        }

        val navRecords = findViewById<TextView>(R.id.navRecords)

        navRecords.setOnClickListener {
            val intent = Intent(this, RecordsActivity::class.java)
            startActivity(intent)
        }
        val navHome = findViewById<TextView>(R.id.navHome)

        navHome.setOnClickListener {
            recreate()
        }
        val navAlerts = findViewById<TextView>(R.id.navAlerts)

        navAlerts.setOnClickListener {
            val intent = Intent(this, AlertsActivity::class.java)
            startActivity(intent)
        }
        val navProfile = findViewById<TextView>(R.id.navProfile)

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        val btnAddRecord = findViewById<
                com.google.android.material.button.MaterialButton
                >(R.id.btnAddRecord)

        btnAddRecord.setOnClickListener {
            val intent = Intent(this, UploadRecordActivity::class.java)
            startActivity(intent)
        }
        val searchBox = findViewById<EditText>(R.id.etSearchRecords)

        searchBox.setOnEditorActionListener { _, _, _ ->
            val query = searchBox.text.toString().trim()

            if (query.isNotEmpty()) {
                val intent = Intent(this, RecordsActivity::class.java)
                intent.putExtra("search_query", query)
                startActivity(intent)
            }

            true
        }
    }
}