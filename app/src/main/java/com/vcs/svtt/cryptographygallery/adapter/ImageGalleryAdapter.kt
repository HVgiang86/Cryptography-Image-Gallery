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
import com.vcs.svtt.cryptographygallery.activities.MainActivity
import com.vcs.svtt.cryptographygallery.fragments.ViewImageFragment
import com.vcs.svtt.cryptographygallery.model.Image
import java.io.File


class ImageGalleryAdapter(
    private var context: MainActivity,
    private var imageList: ArrayList<Image>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
        private const val TAG = "ADAPTER TAG"
        private const val HAS_IMAGES_TYPE = 1
        private const val NO_IMAGES_TYPE = 2

        interface ItemClickListener {
            fun onClick(position: Int)
        }
    }

    private var viewType = NO_IMAGES_TYPE

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var clickListener: ItemClickListener
        override fun onClick(v: View?) {
            clickListener.onClick(absoluteAdapterPosition)
        }

        fun setOnClickListener(clickListener: ItemClickListener) {
            this.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var v: View = inflater.inflate(R.layout.image_card_layout, parent, false)

        if (viewType == HAS_IMAGES_TYPE) {
            v = inflater.inflate(R.layout.image_card_layout, parent, false)
        }

        else if (viewType == NO_IMAGES_TYPE)
            v = inflater.inflate(R.layout.no_image_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (imageList.size != 0) {
            val file = File(imageList[position].rawFilePath)

            if (file.exists() && file.isFile) {
                val imageView: ImageView = holder.itemView.findViewById(R.id.image_IV)
                /*// if the file exists then we are displaying that file in our image view using picasso library.
                Picasso.get().load(file).placeholder(R.drawable.ic_launcher_background).into(imageView)
                Log.d(TAG,"image name: ${imageList[position].fileName}\nimage path: ${imageList[position].rawFilePath}")*/

                val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(myBitmap)

                holder.itemView.setOnClickListener {
                    val fragment = ViewImageFragment.newInstance(position)
                    context.supportFragmentManager.beginTransaction()
                        .replace(R.id.view_image_fragment, fragment)
                        .addToBackStack(null).commit()
                }
            }
            else
                Log.d(TAG, "file not exist!")
        }

    }

    override fun getItemCount(): Int {
        return if (imageList.size != 0) {
            viewType = HAS_IMAGES_TYPE
            imageList.size
        } else {
            viewType = NO_IMAGES_TYPE
            1
        }
    }
}