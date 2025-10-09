package com.example.medicare

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImagePreviewActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        val imageView: ImageView = findViewById(R.id.fullImageView)
        val imageUri = intent.getStringExtra("imageUri")

        if (imageUri != null) {
            imageView.setImageURI(Uri.parse(imageUri))
        }
    }
}
