package com.vcs.svtt.cryptographygallery.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class EncryptedImagePreferences(context: Context) {
    companion object {
        private const val PREFS_FILE = "cryptography_gallery_encrypted_images_preferences"
        private const val PREFS_MODE = Context.MODE_PRIVATE
        private const val STORE_KEY = "encrypted_images"
        private const val FILE_PATH_KEY = "file_path"
        private const val AES_KEY = "aes"
        private const val DES_KEY = "des"
        private const val RSA_KEY = "rsa"
        private const val MD5_KEY = "md5"
        private const val SHA1_KEY = "sha-1"
        private const val SHA512_KEY = "sha-512"

        private const val TAG = "SHARED_PREFERENCES"
    }

    private var prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_FILE, PREFS_MODE)
    }

    fun load() {
        val manager = ImageManager.getInstance()
        manager.clearList()

        val s = prefs.getString(STORE_KEY, "")
        Log.d(TAG, "json: $s")

        if (s!!.isNotEmpty()) {
            val jsonArray = JSONArray(s)

            for (i in 0.until(jsonArray.length())) {
                val jsonObject = jsonArray.getJSONObject(i)
                val path = jsonObject.getString(FILE_PATH_KEY)
                if (path.isNotEmpty()) {
                    val file = File(path)
                    val image = Image(file.name, path)

                    val aes = jsonObject.getString(AES_KEY)
                    if (aes.isNotEmpty()) {
                        image.setAES(aes)
                        if (!File(image.aesFilePath).exists()) continue
                    }

                    val des = jsonObject.getString(DES_KEY)
                    if (des.isNotEmpty()) {
                        image.setDES(des)
                        if (!File(image.desFilePath).exists()) continue
                    }

                    val rsa = jsonObject.getString(RSA_KEY)
                    if (rsa.isNotEmpty()) {
                        image.setRSA(rsa)
                        if (!File(image.rsaFilePath).exists()) continue
                    }

                    val md5 = jsonObject.getString(MD5_KEY)
                    if (md5.isNotEmpty()) {
                        image.setMD5(md5)
                        if (!File(image.md5FilePath).exists()) continue
                    }

                    val sha1 = jsonObject.getString(SHA1_KEY)
                    if (sha1.isNotEmpty()) {
                        image.setSHA1(sha1)
                        if (!File(image.sha1FilePath).exists()) continue
                    }

                    val sha512 = jsonObject.getString(SHA512_KEY)
                    if (sha512.isNotEmpty()) {
                        image.setSHA512(sha512)
                        if (!File(image.sha512FilePath).exists()) continue
                    }

                    manager.addImage(image)
                }
            }
        }

        //update storage
        save()
    }

    fun save() {
        val manager = ImageManager.getInstance()

        val editor = prefs.edit()
        val list = manager.getList()
        val jsonArray = JSONArray()
        var index = 0
        list.forEach { image ->
            val jsonObject = JSONObject()
            jsonObject.put(FILE_PATH_KEY, image.rawFilePath)
            jsonObject.put(AES_KEY, image.aesFilePath)
            jsonObject.put(DES_KEY, image.desFilePath)
            jsonObject.put(RSA_KEY, image.rsaFilePath)
            jsonObject.put(MD5_KEY, image.md5FilePath)
            jsonObject.put(SHA1_KEY, image.sha1FilePath)
            jsonObject.put(SHA512_KEY, image.sha512FilePath)
            jsonArray.put(index++, jsonObject)
        }

        editor.putString(STORE_KEY, jsonArray.toString())
        editor.apply()
        Log.d(TAG, "Saved to shared preferences")
    }
}