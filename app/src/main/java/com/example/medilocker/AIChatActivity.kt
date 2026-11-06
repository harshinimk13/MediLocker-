package com.example.medilocker

import android.graphics.Typeface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class AIChatActivity : AppCompatActivity() {

    private lateinit var etChatMessage: EditText
    private lateinit var btnSendMessage: Button
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScrollView: ScrollView

    private var isWaitingForResponse = false

    // Android Emulator connects to the computer's localhost using 10.0.2.2.
    private val serverUrl = "http://10.0.2.2:5000/chat"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)

        etChatMessage = findViewById(R.id.etChatMessage)
        btnSendMessage = findViewById(R.id.btnSendMessage)
        chatContainer = findViewById(R.id.chatContainer)
        chatScrollView = findViewById(R.id.chatScrollView)

        addMessage(
            sender = "MediLocker AI",
            message = "Hello! I'm MediLocker AI. Ask me general health questions or ask me to explain medical terms.",
            isUser = false
        )

        btnSendMessage.setOnClickListener {
            sendMessage()
        }

        etChatMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        if (isWaitingForResponse) return

        val message = etChatMessage.text.toString().trim()

        if (message.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter a question.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (message.length > 4000) {
            Toast.makeText(
                this,
                "Please shorten your question.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        addMessage(
            sender = "You",
            message = message,
            isUser = true
        )

        etChatMessage.text.clear()
        etChatMessage.clearFocus()

        isWaitingForResponse = true
        btnSendMessage.isEnabled = false
        btnSendMessage.text = "..."

        val loadingView = addMessage(
            sender = "MediLocker AI",
            message = "Thinking...",
            isUser = false
        )

        thread {
            var connection: HttpURLConnection? = null

            try {
                connection = (URL(serverUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 20000
                    readTimeout = 120000
                    doOutput = true
                    doInput = true
                    setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                    )
                    setRequestProperty("Accept", "application/json")
                }

                val requestBody = JSONObject()
                    .put("message", message)
                    .toString()

                connection.outputStream.use { output ->
                    output.write(requestBody.toByteArray(Charsets.UTF_8))
                }

                val statusCode = connection.responseCode

                val responseStream = if (statusCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val responseText = responseStream?.bufferedReader()?.use {
                    it.readText()
                }.orEmpty()

                val jsonResponse = if (responseText.isNotBlank()) {
                    JSONObject(responseText)
                } else {
                    JSONObject()
                }

                if (statusCode in 200..299) {
                    val reply = jsonResponse.optString(
                        "reply",
                        "The AI returned an empty response."
                    )

                    runOnUiThread {
                        chatContainer.removeView(loadingView)

                        addMessage(
                            sender = "MediLocker AI",
                            message = reply,
                            isUser = false
                        )

                        finishRequest()
                    }
                } else {
                    val errorMessage = jsonResponse.optString(
                        "error",
                        "The AI service returned HTTP $statusCode."
                    )

                    runOnUiThread {
                        chatContainer.removeView(loadingView)

                        addMessage(
                            sender = "MediLocker AI",
                            message = errorMessage,
                            isUser = false
                        )

                        finishRequest()
                    }
                }

            } catch (e: Exception) {
                Log.e("MediLockerAI", "Chat request failed", e)

                runOnUiThread {
                    chatContainer.removeView(loadingView)

                    addMessage(
                        sender = "MediLocker AI",
                        message = when (e) {
                            is java.net.SocketTimeoutException ->
                                "The response took too long. Please try again."

                            else ->
                                "Couldn't connect to the AI server. Check that the backend is running and try again."
                        },
                        isUser = false
                    )

                    finishRequest()
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun finishRequest() {
        isWaitingForResponse = false
        btnSendMessage.isEnabled = true
        btnSendMessage.text = "Send"
        etChatMessage.clearFocus()
    }

    private fun addMessage(
        sender: String,
        message: String,
        isUser: Boolean
    ): TextView {

        val cleanMessage = formatMarkdown(message)

        val fullText = "$sender\n\n$cleanMessage"
        val formattedText = SpannableString(fullText)

        if (sender.isNotEmpty()) {
            formattedText.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                sender.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val messageView = TextView(this).apply {
            text = formattedText
            textSize = 14f
            setTextColor(Color.rgb(38, 55, 70))
            setPadding(dp(14), dp(12), dp(14), dp(12))
            setLineSpacing(dp(3).toFloat(), 1.0f)
            setTextIsSelectable(true)

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(18).toFloat()

                setColor(
                    if (isUser) {
                        Color.rgb(220, 238, 255)
                    } else {
                        Color.WHITE
                    }
                )

                if (!isUser) {
                    setStroke(
                        dp(1),
                        Color.rgb(225, 234, 242)
                    )
                }
            }
        }

        val params = LinearLayout.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.78f).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
            marginStart = dp(8)
            marginEnd = dp(8)
            gravity = if (isUser) Gravity.END else Gravity.START
        }

        chatContainer.addView(messageView, params)

        chatScrollView.post {
            chatScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        return messageView
    }

    private fun formatMarkdown(input: String): String {
        return input
            // Remove Markdown heading symbols.
            .replace(Regex("(?m)^#{1,6}\\s*"), "")

            // Remove bold markers while preserving their text.
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("__(.*?)__"), "$1")

            // Remove italic markers.
            .replace(Regex("(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)"), "$1")
            .replace(Regex("(?<!_)_([^_\\n]+)_(?!_)"), "$1")

            // Convert common Markdown bullet points.
            .replace(Regex("(?m)^\\s*[-*]\\s+"), "• ")

            // Normalize paragraph spacing.
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}

