package com.vcs.svtt.cryptographygallery.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import com.vcs.svtt.cryptographygallery.R
import com.vcs.svtt.cryptographygallery.adapter.ImageGalleryAdapter
import com.vcs.svtt.cryptographygallery.fragments.ViewImageFragment
import com.vcs.svtt.cryptographygallery.model.ImageManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.File


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    companion object {

        const val TAG = "CRYPTOGRAPHY APP"
        private const val IMAGE_LOADER_ID = 1
        const val ADD_IMAGE_REQUEST_CODE = 101
    }

    private var listOfAllImages = ArrayList<String>()

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

        /*val directory = File("/storage/emulated/0/Download/Cryptography gallery")
        if (!directory.exists()) {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Cryptography gallery/")
            contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        }*/


        //supportLoaderManager.initLoader(IMAGE_LOADER_ID, null, this)

        fab.setOnClickListener {
            openAddImageActivity()
        }

        Log.d(TAG, "loaded ${listOfAllImages.size} image(s)")
        listOfAllImages.forEach { e -> Log.d(TAG, "image $e") }


        gia_lap.setOnClickListener {
            val fragment = ViewImageFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.view_image_fragment, fragment)
                .addToBackStack(null).commit()
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
        imageList.clear()
        ImageManager.getInstance().addAList(listOfAllImages)
        imageList.forEach { i ->
            Log.d(TAG, "image name: ${i.fileName}\nimage path: ${i.rawFilePath}")
        }

        val adapter = ImageGalleryAdapter(this, imageList, false)
        val layoutManager = GridLayoutManager(this, calculateSpanCount())
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val selection: String? = null     //Selection criteria
        val selectionArgs = arrayOf<String>()  //Selection criteria
        val sortOrder: String? = null

        return CursorLoader(
            this.applicationContext, uri, projection, selection, selectionArgs, sortOrder
        )
    }

    override fun onLoadFinished(p0: Loader<Cursor>, cursor: Cursor?) {
        cursor?.let {
            val columnIndexData = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

            while (it.moveToNext()) {

                loading_tv.visibility = TextView.GONE
                listOfAllImages.add(it.getString(columnIndexData))
                Log.d(TAG, "Load finished!")

                Log.d(TAG, "loaded ${listOfAllImages.size} image(s)")
                listOfAllImages.forEach { e -> Log.d(TAG, "image $e") }
            }

            prepareRecyclerView()
        }
    }

    override fun onLoaderReset(p0: Loader<Cursor>) {
        TODO("Not yet implemented")
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

    override fun onDestroy() {
        super.onDestroy()
        supportLoaderManager.destroyLoader(IMAGE_LOADER_ID)
    }
}