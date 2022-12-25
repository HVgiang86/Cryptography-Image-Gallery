package com.vcs.svtt.cryptographygallery.cryptography.rsa

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.vcs.svtt.cryptographygallery.cryptography.CryptographyHelper
import java.io.*
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * This code appears to be a helper class for performing RSA and AES encryption/decryption on files.
 * It has several constants defined for file names and directory names, as well as various parameters such as key sizes and initialization vectors
 * encrypt data with aes then encrypt aes's key by rsa public key
 * decrypt aes's key with private key then decrypt data
 */
object RSAHelper {
    private var readyDataKey = false
    private var readyRSAKey = false
    private lateinit var key: SecretKey
    private lateinit var iv: ByteArray
    private lateinit var keyPair: KeyPair

    private const val TAG = "RSA TAG"
    private const val RSA_DIRECTORY = "RSA"
    private const val RSA_KEY_DIRECTORY = "RSA_KEY"
    private const val RSA_ENCRYPTED_FILE_PREFIX = "AES_encrypted_"
    private const val RSA_DECRYPTED_FILE_PREFIX = "AES_decrypted_"
    private const val ENCRYPTED_FILE_SUBFIX = ".dat"
    private const val RSA = "RSA"
    private const val AES = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    private const val RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    private const val DATA_KEY_FILE_PATH = "data_key.aes"
    private const val PRIVATE_KEY_FILE_PATH = "private_key.rsa"
    private const val PUBLIC_KEY_FILE_PATH = "public_key.rsa"
    private const val IV_FILE_PATH = "iv.aes"
    private const val EXPECTED_IV_LENGTH = 16
    private const val DATA_KEY_SIZE = 256
    private const val RSA_KEY_SIZE = 2048

    private fun initRSAKeyPair() {
        if (!readyRSAKey) {
            val directory = makeDirectory()
            val keyDirectory = File(directory, RSA_KEY_DIRECTORY)
            if (!keyDirectory.exists()) keyDirectory.mkdirs()
            else Log.d(TAG, "DIREC: ${keyDirectory.path}")

            if (keyDirectory.exists()) Log.d(TAG, "create dir successfully")
            Log.d(TAG, "DIREC: ${keyDirectory.path}")

            val publicFile = File(keyDirectory, PUBLIC_KEY_FILE_PATH)
            val privateFile = File(keyDirectory, PRIVATE_KEY_FILE_PATH)

            if (needToCreateNewRSAKeyPair(publicFile, privateFile)) {
                publicFile.createNewFile()
                privateFile.createNewFile()

                createNewRSAKeyPair()
                saveRSAKeyPairToFile(publicFile, privateFile)
                Log.d(TAG, "init rsa key pair")

            } else loadRSAKeyPairFromFile(publicFile, privateFile)

            readyRSAKey = true
        }
    }

    private fun needToCreateNewRSAKeyPair(publicFile: File, privateFile: File): Boolean {
        return (!publicFile.exists() || !privateFile.exists()) || publicFile.length() == (0).toLong() || privateFile.length() == (0).toLong()
    }

    private fun createNewRSAKeyPair() {
        val keyGen = KeyPairGenerator.getInstance(RSA)
        keyGen.initialize(RSA_KEY_SIZE)
        keyPair = keyGen.genKeyPair()
    }

    private fun saveRSAKeyPairToFile(publicFile: File, privateFile: File) {
        var bos: BufferedOutputStream

        var fos = FileOutputStream(publicFile)
        bos = BufferedOutputStream(fos)

        // Encode the keys in base64
        val publicKey = keyPair.public
        val publicKeyString: String = Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
        val privateKey = keyPair.private
        val privateKeyString: String = Base64.encodeToString(privateKey.encoded, Base64.DEFAULT)

        // Write the keys to each file
        bos.write(publicKeyString.toByteArray())
        bos.close()

        fos = FileOutputStream(privateFile)
        bos = BufferedOutputStream(fos)
        bos.write(privateKeyString.toByteArray())
        bos.close()
    }

