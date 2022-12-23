package com.vcs.svtt.cryptographygallery.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.vcs.svtt.cryptographygallery.R
import com.vcs.svtt.cryptographygallery.model.Image
import java.io.File


class ImageGalleryAdapter(
    private var context: Context,
    private var imageList: ArrayList<Image>,
    private var isEncrypted: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "ADAPTER TAG"
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.image_card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file = File(imageList[position].rawFilePath)

        if (file.exists() && file.isFile) {
            val imageView: ImageView = holder.itemView.findViewById(R.id.image_IV)
            /*// if the file exists then we are displaying that file in our image view using picasso library.
            Picasso.get().load(file).placeholder(R.drawable.ic_launcher_background).into(imageView)
            Log.d(TAG,"image name: ${imageList[position].fileName}\nimage path: ${imageList[position].rawFilePath}")*/

            val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(myBitmap)
        }
        else
            Log.d(TAG, "file not exist!")

    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}