package com.example.medilocker

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.Toast
import org.json.JSONArray

class RecordsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.records_activity)
        val recordsScrollView =
            findViewById<android.widget.ScrollView>(R.id.recordsScrollView)

        recordsScrollView.setOnTouchListener { view, event ->
            view.parent?.requestDisallowInterceptTouchEvent(true)
            false
        }

        // Back button
        val btnBack = findViewById<TextView>(R.id.btnBack)

        btnBack.setOnClickListener {
            Toast.makeText(
                this,
                "Going back...",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
        // Search box
        val searchBox = findViewById<EditText>(R.id.etSearchRecords)

        val searchQuery = intent.getStringExtra("search_query") ?: ""

        // Record category cards
        val prescriptions = findViewById<CardView>(R.id.cardPrescriptions)
        val labReports = findViewById<CardView>(R.id.cardLabReports)
        val scanReports = findViewById<CardView>(R.id.cardScanReports)
        val discharge = findViewById<CardView>(R.id.cardDischarge)
        val medicalBills = findViewById<CardView>(R.id.cardMedicalBills)

        // Search functionality for categories
        searchBox.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val query = s.toString().trim().lowercase()

                prescriptions.visibility =
                    if (query.isEmpty() || "prescription".contains(query)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                labReports.visibility =
                    if (query.isEmpty() ||
                        "lab".contains(query) ||
                        "laboratory".contains(query)
                    ) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                scanReports.visibility =
                    if (query.isEmpty() ||
                        "scan".contains(query) ||
                        "xray".contains(query)
                    ) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                discharge.visibility =
                    if (query.isEmpty() || "discharge".contains(query)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                medicalBills.visibility =
                    if (query.isEmpty() ||
                        "bill".contains(query) ||
                        "medical".contains(query)
                    ) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Open category screens
        labReports.setOnClickListener {
            startActivity(Intent(this, RecordDetailsActivity::class.java))
        }

        prescriptions.setOnClickListener {
            startActivity(Intent(this, PrescriptionDetailsActivity::class.java))
        }

        scanReports.setOnClickListener {
            startActivity(Intent(this, ScanReportsActivity::class.java))
        }

        discharge.setOnClickListener {
            startActivity(Intent(this, DischargeSummaryActivity::class.java))
        }

        medicalBills.setOnClickListener {
            startActivity(Intent(this, MedicalBillsActivity::class.java))
        }

        // Display uploaded records
        displayUploadedRecords()

        // Apply search query received from Dashboard
        if (searchQuery.isNotEmpty()) {
            searchBox.setText(searchQuery)
        }
    }

    private fun displayUploadedRecords() {

        val preferences = getSharedPreferences(
            "MediLockerRecords",
            MODE_PRIVATE
        )

        val savedRecords = preferences.getString("records", "[]") ?: "[]"
        val records = JSONArray(savedRecords)

        val container = findViewById<LinearLayout>(
            R.id.uploadedRecordsContainer
        )

        container.removeAllViews()

        if (records.length() == 0) {

            val emptyMessage = TextView(this).apply {
                text = "No uploaded records yet.\nAdd your first medical record!"
                textSize = 14f
                setTextColor(Color.rgb(100, 116, 139))
                gravity = Gravity.CENTER
                setPadding(16, 24, 16, 24)
            }

            container.addView(emptyMessage)
            return
        }

        for (i in 0 until records.length()) {

            val record = records.getJSONObject(i)

            val title = record.optString("title", "Medical Record")
            val hospital = record.optString(
                "hospital",
                "Hospital not specified"
            )
            val type = record.optString("type", "Medical Document")
            val fileName = record.optString("fileName", "Document")

            // Record card
            val card = CardView(this).apply {
                radius = 20f
                cardElevation = 3f
                setCardBackgroundColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                params.bottomMargin = 16
                layoutParams = params
            }

            // Card content
            val content = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20, 20, 20, 20)
            }

            // Record title
            val titleView = TextView(this).apply {
                text = title
                textSize = 17f
                setTextColor(Color.rgb(23, 43, 77))
                setTypeface(null, Typeface.BOLD)
            }

            // Record type
            val typeView = TextView(this).apply {
                text = type
                textSize = 13f
                setTextColor(Color.rgb(25, 118, 210))
                setPadding(0, 6, 0, 4)
            }

            // Hospital name
            val hospitalView = TextView(this).apply {
                text = hospital
                textSize = 14f
                setTextColor(Color.rgb(100, 116, 139))
            }

            // Filename
            val fileView = TextView(this).apply {
                text = "📄 $fileName"
                textSize = 12f
                setTextColor(Color.rgb(100, 116, 139))
                setPadding(0, 8, 0, 0)
            }

            content.addView(titleView)
            content.addView(typeView)
            content.addView(hospitalView)
            content.addView(fileView)

            card.addView(content)
            container.addView(card)
        }
    }
}