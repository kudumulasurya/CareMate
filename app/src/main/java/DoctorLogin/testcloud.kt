package DoctorLogin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.medicare.R
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.callback.ErrorInfo

class testcloud : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var urltext: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testcloud)

        val button = findViewById<Button>(R.id.button)
        progressBar = findViewById(R.id.progress)
        urltext = findViewById(R.id.url)
        urltext.isVisible = false

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            val selectedImageUri = it.data?.data
            selectedImageUri?.let { uri ->
                uploadImageToCloudinary(uri)
            }
        }
    }

    private fun uploadImageToCloudinary(uri: Uri) {
        progressBar.isVisible = true
        urltext.isVisible = false

        MediaManager.get().upload(uri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    // Optionally update UI at start
                }
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    // Optionally update a progress indicator; not required for a simple progressBar
                }
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    Toast.makeText(this@testcloud, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    val url = resultData?.get("url").toString()
                    urltext.text = url
                    progressBar.isVisible = false
                    urltext.isVisible = true
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    progressBar.isVisible = false
                    Toast.makeText(this@testcloud, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    // Not required in most cases
                }
            }).dispatch()
    }
}
