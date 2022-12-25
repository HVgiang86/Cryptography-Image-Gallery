package com.vcs.svtt.cryptographygallery.cryptography.AES

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.vcs.svtt.cryptographygallery.cryptography.CryptographyHelper
import java.io.*
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESHelper {
    private var ready = false
    private lateinit var key: SecretKey
    private lateinit var iv: ByteArray

    private const val TAG = "AES TAG"
    private const val AES_DIRECTORY = "AES"
    private const val AES_KEY_DIRECTORY = "AES_KEY"
    private const val AES_ENCRYPTED_FILE_PREFIX = "AES_encrypted_"
    private const val AES_DECRYPTED_FILE_PREFIX = "AES_decrypted_"
    private const val ENCRYPTED_FILE_SUBFIX = ".dat"
    private const val AES = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val KEY_FILE_PATH = "AES_key.aes"
    private const val IV_FILE_PATH = "AES_iv.aes"
    private const val EXPECTED_IV_LENGTH = 16
    private const val KEY_SIZE = 256

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initKeyAndIV() {
        if (!ready) {
            val directory = makeDirectory()
            val keyDirectory = File(directory, AES_KEY_DIRECTORY)
            if (!keyDirectory.exists()) keyDirectory.mkdirs()
            else Log.d(TAG, "DIREC: ${keyDirectory.path}")

            if (keyDirectory.exists()) Log.d(TAG, "create dir successfully")
            Log.d(TAG, "DIREC: ${keyDirectory.path}")
            val keyFile = File(keyDirectory, KEY_FILE_PATH)
            val ivFile = File(keyDirectory, IV_FILE_PATH)

            if (needToCreateNewKeyAndIV(keyFile, ivFile)) {
                keyFile.createNewFile()
                ivFile.createNewFile()

                createNewKeyAndIV()
                saveKeyAndIVToFile(keyFile, ivFile)
                Log.d(TAG, "init key and iv")

            } else loadKeyAndIVFromFile(keyFile, ivFile)

            ready = true
        }
    }

    private fun needToCreateNewKeyAndIV(keyFile: File, ivFile: File): Boolean {
        return (!keyFile.exists() || !ivFile.exists()) || keyFile.length() == (0).toLong() || ivFile.length() == (EXPECTED_IV_LENGTH).toLong()
    }

    private fun createNewKeyAndIV() {
        val keygen = KeyGenerator.getInstance(AES)
        keygen.init(KEY_SIZE)
        key = keygen.generateKey()

        iv = ByteArray(EXPECTED_IV_LENGTH)
        SecureRandom().nextBytes(iv)
    }

    private fun saveKeyAndIVToFile(keyFile: File, ivFile: File) {
        var bos: BufferedOutputStream

        var fos = FileOutputStream(keyFile)
        bos = BufferedOutputStream(fos)
        bos.write(key.encoded)
        bos.close()

        fos = FileOutputStream(ivFile)
        bos = BufferedOutputStream(fos)
        bos.write(iv)
        bos.close()
    }

    private fun loadKeyAndIVFromFile(keyFile: File, ivFile: File) {
        var bis: BufferedInputStream
        var fis = FileInputStream(keyFile)

        bis = BufferedInputStream(fis)
        val keyByte = ByteArray(bis.available())
        bis.read(keyByte)
        key = SecretKeySpec(keyByte, 0, keyByte.size, AES)
        bis.close()

        fis = FileInputStream(ivFile)
        bis = BufferedInputStream(fis)
        iv = ByteArray(bis.available())
        bis.read(iv)
        bis.close()
        Log.d(TAG, "load key and iv")
    }

    private fun makeDirectory(): File {
        val directory = File(CryptographyHelper.DIRECTORY, AES_DIRECTORY)
        if (!directory.exists()) directory.mkdirs()
        else Log.d(TAG, "DIREC: ${directory.path}")
        Log.d(TAG, "DIREC: ${directory.path}")
        return directory
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun encrypt(rawFile: File): String {
        initKeyAndIV()

        Log.d(TAG, "encrypting")

        val directory = makeDirectory()
        if (!rawFile.isFile) return ""

        //calculate file name
        val rawFileName = rawFile.name
        //add prefix AES_encrypted_ and ".dat" sub-fix
        val encryptedFileName = AES_ENCRYPTED_FILE_PREFIX + rawFileName + ENCRYPTED_FILE_SUBFIX

        val encryptedFile = File(directory, encryptedFileName)
        if (!encryptedFile.exists()) encryptedFile.createNewFile()

        Log.d(TAG, "all files are exist or created!")

        val fis = FileInputStream(rawFile)
        val bis = BufferedInputStream(fis)
        val fos = FileOutputStream(encryptedFile)
        val bos = BufferedOutputStream(fos)

        val rawData = ByteArray(bis.available())
        bis.read(rawData)
        bis.close()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))

        val encryptedData = cipher.doFinal(rawData)
        bos.write(encryptedData)
        bos.close()
        Log.d(TAG, "encrypted. Byte length: ${encryptedData.size}")

        return encryptedFile.path
    }

    private fun calculateDecryptedFileName(encryptedFileName: String): String {
        //remove prefix AES_encrypted_
        var s = encryptedFileName.substring(15)
        //remove sub-fix ".dat"
        val i = s.lastIndexOf(".")
        s = s.substring(0, i)
        //add AES_decrypted_ prefix
        return AES_DECRYPTED_FILE_PREFIX + s
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun decrypt(encryptedFile: File): String {
        initKeyAndIV()

        Log.d(TAG, "Decrypting")

        val directory = makeDirectory()
        if (!encryptedFile.isFile) return ""

        val encryptedFileName = encryptedFile.name
        val decryptedFileName = calculateDecryptedFileName(encryptedFileName)

        Log.d(
            TAG,
            "Encrypted FILE NAME: $encryptedFileName\nDecrypted File name: $decryptedFileName"
        )


        val decryptedFile = File(directory, decryptedFileName)
        if (!decryptedFile.exists()) decryptedFile.createNewFile()
        Log.d(TAG, "all files are exist or created!")

        val fis = FileInputStream(encryptedFile)
        val bis = BufferedInputStream(fis)
        val fos = FileOutputStream(decryptedFile)
        val bos = BufferedOutputStream(fos)

        val rawData = ByteArray(bis.available())
        bis.read(rawData)
        bis.close()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

        val encryptedData = cipher.doFinal(rawData)

        bos.write(encryptedData)
        bos.close()

        Log.d(TAG, "Decrypted. Size: ${encryptedData.size}")
        return decryptedFile.path
    }

    private fun parseFileExt(path: String): String {
        val s: String
        val i = path.lastIndexOf(".")
        s = path.substring(i + 1)
        return s
    }

}