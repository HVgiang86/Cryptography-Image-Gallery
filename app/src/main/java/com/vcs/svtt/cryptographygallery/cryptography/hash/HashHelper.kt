package com.vcs.svtt.cryptographygallery.cryptography.hash

import android.util.Log
import com.vcs.svtt.cryptographygallery.cryptography.CryptographyHelper
import java.io.*
import java.security.MessageDigest

object HashHelper {
    private const val TAG = "HASH TAG"

    private const val MD5_DIRECTORY = "MD5"
    private const val SHA1_DIRECTORY = "SHA1"
    private const val SHA512_DIRECTORY = "SHA512"

    private const val MD5_PREFIX = "MD5_hashed_"
    private const val SHA1_PREFIX = "SHA-1_hashed_"
    private const val SHA512_PREFIX = "SHA-512_hashed_"

    private const val FILE_SUBFIX = ".txt"

    private const val MD5 = "MD5"
    private const val SHA1 = "SHA-1"
    private const val SHA512 = "SHA-512"

    private fun createFile(file: File, function: String): File {
        var directory = File(CryptographyHelper.DIRECTORY)
        var fileName = ""
        when (function) {
            MD5 -> {
                directory = makeDirectory(MD5)
                fileName = MD5_PREFIX + file.name + FILE_SUBFIX

            }

            SHA1 -> {
                directory = makeDirectory(SHA1)
                fileName = SHA1_PREFIX + file.name + FILE_SUBFIX
            }

            SHA512 -> {
                directory = makeDirectory(SHA512)
                fileName = SHA512_PREFIX + file.name + FILE_SUBFIX
            }
        }

        val hashedFile = File(directory, fileName)
        if (!hashedFile.exists()) hashedFile.createNewFile()
        return hashedFile
    }

    fun hashMD5(file: File): String {
        val hashedFile = createFile(file, MD5)
        val data = getFileData(file)
        val md = MessageDigest.getInstance(MD5)
        val output = md.digest(data)
        writeHashedToFile(hashedFile, output)
        return hashedFile.path
    }

    fun hashSHA1(file: File): String {
        val hashedFile = createFile(file, SHA1)
        val data = getFileData(file)
        val md = MessageDigest.getInstance(SHA1)
        val output = md.digest(data)
        writeHashedToFile(hashedFile, output)
        return hashedFile.path
    }

    fun hashSHA512(file: File): String {
        val hashedFile = createFile(file, SHA512)
        val data = getFileData(file)
        val md = MessageDigest.getInstance(SHA512)
        val output = md.digest(data)
        writeHashedToFile(hashedFile, output)
        return hashedFile.path
    }

    private fun getFileData(file: File): ByteArray {
        val fis = FileInputStream(file)
        val bis = BufferedInputStream(fis)

        val bytes = ByteArray(bis.available())
        bis.read(bytes)
        bis.close()
        return bytes
    }

    private fun writeHashedToFile(file: File, data: ByteArray) {
        val fos = FileOutputStream(file)
        val bos = BufferedOutputStream(fos)
        bos.write(data)
        bos.close()
    }

    private fun makeDirectory(function: String): File {
        var directory = File(CryptographyHelper.DIRECTORY)
        when (function) {
            MD5 -> directory = File(CryptographyHelper.DIRECTORY, MD5_DIRECTORY)
            SHA1 -> directory = File(CryptographyHelper.DIRECTORY, SHA1_DIRECTORY)
            SHA512 -> directory = File(CryptographyHelper.DIRECTORY, SHA512_DIRECTORY)
        }

        if (!directory.exists()) directory.mkdirs()
        else Log.d(TAG, "DIRECT: ${directory.path}")
        Log.d(TAG, "DIRECT: ${directory.path}")
        return directory
    }
}