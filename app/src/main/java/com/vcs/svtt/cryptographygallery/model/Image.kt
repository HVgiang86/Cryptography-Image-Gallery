package com.vcs.svtt.cryptographygallery.model

data class Image(val fileName: String, val rawFilePath: String) {
    private var aes = false
    private var des = false
    private var rsa = false
    private var md5 = false
    private var sha1 = false
    private var sha521 = false
    private var aesFilePath = ""
    private var desFilePath = ""
    private var rsaFilePath = ""
    private var md5FilePath = ""
    private var sha1FilePath = ""
    private var sha521FilePath = ""
}