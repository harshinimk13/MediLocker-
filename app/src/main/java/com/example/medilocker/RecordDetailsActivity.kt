package com.example.medilocker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecordDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.record_details_activity)
        val btnBack = findViewById<TextView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        // View Document button
        val btnViewRecord = findViewById<Button>(R.id.btnViewRecord)

        btnViewRecord.setOnClickListener {

            val intent = Intent(
                this,
                DocumentPreviewActivity::class.java
            )

            startActivity(intent)
        }

        // Share Securely button
        val btnShareSecurely =
            findViewById<Button>(R.id.btnShareSecurely)

        btnShareSecurely.setOnClickListener {

            val shareText = """
                MediLocker Medical Record
                
                Patient: Harshini
                Record: Blood Test Report
                Hospital: Jain Medical Centre
                Date: 18 September 2026
                
                This medical record is securely stored in MediLocker.
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "MediLocker Medical Record")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(
                Intent.createChooser(
                    shareIntent,
                    "Share Medical Record Securely"
                )
            )
        }
    }
}