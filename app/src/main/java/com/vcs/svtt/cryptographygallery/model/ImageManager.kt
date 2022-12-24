package com.vcs.svtt.cryptographygallery.model

class ImageManager private constructor(){
    init {
        // define in constructor
    }

    private object Holder { val INSTANCE = ImageManager() }

    companion object {
        @JvmStatic
        fun getInstance(): ImageManager{
            return Holder.INSTANCE
        }
    }


    private var imageList: ArrayList<Image> = ArrayList()

    fun addAList(pathList: ArrayList<String>) {
        pathList.forEach {
            s ->
            val path = s.substring(s.indexOf("/"))
            val fileName = s.substring(s.lastIndexOf("/")+1)
            val image = Image(fileName,path)
            imageList.add(image)
        }
    }

    fun getList(): ArrayList<Image> {
        return imageList
    }

    fun clearList() {
        imageList.clear()
    }
}