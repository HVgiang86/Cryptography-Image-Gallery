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

/**
 * The AESHelper object provides methods for encrypting and decrypting files using the AES (Advanced Encryption Standard) algorithm.
 * The encrypt and decrypt methods used to encrypt and decrypt file with AES algorithm
 */
object AESHelper {
    // Flag to check if the key and IV have been initialized
    private var ready = false

    // Secret key and initialization vector for AES encryption
    private lateinit var key: SecretKey
    private lateinit var iv: ByteArray

    // Constants for file paths and encryption details
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
    // Initialize the key and IV by either creating new ones or loading them from a file
    private fun initKeyAndIV() {
        if (!ready) {
            // Create the necessary directories and files if they don't already exist
            val directory = makeDirectory()
            val keyDirectory = File(directory, AES_KEY_DIRECTORY)
            if (!keyDirectory.exists()) keyDirectory.mkdirs()
            else Log.d(TAG, "DIRECT: ${keyDirectory.path}")

            if (keyDirectory.exists()) Log.d(TAG, "create dir successfully")
            Log.d(TAG, "DIRECT: ${keyDirectory.path}")
            val keyFile = File(keyDirectory, KEY_FILE_PATH)
            val ivFile = File(keyDirectory, IV_FILE_PATH)

            // Check if new key and IV need to be created
            if (needToCreateNewKeyAndIV(keyFile, ivFile)) {
                keyFile.createNewFile()
                ivFile.createNewFile()

                // Generate new key and IV
                createNewKeyAndIV()
                // Save the new key and IV to the file
                saveKeyAndIVToFile(keyFile, ivFile)
                Log.d(TAG, "init key and iv")

            } else {
                // Load the key and IV from the file
                loadKeyAndIVFromFile(keyFile, ivFile)
                Log.d(TAG, "key and iv has loaded from file")
            }

            ready = true
        }
    }

    // Check if new key and IV need to be created
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

    // Save the key and IV to the specified files
    private fun saveKeyAndIVToFile(keyFile: File, ivFile: File) {
        // BufferedOutputStream for writing to the file
        var bos: BufferedOutputStream

        // Write the key to the key file
        var fos = FileOutputStream(keyFile)
        bos = BufferedOutputStream(fos)
        // Get the encoded bytes of the key and write them to the file
        bos.write(key.encoded)
        bos.close()

        // Write the IV to the IV file
        fos = FileOutputStream(ivFile)
        bos = BufferedOutputStream(fos)
        // Write the IV to the file
        bos.write(iv)
        bos.close()
    }

    // Load the key and IV from the specified files
    private fun loadKeyAndIVFromFile(keyFile: File, ivFile: File) {
        // BufferedInputStream for reading from the file
        var bis: BufferedInputStream
        // Read the key from the key file
        var fis = FileInputStream(keyFile)
        bis = BufferedInputStream(fis)
        // Read the bytes of the key from the file
        val keyByte = ByteArray(bis.available())
        bis.read(keyByte)
        // Create a SecretKeySpec using the key bytes and the algorithm "AES"
        key = SecretKeySpec(keyByte, 0, keyByte.size, AES)
        bis.close()

        // Read the IV from the IV file
        fis = FileInputStream(ivFile)
        bis = BufferedInputStream(fis)
        // Read the bytes of the IV from the file
        iv = ByteArray(bis.available())
        bis.read(iv)
        bis.close()
        Log.d(TAG, "load key and iv")
    }

    private fun makeDirectory(): File {
        val directory = File(CryptographyHelper.DIRECTORY, AES_DIRECTORY)
        if (!directory.exists()) directory.mkdirs()
        else Log.d(TAG, "DIRECT: ${directory.path}")
        Log.d(TAG, "DIRECT: ${directory.path}")
        return directory
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun encrypt(rawFile: File): String {
        // Initialize the key and IV
        initKeyAndIV()

        Log.d(TAG, "encrypting")
        // Create the necessary directories if they don't already exist
        val directory = makeDirectory()
        // Return an empty string if the input is not a file
        if (!rawFile.isFile) return ""

        // Calculate the name of the encrypted file
        val rawFileName = rawFile.name
        // Add the prefix "AES_encrypted_" and the ".dat" suffix
        val encryptedFileName = AES_ENCRYPTED_FILE_PREFIX + rawFileName + ENCRYPTED_FILE_SUBFIX
        val encryptedFile = File(directory, encryptedFileName)
        // Create the encrypted file if it doesn't already exist
        if (!encryptedFile.exists()) encryptedFile.createNewFile()

        Log.d(TAG, "all files are exist or created!")

        // Read the raw data from the input file
        val fis = FileInputStream(rawFile)
        val bis = BufferedInputStream(fis)
        // Write the encrypted data to the output file
        val fos = FileOutputStream(encryptedFile)
        val bos = BufferedOutputStream(fos)

        val rawData = ByteArray(bis.available())
        bis.read(rawData)
        bis.close()

        // Initialize the Cipher object for encryption
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))

        // Encrypt the data
        val encryptedData = cipher.doFinal(rawData)
        bos.write(encryptedData)
        bos.close()
        Log.d(TAG, "encrypted. Byte length: ${encryptedData.size}")

        // Return
        return encryptedFile.path
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun decrypt(encryptedFile: File): String {
        // Initialize the key and IV
        initKeyAndIV()

        Log.d(TAG, "Decrypting")

        // Create the necessary directories if they don't already exist
        val directory = makeDirectory()
        // Return an empty string if the input is not a file
        if (!encryptedFile.isFile) return ""

        // Calculate the name of the decrypted file
        val encryptedFileName = encryptedFile.name
        val decryptedFileName = calculateDecryptedFileName(encryptedFileName)

        Log.d(
            TAG, "Encrypted FILE NAME: $encryptedFileName\nDecrypted File name: $decryptedFileName"
        )

        // Create the decrypted file if it doesn't already exist
        val decryptedFile = File(directory, decryptedFileName)
        if (!decryptedFile.exists()) decryptedFile.createNewFile()
        Log.d(TAG, "all files are exist or created!")

        // Read the encrypted data from the input file
        val fis = FileInputStream(encryptedFile)
        val bis = BufferedInputStream(fis)
        // Write the decrypted data to the output file
        val fos = FileOutputStream(decryptedFile)
        val bos = BufferedOutputStream(fos)

        val rawData = ByteArray(bis.available())
        bis.read(rawData)
        bis.close()

        // Initialize the Cipher object for decryption
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

        // Decrypt the encrypted data
        val encryptedData = cipher.doFinal(rawData)

        // Write the decrypted data to the output file
        bos.write(encryptedData)
        bos.close()

        Log.d(TAG, "Decrypted. Size: ${encryptedData.size}")
        // Return the path of the decrypted file
        return decryptedFile.path
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

    /*private fun parseFileExt(path: String): String {
        val s: String
        val i = path.lastIndexOf(".")
        s = path.substring(i + 1)
        return s
    }*/

}