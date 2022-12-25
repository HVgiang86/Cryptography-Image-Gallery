package com.vcs.svtt.cryptographygallery.model

data class Image(val fileName: String, val rawFilePath: String) {
    private var aes = false
    private var des = false
    private var rsa = false
    private var md5 = false
    private var sha1 = false
    private var SHA512 = false
    var aesFilePath = ""
    var desFilePath = ""
    var rsaFilePath = ""
    var md5FilePath = ""
    var sha1FilePath = ""
    var SHA512FilePath = ""

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
        SHA512 = true
        SHA512FilePath = encryptedFilePath
    }
}