package com.example.medicare

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build.VERSION_CODES.N
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File

class Documinder : Fragment() {

    private lateinit var reminderTab: Button
    private lateinit var documentTab: Button
    private lateinit var backButton: ImageView
    private lateinit var containerFrame: FrameLayout
    private lateinit var addButton: FloatingActionButton

    private val REQUEST_CAMERA = 100
    private val REQUEST_GALLERY = 101
    private val REQUEST_PDF = 102
    private val REQUEST_CAMERA_PERMISSION = 103

    private var cameraImageUri: Uri? = null
    private var currentDocumentsFragment: Documents? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_documinder, container, false)

        reminderTab = view.findViewById(R.id.reminderTab)
        documentTab = view.findViewById(R.id.documentTab)
        backButton = view.findViewById(R.id.Back)
        containerFrame = view.findViewById(R.id.containerFrame)
        addButton = view.findViewById(R.id.Add)

        // Load default tab
        loadFragment(Reminders())
        setTabSelected(reminderTab, documentTab)

        reminderTab.setOnClickListener {
            loadFragment(Reminders())
            setTabSelected(reminderTab, documentTab)
        }

        documentTab.setOnClickListener {
            val documents = Documents()
            currentDocumentsFragment = documents
            loadFragment(documents)
            setTabSelected(documentTab, reminderTab)
        }

        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }



        addButton.setOnClickListener {
            showDocumentTypeDialog()
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.containerFrame, fragment)
            .commit()
    }

    private fun setTabSelected(selected: Button, unselected: Button) {
        selected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
        unselected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_tab))
    }

    private fun showDocumentTypeDialog() {
        val options = arrayOf("Camera (Image)", "Gallery (Image)", "Choose PDF")
        AlertDialog.Builder(requireContext())
            .setTitle("Upload Document From")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                    2 -> openPDFPicker()
                }
            }
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val imagesFolder = File(requireContext().cacheDir, "images")
        if (!imagesFolder.exists()) imagesFolder.mkdirs()

        val imageFile = File(imagesFolder, "captured_image_${System.currentTimeMillis()}.jpg")

        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)

        val resInfoList = requireContext().packageManager.queryIntentActivities(cameraIntent, 0)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            requireContext().grantUriPermission(
                packageName, cameraImageUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    private fun openPDFPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_PDF)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = when (requestCode) {
                REQUEST_GALLERY -> data?.data
                REQUEST_CAMERA -> cameraImageUri
                REQUEST_PDF -> data?.data
                else -> null
            }

            uri?.let {
                val fragment = childFragmentManager.findFragmentById(R.id.containerFrame)
                if (fragment is Documents) {
                    fragment.addImage(it)
                } else {
                    val documents = Documents()
                    currentDocumentsFragment = documents
                    childFragmentManager.beginTransaction()
                        .replace(R.id.containerFrame, documents)
                        .commitNow()
                    documents.addImage(it)
                    setTabSelected(documentTab, reminderTab)
                }
            }
        }
    }
}
