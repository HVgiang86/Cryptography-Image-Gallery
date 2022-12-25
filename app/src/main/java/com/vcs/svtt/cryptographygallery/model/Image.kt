package com.vcs.svtt.cryptographygallery.model

data class Image(val fileName: String, val rawFilePath: String) {
    var aes = false
    var des = false
    var rsa = false
    var md5 = false
    var sha1 = false
    var sha512 = false
    var aesFilePath = ""
    var desFilePath = ""
    var rsaFilePath = ""
    var md5FilePath = ""
    var sha1FilePath = ""
    var sha512FilePath = ""

    fun setAES(encryptedFilePath: String) {
        aes = true
        aesFilePath = encryptedFilePath
    }

    fun setDES(encryptedFilePath: String) {
        des = true
        desFilePath = encryptedFilePath
    }

    fun setRSA(encryptedFilePath: String) {
        rsa = true
        rsaFilePath = encryptedFilePath
    }

    fun setMD5(encryptedFilePath: String) {
        md5 = true
        md5FilePath = encryptedFilePath
    }

    fun setSHA1(encryptedFilePath: String) {
        sha1 = true
        sha1FilePath = encryptedFilePath
    }

    fun setSHA512(encryptedFilePath: String) {
        sha512 = true
        sha512FilePath = encryptedFilePath
    }
}