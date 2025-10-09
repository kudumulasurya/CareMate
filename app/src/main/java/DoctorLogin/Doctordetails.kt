package DoctorLogin

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.medicare.R
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class Doctordetails : AppCompatActivity() {

    private lateinit var hospitalName: TextInputEditText
    private lateinit var specialization: TextInputEditText
    private lateinit var briefDescription: TextInputEditText
    private lateinit var yearsOfExp: TextInputEditText
    private lateinit var consultationFee: TextInputEditText
    private lateinit var profilePicCard: CardView
    private lateinit var licensePicCard: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var button: Button
    private lateinit var profilePicImage: ImageView
    private lateinit var licensePicImage: ImageView

    private var currentImageSlot: String? = null // "profile" or "license"
    private var tempImageUri: Uri? = null
    private var profileImageUrl: String? = null
    private var licenseImageUrl: String? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            tempImageUri?.let {
                showLoading(true)
                updateImageView(it)
                uploadImageToCloudinary(it, currentImageSlot)
            }
        }
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            showLoading(true)
            updateImageView(it)
            uploadImageToCloudinary(it, currentImageSlot)
        }
    }
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) openCamera()
        else Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
    }
    private val galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) openGallery()
        else Toast.makeText(this, "Storage permission is required.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctordetails)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val doctorMap = intent.getSerializableExtra("doctorMap") as HashMap<String, String>
        initViews()
        setupClickListeners(doctorMap)
    }

    private fun initViews() {
        hospitalName = findViewById(R.id.hospitalname)
        specialization = findViewById(R.id.Specialization)
        briefDescription = findViewById(R.id.Briefdescription)
        yearsOfExp = findViewById(R.id.yearsofexp)
        consultationFee = findViewById(R.id.Consultationfee)
        profilePicCard = findViewById(R.id.profilePicCard)
        licensePicCard = findViewById(R.id.licensePicCard)
        progressBar = findViewById(R.id.progressBar)
        button = findViewById(R.id.button)
        profilePicImage = findViewById(R.id.profilePicImage)
        licensePicImage = findViewById(R.id.licensePicImage)
    }

    private fun setupClickListeners(doctorMap: HashMap<String, String>) {
        profilePicCard.setOnClickListener {
            if (!progressBar.isVisible) {
                currentImageSlot = "profile"
                showImageSourceDialog()
            }
        }
        licensePicCard.setOnClickListener {
            if (!progressBar.isVisible) {
                currentImageSlot = "license"
                showImageSourceDialog()
            }
        }
        button.setOnClickListener {
            if (!progressBar.isVisible) saveAndGoToBankDetails(doctorMap)
        }
    }

    private fun showImageSourceDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(arrayOf("Camera", "Gallery")) { _, which ->
                if (which == 0) {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    galleryPermissionLauncher.launch(permission)
                }
            }.show()
    }

    private fun openCamera() {
        val imageFile = File(cacheDir, "doctor_${currentImageSlot}_${System.currentTimeMillis()}.jpg")
        val authority = "${applicationContext.packageName}.provider"
        tempImageUri = FileProvider.getUriForFile(this, authority, imageFile)
        tempImageUri?.let { uri -> cameraLauncher.launch(uri) }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun updateImageView(uri: Uri) {
        if (currentImageSlot == "profile") {
            profilePicImage.setImageURI(uri)
        } else {
            licensePicImage.setImageURI(uri)
        }
    }

    private fun uploadImageToCloudinary(uri: Uri, imageSlot: String?) {
        MediaManager.get().upload(uri)
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("url")?.toString()
                    if (imageSlot == "profile") profileImageUrl = url else licenseImageUrl = url
                    showLoading(false)
                }
                override fun onError(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {
                    showLoading(false)
                    Toast.makeText(this@Doctordetails, "Upload failed: ${error?.description}", Toast.LENGTH_SHORT).show()
                }
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: com.cloudinary.android.callback.ErrorInfo?) {}
            })
            .dispatch()
    }

    private fun saveAndGoToBankDetails(doctorMap: HashMap<String, String>) {
        val hospital = hospitalName.text.toString().trim()
        val spec = specialization.text.toString().trim()
        val desc = briefDescription.text.toString().trim()
        val exp = yearsOfExp.text.toString().trim()
        val fee = consultationFee.text.toString().trim()

        if (hospital.isEmpty() || spec.isEmpty() || desc.isEmpty() || exp.isEmpty() || fee.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (profileImageUrl.isNullOrEmpty() || licenseImageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Please upload both profile and license images", Toast.LENGTH_SHORT).show()
            return
        }

        doctorMap.apply {
            put("hospitalName", hospital)
            put("specialization", spec)
            put("briefDescription", desc)
            put("yearsOfExperience", exp)
            put("consultationFee", fee)
            put("profileImageUrl", profileImageUrl!!)
            put("licenseImageUrl", licenseImageUrl!!)
        }
        val intent = Intent(this, Bankdetails::class.java)
        intent.putExtra("doctorMap", doctorMap)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
        button.isEnabled = !isLoading
        profilePicCard.isEnabled = !isLoading
        licensePicCard.isEnabled = !isLoading
    }
}
