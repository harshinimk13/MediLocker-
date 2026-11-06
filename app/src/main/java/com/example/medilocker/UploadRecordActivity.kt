package com.example.medilocker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONObject

class UploadRecordActivity : AppCompatActivity() {

    private var selectedFileUri: Uri? = null
    private lateinit var tvSelectedFile: TextView

    // Open the Android document picker
    private val documentPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri != null) {
                selectedFileUri = uri

                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Continue if persistent permission is unavailable
                }

                val fileName = getFileName(uri)
                tvSelectedFile.text = "Selected: $fileName"
            }
        }

    // Get the actual selected document filename
    private fun getFileName(uri: Uri): String {
        var fileName = "Selected document"

        contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->

            if (cursor.moveToFirst()) {
                val columnIndex =
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (columnIndex >= 0) {
                    fileName = cursor.getString(columnIndex)
                        ?: fileName
                }
            }
        }

        return fileName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upload_record)

        // Back button
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Record type dropdown
        val spinnerRecordType =
            findViewById<Spinner>(R.id.spinnerRecordType)

        val recordTypes = arrayOf(
            "Prescription",
            "Lab Report",
            "Scan Report",
            "Discharge Summary",
            "Medical Bill"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            recordTypes
        )

        spinnerRecordType.adapter = adapter

        // Form fields
        val etRecordTitle =
            findViewById<EditText>(R.id.etRecordTitle)

        val etHospital =
            findViewById<EditText>(R.id.etHospital)

        tvSelectedFile =
            findViewById(R.id.tvSelectedFile)

        // Select document
        val cardSelectDocument =
            findViewById<CardView>(R.id.cardSelectDocument)

        cardSelectDocument.setOnClickListener {
            documentPicker.launch(
                arrayOf(
                    "application/pdf",
                    "image/jpeg",
                    "image/png"
                )
            )
        }

        // Upload button
        val btnUpload =
            findViewById<MaterialButton>(R.id.btnUpload)

        btnUpload.setOnClickListener {

            val title = etRecordTitle.text.toString().trim()
            val hospital = etHospital.text.toString().trim()
            val recordType = spinnerRecordType.selectedItem.toString()
            val documentUri = selectedFileUri

            // Validate title
            if (title.isEmpty()) {
                etRecordTitle.error = "Please enter the record title"
                etRecordTitle.requestFocus()
                return@setOnClickListener
            }

            // Validate hospital
            if (hospital.isEmpty()) {
                etHospital.error = "Please enter the hospital name"
                etHospital.requestFocus()
                return@setOnClickListener
            }

            // Validate document
            if (documentUri == null) {
                Toast.makeText(
                    this,
                    "Please select a medical document",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Save record details locally
            val preferences = getSharedPreferences(
                "MediLockerRecords",
                MODE_PRIVATE
            )

            val existingRecords = preferences.getString(
                "records",
                "[]"
            ) ?: "[]"

            try {
                val records = JSONArray(existingRecords)

                val record = JSONObject().apply {
                    put("title", title)
                    put("hospital", hospital)
                    put("type", recordType)
                    put("documentUri", documentUri.toString())
                    put("fileName", getFileName(documentUri))
                    put("uploadedAt", System.currentTimeMillis())
                }

                records.put(record)

                val saved = preferences.edit()
                    .putString("records", records.toString())
                    .commit()

                if (saved) {
                    Toast.makeText(
                        this,
                        "Medical record saved successfully!",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Could not save the record. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Unable to save the record.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}