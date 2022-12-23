package com.vcs.svtt.cryptographygallery.utils

import android.app.Activity
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

class ImageGalleryLoader(private val activity: Activity) : LoaderManager.LoaderCallbacks<Cursor> {

    private val IMAGE_LOADER_ID = 1
    private val listOfAllImages = ArrayList<String>()

    fun init(loaderManager: LoaderManager) {
        loaderManager.initLoader(IMAGE_LOADER_ID,null,this)
    }



    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val selection: String? = null     //Selection criteria
        val selectionArgs = arrayOf<String>()  //Selection criteria
        val sortOrder: String? = null

        return CursorLoader(
            activity.applicationContext,
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
    }

    override fun onLoadFinished(p0: Loader<Cursor>, cursor: Cursor?) {
        cursor?.let {
            val columnIndexData = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (it.moveToNext()) {

                listOfAllImages.add(it.getString(columnIndexData));
            }
        }
    }

    fun getImageList(): ArrayList<String> {
        return listOfAllImages
    }

    override fun onLoaderReset(p0: Loader<Cursor>) {
        TODO("Not yet implemented")
    }
}