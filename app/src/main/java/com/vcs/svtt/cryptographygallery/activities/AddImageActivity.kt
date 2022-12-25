package com.vcs.svtt.cryptographygallery.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vcs.svtt.cryptographygallery.R
import com.vcs.svtt.cryptographygallery.cryptography.CryptographyHelper
import com.vcs.svtt.cryptographygallery.model.Image
import com.vcs.svtt.cryptographygallery.model.ImageManager
import com.vcs.svtt.cryptographygallery.utils.PathUtil
import kotlinx.android.synthetic.main.activity_add_image.*
import kotlinx.android.synthetic.main.activity_add_image.view.*
import java.io.File


@Suppress("DEPRECATION")
class AddImageActivity : AppCompatActivity() {
    companion object {
        private const val PICK_IMAGE = 12
        private const val TAG = "ADD IMAGE"
    }

    private var imagePath: String = "error path"
    private var aesCheck = false
    private var desCheck = false
    private var rsaCheck = false
    private var md5Check = false
    private var sha1Check = false
    private var sha512Check = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.top_app_bar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        upload_image_btn.setOnClickListener { pickImageFromGallery() }

        confirm_button.setOnClickListener { encryptImage() }

        aes_checkbox.setOnCheckedChangeListener { _, isChecked ->
            aesCheck = isChecked
        }

        des_checkbox.setOnCheckedChangeListener { _, isChecked ->
            desCheck = isChecked
        }

        rsa_checkbox.setOnCheckedChangeListener { _, isChecked ->
            rsaCheck = isChecked
        }

        hash_checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hash_select_radio_group.visibility = View.VISIBLE
            } else {
                hash_select_radio_group.visibility = View.GONE
                hash_select_radio_group.clearCheck()
                md5Check = false
                sha1Check = false
                sha512Check = false
            }
        }

        hash_select_radio_group.setOnCheckedChangeListener {
            _, checked ->
            md5Check = false
            sha1Check = false
            sha512Check = false
            when (checked) {
                R.id.md5 -> md5Check = true
                R.id.sha1 -> sha1Check = true
                R.id.sha512 -> sha512Check = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.confirm_menu -> {
                encryptImage()
                return true
            }

            R.id.clear_menu -> {
                clearForm()
                return true
            }

            else -> return false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_image_menu, menu)
        return true
    }

    @SuppressLint("IntentReset")
    private fun pickImageFromGallery() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            imagePath = selectedImage?.path.toString()
            Log.d(TAG, "image path: $imagePath")

            imagePath = PathUtil.getPath(this, selectedImage!!).toString()
            val file = File(imagePath)
            if (!file.isFile) {
                Toast.makeText(this, "Invalid Image. Please try again", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d(TAG, "absolute image path: $imagePath")
            preview_iv.setImageURI(selectedImage)
            preview_iv.visibility = View.VISIBLE
            preview_panel_tv.visibility = View.VISIBLE
            confirm_button.visibility = View.VISIBLE
        }
    }

    private fun clearForm() {
        preview_panel_tv.visibility = View.GONE
        confirm_button.visibility = View.GONE
        preview_iv.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun encryptImage() {
        val imageFile = File(imagePath)
        val name = imageFile.name
        val image = Image(name, imagePath)
        ImageManager.getInstance().addImage(image)

        CryptographyHelper.encrypt(image,CryptographyHelper.AES or CryptographyHelper.DES)
    }
}