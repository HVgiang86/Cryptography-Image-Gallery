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
            var path = s.substring(s.indexOf("/"))
            var fileName = s.substring(s.lastIndexOf("/")+1)
            var image = Image(fileName,path,false,"")
            imageList.add(image)
        }
    }

    fun getList(): ArrayList<Image> {
        return imageList
    }
}