    private fun loadRSAKeyPairFromFile(publicFile: File, privateFile: File) {
        var bis: BufferedInputStream
        var fis = FileInputStream(publicFile)

        bis = BufferedInputStream(fis)
        var keyBytes: ByteArray = ByteArray(bis.available())
        bis.read(keyBytes)
        val publicKeyBytes = Base64.decode(String(keyBytes), Base64.DEFAULT)
        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        bis.close()
        Log.d(TAG, "Public key loaded")

        fis = FileInputStream(privateFile)
        bis = BufferedInputStream(fis)
        keyBytes = ByteArray(bis.available())
        bis.read(keyBytes)
        val privateKeyBytes = Base64.decode(String(keyBytes), Base64.DEFAULT)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
        bis.close()
        Log.d(TAG, "loaded private key")

        val keyFactory = KeyFactory.getInstance(RSA)
        val publicKey = keyFactory.generatePublic(publicKeySpec)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        keyPair = KeyPair(publicKey, privateKey)
        Log.d(TAG, "Key pair matched")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initKeyAndIV() {
        if (!readyDataKey) {
            val directory = makeDirectory()
            val keyDirectory = File(directory, RSA_KEY_DIRECTORY)
            if (!keyDirectory.exists()) keyDirectory.mkdirs()
            else Log.d(TAG, "DIREC: ${keyDirectory.path}")

            if (keyDirectory.exists()) Log.d(TAG, "create dir successfully")
            Log.d(TAG, "DIREC: ${keyDirectory.path}")
            val keyFile = File(keyDirectory, DATA_KEY_FILE_PATH)
            val ivFile = File(keyDirectory, IV_FILE_PATH)

            if (needToCreateNewKeyAndIV(keyFile, ivFile)) {
                keyFile.createNewFile()
                ivFile.createNewFile()

                createNewKeyAndIV()
                encryptAESKey(keyFile, ivFile)
                Log.d(TAG, "init key and iv")

            } else {
                decryptAESKey(keyFile, ivFile)
                Log.d(TAG, "key and iv has loaded from file")
            }

            readyDataKey = true
        }
    }

    private fun needToCreateNewKeyAndIV(keyFile: File, ivFile: File): Boolean {
        return (!keyFile.exists() || !ivFile.exists()) || keyFile.length() == (0).toLong() || ivFile.length() == (EXPECTED_IV_LENGTH).toLong()
    }

    private fun createNewKeyAndIV() {
        val keygen = KeyGenerator.getInstance(AES)
        keygen.init(DATA_KEY_SIZE)
        key = keygen.generateKey()

        iv = ByteArray(EXPECTED_IV_LENGTH)
        SecureRandom().nextBytes(iv)
    }

    private fun makeDirectory(): File {
        val directory = File(CryptographyHelper.DIRECTORY, RSA_DIRECTORY)
        if (!directory.exists()) directory.mkdirs()
        else Log.d(TAG, "DIREC: ${directory.path}")
        Log.d(TAG, "DIREC: ${directory.path}")
        return directory
    }

    private fun encryptAESKey(keyFile: File, ivFile: File) {
        encryptWithRSAKey(keyFile, key.encoded)
        encryptWithRSAKey(ivFile, iv)
    }

    private fun decryptAESKey(keyFile: File, ivFile: File) {
        key = SecretKeySpec(decryptWithRSAKey(keyFile), AES)
        iv = decryptWithRSAKey(ivFile)
    }

    private fun encryptWithRSAKey(file: File, data: ByteArray) {
        val fos = FileOutputStream(file)
        val bos = BufferedOutputStream(fos)

        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        val encryptedBytes = cipher.doFinal(data)
        bos.write(encryptedBytes)
        bos.close()
    }

    private fun decryptWithRSAKey(file: File): ByteArray {
        val fis = FileInputStream(file)
        val bis = BufferedInputStream(fis)
        val dataBytes = ByteArray(bis.available())
        bis.read(dataBytes)

        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private)
        val decryptedBytes = cipher.doFinal(dataBytes)
        bis.close()
        return decryptedBytes
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun encrypt(rawFile: File): String {
        initRSAKeyPair()
        initKeyAndIV()

        Log.d(TAG, "encrypting")

        val directory = makeDirectory()
        if (!rawFile.isFile) return ""

        //calculate file name
        val rawFileName = rawFile.name
        //add prefix AES_encrypted_ and ".dat" sub-fix
        val encryptedFileName = RSA_ENCRYPTED_FILE_PREFIX + rawFileName + ENCRYPTED_FILE_SUBFIX

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
        return RSA_DECRYPTED_FILE_PREFIX + s
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun decrypt(encryptedFile: File): String {
        initRSAKeyPair()
        initKeyAndIV()

        Log.d(TAG, "Decrypting")

        val directory = makeDirectory()
        if (!encryptedFile.isFile) return ""

        val encryptedFileName = encryptedFile.name
        val decryptedFileName = calculateDecryptedFileName(encryptedFileName)

        Log.d(
            TAG, "Encrypted FILE NAME: $encryptedFileName\nDecrypted File name: $decryptedFileName"
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