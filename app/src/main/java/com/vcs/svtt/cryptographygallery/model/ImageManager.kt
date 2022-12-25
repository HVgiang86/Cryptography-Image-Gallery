package com.vcs.svtt.cryptographygallery.model

class ImageManager private constructor(){
    private var imageList: ArrayList<Image> = ArrayList()

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




    fun addAList(pathList: ArrayList<String>) {
        pathList.forEach {
            s ->
            val path = s.substring(s.indexOf("/"))
            val fileName = s.substring(s.lastIndexOf("/")+1)
            val image = Image(fileName,path)
            imageList.add(image)
        }
    }

    fun addImage(image: Image) {
        imageList.add(image)
    }

    fun getList(): ArrayList<Image> {
        return imageList
    }

    fun clearList() {
        imageList.clear()
    }

    fun removeImage(position: Int) {
        imageList.removeAt(position)
    }
}