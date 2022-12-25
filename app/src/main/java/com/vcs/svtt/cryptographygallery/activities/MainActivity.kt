package com.vcs.svtt.cryptographygallery.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView
import com.vcs.svtt.cryptographygallery.R
import com.vcs.svtt.cryptographygallery.adapter.ImageGalleryAdapter
import com.vcs.svtt.cryptographygallery.fragments.ViewImageFragment
import com.vcs.svtt.cryptographygallery.model.EncryptedImagePreferences
import com.vcs.svtt.cryptographygallery.model.ImageManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(){
    companion object {
        const val TAG = "CRYPTOGRAPHY APP"
        const val ADD_IMAGE_REQUEST_CODE = 101
    }

    private lateinit var adapter: ImageGalleryAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.top_app_bar)
        setSupportActionBar(toolbar)

        //ask for external storage permission
        if (shouldAskPermissions()) {
            askPermissions()
        }

        fab.setOnClickListener {
            openAddImageActivity()
        }

        //load encrypted images list from shared preferences
        val prefs = EncryptedImagePreferences(this)
        prefs.load()

        prepareRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged()
            Log.d(TAG,"List changed")
            ImageManager.getInstance().getList().forEach { i ->
                Log.d(TAG,"image: path ${i.rawFilePath}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_menu -> {
                openAddImageActivity()
                return true
            }

            R.id.delete_menu -> {
                return true
            }

            R.id.show_as_list_menu -> {
                return true
            }

            else -> return false
        }

    }

    private fun openAddImageActivity() {
        val intent = Intent(this, AddImageActivity::class.java)
        startActivityForResult(intent, ADD_IMAGE_REQUEST_CODE)
    }

    private fun getDeviceWidth(): Float {
        return resources.displayMetrics.run { widthPixels / density }
    }

    private fun calculateSpanCount(): Int {
        val width = getDeviceWidth()
        val span = ((width + 3 - 8) / 123).toInt()
        Log.d(TAG, "width: $width; span: $span")
        return span
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun prepareRecyclerView() {
        val imageList = ImageManager.getInstance().getList()

        adapter = ImageGalleryAdapter(this, imageList)
        val layoutManager = GridLayoutManager(this, calculateSpanCount())
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    //request external memory permission
    private fun shouldAskPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true
        }
        return false
    }

    /**
     * This function request for important permissions, if user accept, application will work correctly
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.MANAGE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}