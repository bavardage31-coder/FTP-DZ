package com.example.ftpsender

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

        private var fileUri: Uri? = null
            private lateinit var host: EditText
                private lateinit var port: EditText
                    private lateinit var user: EditText
                        private lateinit var pass: EditText
                            private lateinit var remoteDir: EditText
                                private lateinit var resume: CheckBox
                                    private lateinit var fileLabel: TextView
                                        private lateinit var status: TextView
                                            private lateinit var bar: ProgressBar
                                                private lateinit var sendBtn: Button

                                                    private val picker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                                                            if (uri != null) {
                                                                            try {
                                                                                                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                                            } catch (_: Exception) {}
                                                                                        fileUri = uri
                                                                                                    fileLabel.text = queryName(uri)
                                                            }
                                                    }
                                                        private val notifPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

                                                            override fun onCreate(savedInstanceState: Bundle?) {
                                                                        super.onCreate(savedInstanceState)
                                                                                if (Build.VERSION.SDK_INT >= 33) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)

                                                                                        fun field(hint: String, type: Int = InputType.TYPE_CLASS_TEXT) = EditText(this).apply {
                                                                                                        this.hint = hint; inputType = type; setSingleLine()
                                                                                        }
                                                                                                host = field("Host (مثال: 192.168.1.10)", InputType.TYPE_TEXT_VARIATION_URI)
                                                                                                        port = field("Port", InputType.TYPE_CLASS_NUMBER).apply { setText("21") }
                                                                                                                user = field("Username").apply { setText("anonymous") }
                                                                                                                        pass = field("Password", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                                                                                                                remoteDir = field("Remote folder (مثال: /uploads)").apply { setText("/") }
                                                                                                                                        resume = CheckBox(this).apply { text = "استئناف إن وُجد جزء من الملف على السيرفر"; isChecked = true }
                                                                                                                                                fileLabel = TextView(this).apply { text = "لم يتم اختيار ملف" }
                                                                                                                                                        status = TextView(this)
                                                                                                                                                                bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply { max = 100 }
                                                                                                                                                                        val pickBtn = Button(this).apply { text = "اختيار ملف"; setOnClickListener { picker.launch(arrayOf("*/*")) } }
                                                                                                                                                                                sendBtn = Button(this).apply { text = "إرسال"; setOnClickListener { startUpload() } }
                                                                                                                                                                                        val stopBtn = Button(this).apply {
                                                                                                                                                                                                        text = "إيقاف"
                                                                                                                                                                                                                    setOnClickListener {
                                                                                                                                                                                                                                        startService(Intent(this@MainActivity, UploadService::class.java).setAction(UploadService.ACTION_CANCEL))
                                                                                                                                                                                                                    }
                                                                                                                                                                                        }

                                                                                                                                                                                                val root = LinearLayout(this).apply {
                                                                                                                                                                                                                orientation = LinearLayout.VERTICAL
                                                                                                                                                                                                                            setPadding(48, 48, 48, 48)
                                                                                                                                                                                                                                        listOf(host, port, user, pass, remoteDir, resume, pickBtn, fileLabel, sendBtn, stopBtn, bar, status)
                                                                                                                                                                                                                                                        .forEach { addView(it) }
                                                                                                                                                                                                }
                                                                                                                                                                                                        setContentView(ScrollView(this).apply { addView(root) })
                                                            }

                                                                override fun onStart() {
                                                                            super.onStart()
                                                                                    UploadState.listener = { msg, pct, running ->
                                                                                                status.text = msg
                                                                                                            bar.progress = pct
                                                                                                                        sendBtn.isEnabled = !running
                                                                                    }
                                                                }

                                                                    override fun onStop() {
                                                                                UploadState.listener = null
                                                                                        super.onStop()
                                                                    }

                                                                        private fun startUpload() {
                                                                                    val uri = fileUri ?: run { status.text = "اختر ملفاً أولاً"; return }
                                                                                            val h = host.text.toString().trim()
                                                                                                    if (h.isEmpty()) { status.text = "أدخل عنوان السيرفر"; return }

                                                                                                            val i = Intent(this, UploadService::class.java)
                                                                                                                        .putExtra("uri", uri.toString())
                                                                                                                                    .putExtra("host", h)
                                                                                                                                                .putExtra("port", port.text.toString().toIntOrNull() ?: 21)
                                                                                                                                                            .putExtra("user", user.text.toString())
                                                                                                                                                                        .putExtra("pass", pass.text.toString())
                                                                                                                                                                                    .putExtra("dir", remoteDir.text.toString().ifBlank { "/" })
                                                                                                                                                                                                .putExtra("resume", resume.isChecked)
                                                                                                                                                                                                        startForegroundService(i)
                                                                                                                                                                                                                sendBtn.isEnabled = false
                                                                                                                                                                                                                        bar.progress = 0
                                                                                                                                                                                                                                status.text = "بدأ الرفع في الخلفية..."
                                                                        }

                                                                            private fun queryName(uri: Uri): String {
                                                                                        var name = "file"
                                                                                                contentResolver.query(uri, null, null, null, null)?.use { c ->
                                                                                                            if (c.moveToFirst()) {
                                                                                                                                val n = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                                                                                                                                if (n >= 0) name = c.getString(n)
                                                                                                            }
                                                                                                }
                                                                                                        return name
                                                                            }
}

                                                                            }
                                                            }}
}