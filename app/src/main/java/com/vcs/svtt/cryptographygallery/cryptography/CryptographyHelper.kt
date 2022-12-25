package com.vcs.svtt.cryptographygallery.cryptography

import android.os.Build
import androidx.annotation.RequiresApi
import com.vcs.svtt.cryptographygallery.cryptography.AES.AESHelper
import com.vcs.svtt.cryptographygallery.cryptography.DES.DESHelper
import com.vcs.svtt.cryptographygallery.cryptography.hash.HashHelper
import com.vcs.svtt.cryptographygallery.cryptography.rsa.RSAHelper
import com.vcs.svtt.cryptographygallery.model.Image
import java.io.File

object CryptographyHelper {
    const val DIRECTORY = "/storage/emulated/0/Cryptography gallery"

    /**
     * Define integer constants for each algorithm. Each algorithm is represented by
     * a set of 5 bits. The OR of algorithm codes will let the program know which
     * algorithms have been selected.
     */
    const val AES = 31 //0000 0000 0000 0000 0000 0000 0001 1111
    const val DES = 992 //0000 0000 0000 0000 0000 0011 1110 0000
    const val RSA = 31744 //0000 0000 0000 0000 0111 1100 0000 0000
    const val MD5 = 1015808 //0000 0000 0000 1111 1000 0000 0000 0000
    const val SHA1 = 32505856 //0000 0001 1111 0000 0000 0000 0000 0000
    const val SHA512 = 1040187392 //0011 1110 0000 0000 0000 0000 0000 0000

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun encrypt(image: Image, vararg algorithm: Int){
        for (i in 0.until(algorithm.size)) {
            when (algorithm[i]) {
                AES -> encryptByAES(image)
                DES -> encryptByDES(image)
                RSA -> encryptByRSA(image)
                MD5 -> encryptByMD5(image)
                SHA1 -> encryptBySHA1(image)
                SHA512 -> encryptBySHA512(image)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun encrypt(image: Image, a: Int){
        // Use bitwise AND and equality check to see if the algorithm has been selected
        if (a and AES == AES) encryptByAES(image)
        if (a and RSA == RSA) encryptByRSA(image)
        if (a and DES == DES) encryptByDES(image)
        if (a and MD5 == MD5) encryptByMD5(image)
        if (a and SHA1 == SHA1) encryptBySHA1(image)
        if (a and SHA512 == SHA512) encryptBySHA512(image)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun encryptByAES(image: Image) {
        val filePath = image.rawFilePath
        image.setAES(AESHelper.encrypt(File(filePath)))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun encryptByDES(image: Image) {
        val filePath = image.rawFilePath
        image.setAES(DESHelper.encrypt(File(filePath)))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun encryptByRSA(image: Image) {
        val filePath = image.rawFilePath
        image.setRSA(RSAHelper.encrypt(File(filePath)))
    }

    private fun encryptByMD5(image: Image) {
        image.setMD5(HashHelper.hashMD5(File(image.rawFilePath)))
    }

    private fun encryptBySHA1(image: Image) {
        image.setSHA1(HashHelper.hashSHA1(File(image.rawFilePath)))
    }

    private fun encryptBySHA512(image: Image) {
        image.setSHA512(HashHelper.hashSHA512(File(image.rawFilePath)))
    }


}